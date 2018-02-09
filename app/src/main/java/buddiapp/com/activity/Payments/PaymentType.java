package buddiapp.com.activity.Payments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.SessionReady;
import buddiapp.com.adapter.CardListAdapter;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;


public class PaymentType extends AppCompatActivity {
    LinearLayout root, promocodeLayout;
    CardView addPayment, promocodeView; //credit_card
String defaultCard_id = "";
    final int REQUEST_CODE = 1;
    TextView credit_card_text, applyPromo, promoText;
    EditText promocode;
//    ImageView payment_image;
    Button done;
    ListView cardList;
    CardListAdapter cardListAdapter;
    ImageView validImage;
    int cardAddResultCode = 304;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_type);

        root = (LinearLayout) findViewById(R.id.root);
        applyPromo = (TextView) findViewById(R.id.applyPromo);
        promocode = (EditText) findViewById(R.id.promocode);
        addPayment = (CardView) findViewById(R.id.addPayment);
//        credit_card = (CardView) findViewById(R.id.credit_card);

        promocodeView = (CardView) findViewById(R.id.promocode_view);
//        payment_image = (ImageView) findViewById(R.id.payment_image);
        done = (Button) findViewById(R.id.done);
//        credit_card_text = (TextView) findViewById(R.id.credit_card_text);
        promocodeLayout = findViewById(R.id.promocode_layout);
        promoText = (TextView) findViewById(R.id.promocode_text);
        cardList = findViewById(R.id.card_list_view);
        validImage = findViewById(R.id.valid_image);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Payments");

        if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals("trainer")){
            promocodeLayout.setVisibility(View.GONE);
        }else{
            promocodeLayout.setVisibility(View.VISIBLE);
        }

        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (promocode.getText().toString().trim().length() > 1) {
                    if(CommonCall.isNetworkAvailable()){
                        applyPromo.setEnabled(false);
                    new applyPromo().execute();
                    }else{
                        Toast.makeText(PaymentType.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        showPromocode();

//        CommonCall.showLoader(PaymentType.this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       /*         if(getIntent().hasExtra("result")){

    *//**ForResult
                 *//*
    *
    * going back to map screen
    *
    * *//*
                    setResult(RESULT_OK,new Intent());
                    finish();
                }*/
                onBackPressed();
            }
        });
    }

    void showPromocode() {
    if(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "").length() > 0 && (Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "0")) > 0 || PreferencesUtils.getData(Constants.promo_code_code, getApplicationContext(), "").length()>0 )){
        if(CommonCall.isNetworkAvailable()){
            new applyPromo().execute();

        }else{
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }else {

        promocodeView.setVisibility(View.GONE);

    }
    /*if (PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), "").length() > 0) {


        } else {

            promocodeView.setVisibility(View.GONE);

        }*/
    }

    class applyPromo extends AsyncTask<String, String, String> {
        JSONObject req = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            CommonCall.showLoader(PaymentType.this);

            try {
                req.put("user_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                if(promocode.getText().toString().length()>0) {
                    req.put("promocode", promocode.getText().toString());
                }else{
                    req.put("promocode", PreferencesUtils.getData(Constants.promo_code_code, getApplicationContext(), ""));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String res = NetworkCalls.POST(Urls.getapplyPromoCodeURL(), req.toString());

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            CommonCall.hideLoader();
            applyPromo.setEnabled(true);

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    promocode.setText("");
//                    Toast.makeText(PaymentType.this, "Promocode Applied Succesfully!", Toast.LENGTH_SHORT).show();


                    PreferencesUtils.saveData(Constants.promo_code, response.getJSONObject("data").getString("code"), getApplicationContext());
                    JSONObject jsonObject = response.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.promo_code_used_count, jsonObject.getString("limitOfUse"), getApplicationContext());
                    PreferencesUtils.saveData(Constants.promo_code_limit, jsonObject.getString("limitPerUser"), getApplicationContext());
                    PreferencesUtils.saveData(Constants.promo_code_code, jsonObject.getString("code"), getApplicationContext());

                    showCode(jsonObject.getString("codeStatus"));
                    if(jsonObject.getString("codeStatus").equals("expired")){
                        PreferencesUtils.saveData(Constants.promo_code, "", getApplicationContext());
                        PreferencesUtils.saveData(Constants.promo_code_used_count, "", getApplicationContext());
                        PreferencesUtils.saveData(Constants.promo_code_limit, "", getApplicationContext());
                        PreferencesUtils.saveData(Constants.promo_code_code, "", getApplicationContext());

                    }else {
                        if (getIntent().hasExtra("result")) {
//
////ForResult
//                /*
//*
//* going back to map screen
//*
//* */
//
                            setResult(RESULT_OK, new Intent());
                            finish();
                        }
                    }
                } else if (response.getInt(Constants.status) == 2) {
                    Toast.makeText(PaymentType.this, response.getString(Constants.message), Toast.LENGTH_SHORT).show();
                    PreferencesUtils.saveData(Constants.promo_code, "", getApplicationContext());
                    PreferencesUtils.saveData(Constants.promo_code_used_count, "", getApplicationContext());
                    PreferencesUtils.saveData(Constants.promo_code_limit, "", getApplicationContext());
                    PreferencesUtils.saveData(Constants.promo_code_code, "", getApplicationContext());

                    /*Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new applyPromo().execute();

                                }
                            });

                    snackbar.show();*/
                } else if (response.getInt(Constants.status) == 3) {

                    CommonCall.sessionout(getApplicationContext());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    public void addPayment(View view) {


        Intent stripeCard = new Intent(getApplicationContext(), AddPayment.class);
        startActivity(stripeCard);

//        DropInRequest dropInRequest = new DropInRequest()
//                .clientToken(mAuthorization);
//        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);

    }

//    @Override
//    protected void reset() {
//        CommonCall.PrintLog("resresetult", "reset reset");
//    }

//    @Override
//    protected void onAuthorizationFetched() {
//        CommonCall.PrintLog("onAuthorizationFetched", "onAuthorizationFetched onAuthorizationFetched");
//        try {
//            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
//
//            if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
//                DropInResult.fetchDropInResult(this, mAuthorization, this);
//            } else {
//                CommonCall.PrintLog("payment visible", "payment payment");
//            }
//        } catch (InvalidArgumentException e) {
//            showDialog(e.getMessage());
//        }
//
//
//    }


//    static ApiClient getApiClient(Context context) {
//
//        if (sApiClient == null) {
//            sApiClient = new RestAdapter.Builder()
//                    .setEndpoint(Urls.BASEURL)
////                    .setEndpoint("http://192.168.1.66:9002")
//                    .setRequestInterceptor(new ApiClientRequestInterceptor())
//                    .build()
//                    .create(ApiClient.class);
//        }
//
//        return sApiClient;
//    }

//    static void resetApiClient() {
//        sApiClient = null;
//    }

//    @Override
//    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//        return false;
//    }

//    @Override
//    public void onResult(DropInResult result) {
//        CommonCall.PrintLog("result", result.toString());
//
//CommonCall.hideLoader();
//        if (result.getPaymentMethodNonce() != null) {
//
//            displaycard(result);
//done.setVisibility(View.VISIBLE);
//
//            if(getIntent().hasExtra("result")){
//
////ForResult
//                /*
//*
//* going back to map screen
//*
//* */
//
//                setResult(RESULT_OK,new Intent());
//                finish();
//
//            }else{
//
////default
//            }
//
//
//        }else{
//            credit_card.setVisibility(View.GONE);
//        }
//
//
//    }


//    void displaycard(DropInResult result){
//
//        if (result.getPaymentMethodType()!=null) {
//
//
//            credit_card_text.setText(result.getPaymentMethodType().getCanonicalName()+" "+result.getPaymentMethodNonce().getDescription());
//
//
//            payment_image.setImageResource( result.getPaymentMethodType().getDrawable());
//
//        }
//
//
//
//    }


    @Override
    protected void onResume() {
        super.onResume();
        new getCards().execute();
        LocalBroadcastManager.getInstance(this).registerReceiver(refresh,
                new IntentFilter("BUDDI_TRAINER_SESSION_FINISH"));

//        try {
//            if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
//                DropInResult.fetchDropInResult(this, mAuthorization, this);
//
//            } else {
//                CommonCall.PrintLog("no payment method ", "no payment method found");
//            }
//        } catch (InvalidArgumentException e) {
//            e.printStackTrace();
//
//        }
    }


    private BroadcastReceiver refresh = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //write your activity starting code here
            Intent intentB = new Intent(getApplicationContext(), SessionReady.class);
            startActivity(intentB);
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refresh);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refresh);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
//                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
//                String stringNonce = nonce.getNonce();
//                Log.d("mylog", "Result: " + stringNonce);
//                displaycard(result);
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                // the user canceled
//                Log.d("mylog", "user cancelled");
//                CommonCall.hideLoader();
//            } else {
//                // handle errors here, an exception may be available in
//                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
//                Log.d("mylog", "Error : " + error.toString());
//                CommonCall.hideLoader();
//            }
//        }
//
//    }

    public void showCode(String codeStatus){
        if(codeStatus.equals("valid")){
            int limit = 0;
            if(Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "0"))>0 &&
                    Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_used_count, getApplicationContext(), "0"))==0)
            {
               limit = Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "0"));
            }else if(Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "0"))>0 &&
                    Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_used_count, getApplicationContext(), "0"))>0)
            {
                limit =Math.abs(Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_limit, getApplicationContext(), "0"))-
                        Integer.parseInt(PreferencesUtils.getData(Constants.promo_code_used_count, getApplicationContext(), "0")));
            }else{
                limit = 0;
            }
            validImage.setVisibility(View.VISIBLE);
        if(limit == 1){
            promoText.setText("Applied Promocode : " + PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), "")+"\n" +
                    " You have "+ limit +" use left");
            promocodeView.setVisibility(View.VISIBLE);
        }else if(limit>1 ){
            promoText.setText("Applied Promocode : " + PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), "")+"\n" +
                    " You have "+limit +" uses left");
            promocodeView.setVisibility(View.VISIBLE);
        }else {
            promoText.setText("Applied Promocode : " + PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), ""));
            promocodeView.setVisibility(View.VISIBLE);
        }
        if(getIntent().hasExtra("result")) {
//
////ForResult
//
//
// going back to map screen
//
//
//
            setResult(RESULT_OK, new Intent());
            finish();
        }
        }else if(codeStatus.equals("expired")){
//            promoText.setText("Applied Promocode : " + PreferencesUtils.getData(Constants.promo_code, getApplicationContext(), "")
//            + codeStatus);
            Toast.makeText(this, "Applied Promocode is expired!", Toast.LENGTH_SHORT).show();
            promocodeView.setVisibility(View.GONE);
            validImage.setVisibility(View.GONE);
            PreferencesUtils.saveData(Constants.promo_code, "", getApplicationContext());
            PreferencesUtils.saveData(Constants.promo_code_limit, "0", getApplicationContext());
            PreferencesUtils.saveData(Constants.promo_code_code, "", getApplicationContext());

        }

    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class getCards extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            CommonCall.showLoader(PaymentType.this,"Fetching now...");
        }

        @Override
        protected String doInBackground(String... strings) {


            String res = NetworkCalls.POST(Urls.getFindDefaultCardURL(), "");


            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);
                JSONObject sources = new JSONObject();
                JSONArray cards = new JSONArray();
                JSONObject jsonData = new JSONObject();

                if (response.getInt(Constants.status) == 1) {

                    if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainer)){
                        jsonData = response;
                        cards = response.getJSONArray("data");

                    }else if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainee))
                    {
                        jsonData = response.getJSONObject("data");
                        sources = jsonData.getJSONObject("sources");
                        cards = sources.getJSONArray("data");
                    }



        if (cards.length()==0){
//            if(credit_card.isShown()){
//                credit_card.setVisibility(View.GONE);
//            }
            PreferencesUtils.saveData(Constants.clientToken,"",getApplicationContext());
        }else{
            /*credit_card_text.setText(
            cards.getJSONObject(0).getString("brand")+" Ending with "+

                    cards.getJSONObject(0).getString("last4"));*/

            cardListAdapter = new CardListAdapter(jsonData, PaymentType.this);
            cardList.setAdapter(cardListAdapter);
            cardList.setScrollContainer(false);

PreferencesUtils.saveData(Constants.clientToken,"cardsaved",getApplicationContext());
//    addPayment.setVisibility(View.GONE);

    if(getIntent().hasExtra("from")) {
        if(getIntent().getExtras().getString("from").equals("addCard")){
//
////For Add card Result //* going back to map screen
//
       setResult(cardAddResultCode, new Intent());
       finish();
        }
        else if(getIntent().getExtras().getString("from").equals("noActiveCard")){
            setResult(305, new Intent());
            finish();
        }
        else if(getIntent().getExtras().getString("from").equals("noCadError")){
            setResult(306, new Intent());
            finish();
        }
    }

    if(getIntent().hasExtra("result")) {
//
////ForResult //* going back to map screen
//
        setResult(RESULT_OK, new Intent());
        finish();
    }
}

                } else if (response.getInt(Constants.status) == 2) {

                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getCards().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {

                    CommonCall.sessionout(getApplicationContext());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class findDefaultCard extends AsyncTask<String, String, String> {
        String response1;
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(PaymentType.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            response1 = NetworkCalls.POST(Urls.getFindDefaultCardURL(),"");
            return response1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getInt("status")==1){
                    defaultCard_id = jsonObject.getString("default_source");
                }else if(jsonObject.getInt("status")==2){
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }else if(jsonObject.getInt("status")==3){
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
            }

        }
    }
}
