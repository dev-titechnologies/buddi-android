package buddyapp.com.activity.Payments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;

import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.ClientToken;
import com.braintreepayments.api.models.PaymentMethodNonce;


import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static buddyapp.com.R.id.root;


public class PaymentType extends BaseActivity implements PaymentMethodNonceCreatedListener,
        BraintreeCancelListener, BraintreeErrorListener, DropInResult.DropInResultListener {
LinearLayout root;
    CardView addPayment, credit_card,promocodeView;
    final int REQUEST_CODE = 1;
    TextView credit_card_text,applyPromo,promoText;
EditText promocode;
ImageView payment_image;
Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_type);


        root = (LinearLayout) findViewById(R.id.root);
        applyPromo = (TextView) findViewById(R.id.applyPromo);
        promocode = (EditText) findViewById(R.id.promocode);
        addPayment = (CardView) findViewById(R.id.addPayment);
        credit_card = (CardView) findViewById(R.id.credit_card);

        promocodeView = (CardView) findViewById(R.id.promocode_view);
        payment_image = (ImageView) findViewById(R.id.payment_image);
        done = (Button) findViewById(R.id.done);
        credit_card_text = (TextView) findViewById(R.id.credit_card_text);

        promoText = (TextView) findViewById(R.id.promocode_text);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        applyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (promocode.getText().toString().trim().length()>1){

                    new applyPromo().execute();
                }


            }
        });
        showPromocode();
CommonCall.showLoader(PaymentType.this);

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

void showPromocode(){


    if (PreferencesUtils.getData(Constants.promo_code,getApplicationContext(),"").length()>0) {
        promoText.setText("Applied Promocode : "+PreferencesUtils.getData(Constants.promo_code,getApplicationContext(),""));
promocodeView.setVisibility(View.VISIBLE);

    }else{

        promocodeView.setVisibility(View.GONE);

    }
}
    class applyPromo extends AsyncTask<String,String,String>{
        JSONObject req = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(PaymentType.this);

            try {
                req.put("user_id", PreferencesUtils.getData(Constants.user_id,getApplicationContext(),""));
                req.put("promocode",promocode.getText().toString() );



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String res = NetworkCalls.POST(Urls.getapplyPromoCodeURL(),req.toString());

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();


            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    promocode.setText("");
                    Toast.makeText(PaymentType.this, "Promocode Applied Succesfully!", Toast.LENGTH_SHORT).show();



              PreferencesUtils.saveData(Constants.promo_code,response.getJSONObject("data").getString("code"),getApplicationContext());

                    showPromocode();
                } else if (response.getInt(Constants.status) == 2) {

                    Snackbar snackbar = Snackbar
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

                    snackbar.show();
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
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(mAuthorization);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);

    }

    @Override
    protected void reset() {
        CommonCall.PrintLog("resresetult", "reset reset");
    }

    @Override
    protected void onAuthorizationFetched() {
        CommonCall.PrintLog("onAuthorizationFetched", "onAuthorizationFetched onAuthorizationFetched");
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);

            if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
                DropInResult.fetchDropInResult(this, mAuthorization, this);
            } else {
                CommonCall.PrintLog("payment visible", "payment payment");
            }
        } catch (InvalidArgumentException e) {
            showDialog(e.getMessage());
        }


    }


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

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public void onResult(DropInResult result) {
        CommonCall.PrintLog("result", result.toString());

CommonCall.hideLoader();
        if (result.getPaymentMethodNonce() != null) {

            displaycard(result);
done.setVisibility(View.VISIBLE);

            if(getIntent().hasExtra("result")){

//ForResult
                /*
*
* going back to map screen
*
* */

                setResult(RESULT_OK,new Intent());
                finish();

            }else{

//default
            }


        }else{
            credit_card.setVisibility(View.GONE);
        }


    }



    void displaycard(DropInResult result){

        if (result.getPaymentMethodType()!=null) {


            credit_card_text.setText(result.getPaymentMethodType().getCanonicalName()+" "+result.getPaymentMethodNonce().getDescription());


            payment_image.setImageResource( result.getPaymentMethodType().getDrawable());

        }



    }


    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (ClientToken.fromString(mAuthorization) instanceof ClientToken) {
                DropInResult.fetchDropInResult(this, mAuthorization, this);

            } else {
                CommonCall.PrintLog("no payment method ", "no payment method found");
            }
        } catch (InvalidArgumentException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                Log.d("mylog", "Result: " + stringNonce);
                displaycard(result);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
                Log.d("mylog", "user cancelled");
                CommonCall.hideLoader();
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                Log.d("mylog", "Error : " + error.toString());
                CommonCall.hideLoader();
            }
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

            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
