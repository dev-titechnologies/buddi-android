package buddiapp.com.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.adapter.WalletTransactionTraineeAdapter;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

public class WalletTransactionHistory extends AppCompatActivity {
    ListView transactionList;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noTransaction;
    WalletTransactionTraineeAdapter walletTransactionTraineeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_transaction_history);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("All Transactions");

        transactionList = findViewById(R.id.transaction_list);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        noTransaction = findViewById(R.id.no_transaction);

        if(CommonCall.isNetworkAvailable())
        {new GetWalletTransactionList().execute();}
        else{
            Snackbar snackbar = Snackbar
                    .make(swipeRefreshLayout, "Please check your internet connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GetWalletTransactionList().execute();
                        }
                    });

            snackbar.show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new GetWalletTransactionList().execute();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    public class GetWalletTransactionList extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(WalletTransactionHistory.this,"Please wait...");
        }

        @Override
        protected String doInBackground(String... strings) {

            String response = NetworkCalls.POST(Urls.getGetWalletHistoryURL(), "");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            swipeRefreshLayout.setRefreshing(false);
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {
                    if(response.getJSONArray("data").length()>0){
                        noTransaction.setVisibility(View.GONE);
                        JSONArray dataArray = response.getJSONArray("data");
                        walletTransactionTraineeAdapter = new WalletTransactionTraineeAdapter(dataArray, WalletTransactionHistory.this);
                        transactionList.setAdapter(walletTransactionTraineeAdapter);
                        transactionList.setScrollContainer(false);
                    }else{
                        swipeRefreshLayout.setVisibility(View.GONE);
                        noTransaction.setVisibility(View.VISIBLE);
                    }

                }else if (response.getInt(Constants.status) == 2) {

                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar
                            .make(swipeRefreshLayout, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                            new GetWalletTransactionList().execute();
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
