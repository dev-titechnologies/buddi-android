package buddiapp.com.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.utils.CircleImageView;
import buddiapp.com.utils.CommonCall;

public class WalletHistoryDetailActivity extends AppCompatActivity {
    ImageView imageView1, imageView2;
    TextView amount, status, date,from, to, transactionId,session, discription;
    LinearLayout transactionLayer, sessionLayer;
    JSONObject jsonObject;
    CircleImageView imageView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Transaction detail");

        imageView1 = findViewById(R.id.image_view1);
        imageView2 = findViewById(R.id.image_view2);
        imageView3 = findViewById(R.id.image_view3);
        amount = findViewById(R.id.amount);
        status = findViewById(R.id.status);
        date = findViewById(R.id.date);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        transactionId = findViewById(R.id.transaction_id);
        transactionLayer = findViewById(R.id.transaction_layer);
        sessionLayer = findViewById(R.id.session_layer);
        session = findViewById(R.id.session_name);
        discription = findViewById(R.id.discription);

        if(getIntent().hasExtra("dataArray")){
            try {
                jsonObject = new JSONObject(getIntent().getExtras().getString("dataArray"));
                date.setText(CommonCall.convertTime2(jsonObject.getString("date")).toUpperCase());

                if(jsonObject.getString("type").equals("stripe")){
                    discription.setText("Wallet recharged");
                    status.setText("Wallet recharge success");
                    transactionLayer.setVisibility(View.VISIBLE);
                    transactionId.setText(jsonObject.getString("transaction_id"));
                    if(jsonObject.getString("transaction_type").equals("Income")){
                        from.setText("Bank");
                        to.setText("Wallet");
                        imageView1.setImageResource(R.drawable.ic_bank_building_black);
                        imageView2.setImageResource(R.drawable.ic_wallet_black);
                        amount.setText("$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount"))));
                    }
                    sessionLayer.setVisibility(View.GONE);
                }else if(jsonObject.getString("type").equals("refund")){
                    discription.setText("Refunded to wallet");
                    status.setText("Refund success");
                    transactionLayer.setVisibility(View.GONE);
                        from.setText("Buddi");
                        to.setText("Wallet");
                        imageView1.setImageResource(R.mipmap.ic_launcher);
                        imageView2.setImageResource(R.drawable.ic_wallet_black);
                        amount.setText("$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount"))));
                        sessionLayer.setVisibility(View.VISIBLE);
                        session.setText(jsonObject.getString("session_duration")+" minutes "+
                        jsonObject.getString("session_name")+" session with "+ jsonObject.getString("trainer_name"));
                }else if(jsonObject.getString("type").equals("session")){
                    discription.setText("Paid to Buddi");
                    status.setText("Payment successful");
                    transactionLayer.setVisibility(View.GONE);
                    from.setText("Wallet");
                    to.setText("Buddi");
                    imageView1.setImageResource(R.drawable.ic_wallet_black);
                    imageView2.setImageResource(R.mipmap.ic_launcher);
                    amount.setText("$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount"))));
                    sessionLayer.setVisibility(View.VISIBLE);
                    session.setText(jsonObject.getString("session_duration")+" minutes "+
                            jsonObject.getString("session_name")+" session with "+ jsonObject.getString("trainer_name"));
                }else if(jsonObject.getString("type").equals("Withdraw")){
                    discription.setText("Withdrawn to Bank");
                    status.setText("Payment successful");
                    transactionLayer.setVisibility(View.VISIBLE);
                    from.setText("Wallet");
                    to.setText("Bank");
                    transactionId.setText(jsonObject.getString("transaction_id"));
                    imageView1.setImageResource(R.drawable.ic_wallet_black);
                    imageView2.setImageResource(R.drawable.ic_bank_building_black);
                    amount.setText("$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount"))));
                    sessionLayer.setVisibility(View.GONE);
                }else if(jsonObject.getString("type").equals("Income")){
                    discription.setText("Buddi paid to you");
                    status.setText("Payment successful");
                    transactionLayer.setVisibility(View.GONE);
                    from.setText("Buddi");
                    to.setText("Wallet");
                    imageView1.setImageResource(R.mipmap.ic_launcher);
                    imageView2.setVisibility(View.GONE);
                    imageView3.setVisibility(View.VISIBLE);
                    CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image,getApplicationContext(),""),imageView3,R.drawable.ic_broken_image,R.drawable.ic_broken_image);
                    amount.setText("$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount"))));
                    sessionLayer.setVisibility(View.VISIBLE);
                    session.setText(jsonObject.getString("session_duration")+" minutes "+
                            jsonObject.getString("session_name")+" session with "+ jsonObject.getString("trainee_name"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
}
