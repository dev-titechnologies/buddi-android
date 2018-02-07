
package buddiapp.com.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.Payments.PaymentType;
import buddiapp.com.services.LocationService;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;
import buddiapp.com.utils.androidslidr.Slidr;
import buddiapp.com.utils.slidetounlock.ISlideListener;
import buddiapp.com.utils.slidetounlock.SlideLayout;
import buddiapp.com.utils.slidetounlock.renderers.TranslateRenderer;
import buddiapp.com.utils.slidetounlock.sliders.Direction;
import buddiapp.com.utils.slidetounlock.sliders.VerticalSlider;


public class WalletActivity extends AppCompatActivity {
    TextView walletAmount, proceed, allTransaction;
    FrameLayout root;
    LinearLayout walletTrainee;
    LinearLayout walletTrainer;
    int zeroBalanceResultCode = 303,
    noActiveCardCode = 305;
    SlideLayout slider;
    View arrow;
    Slidr slidr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Wallet");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);



        walletAmount = findViewById(R.id.wallet_amount);
        proceed = findViewById(R.id.proceed);
        allTransaction = findViewById(R.id.all_transaction);
        root = findViewById(R.id.root);
        walletTrainee = findViewById(R.id.wallet_trainee);
        walletTrainer = findViewById(R.id.wallet_trainer);
        arrow = findViewById(R.id.arrow);

        slider = findViewById(R.id.slider1);
        slider.setRenderer(new TranslateRenderer());
        slider.setSlider(new VerticalSlider(Direction.FORWARD));
        slider.setChildId(R.id.slide_child_3);
        slider.addSlideListener(new ISlideListener() {
            @Override
            public void onSlideChanged(SlideLayout slider, float percentage) {
                if(percentage>1){
                    arrow.setVisibility(View.GONE);
                    if(Float.parseFloat(PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0"))<1) {
                        Toast.makeText(WalletActivity.this, "No wallet balance to withdraw!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    arrow.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSlideDone(SlideLayout slider, boolean done) {
                if (done) {
                    // restore start state
                    if(Float.parseFloat(PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0"))>0){
                    new WithdrawWalletBalance().execute();
                    }else{
                        Toast.makeText(WalletActivity.this, "No wallet balance to withdraw!", Toast.LENGTH_SHORT).show();
                        slider.reset();
                    }
                }
            }
        });


        slidr = (Slidr) findViewById(R.id.slideure);
        slidr.setMax(1000);
//        slidr.addStep(new Slidr.Step("test", 250, Color.parseColor("#007E90"), Color.RED));
//        slidr.setTextMax("max\nvalue");
//        slidr.setCurrentValue(300);

        slidr.setTextFormatter(new Slidr.TextFormatter() {
            @Override
            public String format(float value) {
                return  "$"+" "+String.valueOf((int) value);
            }
        });

        slidr.setListener(new Slidr.Listener() {
            @Override
            public void valueChanged(Slidr slidr, float currentValue) {
                if(currentValue>0){
                    proceed.setBackgroundResource(R.color.colorPrimary);
                }else{
                    proceed.setBackgroundResource(R.color.ash);
                }
            }

            @Override
            public void bubbleClicked(Slidr slidr) {

            }
        });

        if(Float.parseFloat(PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0"))<1) {
            SlideLayout layout = findViewById(R.id.slider1);
// Gets the layout params that will allow you to resize the layout
            ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*

            float density = getResources().getDisplayMetrics().density;

                params.height = 420;
                params.width = params.WRAP_CONTENT;
                layout.setLayoutParams(params);
        }

        if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainee)){
            walletTrainer.setVisibility(View.GONE);
            walletTrainee.setVisibility(View.VISIBLE);
        }else{
            walletTrainer.setVisibility(View.VISIBLE);
            walletTrainee.setVisibility(View.GONE);
            blink();
        }

        new GetWalletBalance().execute();

        if(getIntent().hasExtra("amountRequired")){
            slidr.setCurrentValue(Float.parseFloat(getIntent().getExtras().getString("amountRequired")));
            proceed.setBackgroundResource(R.color.colorPrimary);
        }
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(slidr.getCurrentValue()>0 ){
                    new AddToWallet().execute();
                }
            }
        });

        allTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WalletTransactionHistory.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 305){
            if(Float.parseFloat(PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0"))>0){
                new WithdrawWalletBalance().execute();
            }else{
                Toast.makeText(WalletActivity.this, "No wallet balance to withdraw!", Toast.LENGTH_SHORT).show();
                slider.reset();
            }
        }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startAuto);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refresh);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startAuto);
    }

    private void blink(){
        /*final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 400;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(arrow.getVisibility() == View.VISIBLE){
                            arrow.setVisibility(View.INVISIBLE);
                        }else{
                            arrow.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();*/

        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, 250);
        tAnimation.setDuration(3000);
        tAnimation.setRepeatCount(200);
        tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        tAnimation.setFillAfter(true);
        tAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                slider.setVisibility(View.VISIBLE);
//                slider1.setVisibility(View.GONE);
            }
        });

        arrow.startAnimation(tAnimation);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }else{
            this.finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {
            startService(new Intent(getApplicationContext(), LocationService.class));
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(refresh,
                new IntentFilter("BUDDI_TRAINER_SESSION_FINISH"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                startAuto, new IntentFilter("BUDDI_TRAINER_START")
        );
    }

    BroadcastReceiver startAuto = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentB = new Intent(getApplicationContext(), SessionReady.class);
            startActivity(intentB);
            finish();
        }
    };

    public class AddToWallet extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(WalletActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                reqData.put("amount", slidr.getCurrentValue());

            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = NetworkCalls.POST(Urls.getAddToWalletURL(), reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {
                    Toast.makeText(getApplicationContext(),"Wallet recharged", Toast.LENGTH_SHORT).show();
                    slidr.setCurrentValue(0);
                    walletAmount.setText(String.format("%.2f",Float.parseFloat(response.getJSONObject("data").getString("walletBalance"))));
                    PreferencesUtils.saveData(Constants.wallet,String.format("%.2f",Float.parseFloat(response.getJSONObject("data").getString("walletBalance"))),getApplicationContext());
//                    wallet.setText("$"+String.format("%.2f",Float.parseFloat(response.getJSONObject("data").getString("walletBalance"))));

                    String walletString = "Wallet    $"+PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0");
//        menu.findItem(R.id.nav_wallet).setTitle("Wallet "+Html.fromHtml(String.format(Html.toHtml(new SpannedString(getResources().getText(R.string.pbs_setup_title))),
//                walletString)) );
                    CommonCall.setMenuTextColor(HomeActivity.menu,R.id.nav_wallet,walletString);


                    if(getIntent().hasExtra("from")){
                        if(getIntent().getStringExtra("from").equals("maptrainee")){
                            Intent intent = new Intent();
                            intent.putExtra("message",response.getString("message"));
                            setResult(zeroBalanceResultCode, intent);
                            finish();
                        }
                    }

                }else if (response.getInt(Constants.status) == 2) {
                    if(response.getString("message").equals("Cannot charge a customer that has no active card")){
                        @SuppressLint("RestrictedApi") final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(WalletActivity.this, R.style.cancelDialog));

                        builder.setMessage("You have not added your card details with buddi. Please add it in Payment method tab");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                            alert.dismiss();

                            }
                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.getInt(Constants.status) == 3) {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetWalletBalance extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(WalletActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {

            String response = NetworkCalls.POST(Urls.getWalletBalanceURL(), "");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    walletAmount.setText(String.format("%.2f",Float.parseFloat(response.getJSONObject("data").getString("walletBalance"))));
                    PreferencesUtils.saveData(Constants.wallet,String.format("%.2f",Float.parseFloat(response.getJSONObject("data").getString("walletBalance"))),getApplicationContext());

                    String walletString = "Wallet    $"+PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0");
                    CommonCall.setMenuTextColor(HomeActivity.menu,R.id.nav_wallet,walletString);

//                    if(Integer.parseInt(response.getJSONObject("data").getString("walletBalance"))>0){
//                        slider.setEnabled(true);
//                    }else{
//                        slider.setEnabled(false);
//                    }
                }else if (response.getInt(Constants.status) == 2) {

                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    new GetWalletBalance().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class WithdrawWalletBalance extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        CommonCall.showLoader(WalletActivity.this,"Processing your request...");
        }

        @Override
        protected String doInBackground(String... strings) {

            String response = NetworkCalls.POST(Urls.getWalletWithdrawalURL(), "");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                final JSONObject response = new JSONObject(s);
                CommonCall.hideLoader();
                if (response.getInt(Constants.status) == 1) {
                    Toast.makeText(WalletActivity.this, "Transaction success", Toast.LENGTH_SHORT).show();
                    JSONObject stripeObject = response.getJSONObject("data").getJSONObject("stripeResponse");
                    walletAmount.setText(stripeObject.getString("amount_reversed"));
                    PreferencesUtils.saveData(Constants.wallet,stripeObject.getString("amount_reversed"),getApplicationContext());

                    String walletString = "Wallet    $"+PreferencesUtils.getData(Constants.wallet,getApplicationContext(),"0");
                    CommonCall.setMenuTextColor(HomeActivity.menu,R.id.nav_wallet,walletString);


                    slider.reset();
                }else if (response.getInt(Constants.status) == 2) {
                    slider.reset();
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    if(response.getString("status_type").equals("NoActiveCard")){
                        Intent payment = new Intent(getApplicationContext(), PaymentType.class);
                        payment.putExtra("from", "noActiveCard");
                        startActivityForResult(payment, noActiveCardCode);

                    }
                } else if (response.getInt(Constants.status) == 3) {
                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getApplicationContext());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
