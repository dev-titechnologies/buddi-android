package buddyapp.com.activity.Payments;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.paypal.android.sdk.onetouch.core.PayPalOneTouchCore;

import org.json.JSONException;
import org.json.JSONObject;



import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;


@SuppressWarnings("deprecation")
public abstract class BaseActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback,
        PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener,
        ActionBar.OnNavigationListener {

    private static final String KEY_AUTHORIZATION = "buddyapp.com.activity.Payments.KEY_AUTHORIZATION";

    protected String mAuthorization;
    protected String mCustomerId;
    protected BraintreeFragment mBraintreeFragment;


    private boolean mActionBarSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_AUTHORIZATION)) {
            mAuthorization = savedInstanceState.getString(KEY_AUTHORIZATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (!mActionBarSetup) {
//            setupActionBar();
//            mActionBarSetup = true;
//        }

//        SignatureVerificationOverrides.disableAppSwitchSignatureVerification(
//                Settings.isPayPalSignatureVerificationDisabled(this));
        PayPalOneTouchCore.useHardcodedConfig(this, Settings.useHardcodedPayPalConfiguration(this));

//        if (BuildConfig.DEBUG && ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{ WRITE_EXTERNAL_STORAGE }, 1);
//        } else

        {
            handleAuthorizationState();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handleAuthorizationState();
    }

    private void handleAuthorizationState() {
        if (mAuthorization == null ||
                (Settings.useTokenizationKey(this) && !mAuthorization.equals(Settings.getEnvironmentTokenizationKey(this))) ||
                !TextUtils.equals(mCustomerId, Settings.getCustomerId(this))) {
            performReset();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAuthorization != null) {
            outState.putString(KEY_AUTHORIZATION, mAuthorization);
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        CommonCall.PrintLog("Payment Method Nonce received: ", "" + paymentMethodNonce.getTypeLabel());
    }

    @Override
    public void onCancel(int requestCode) {
        Log.e("Cancel received: ", "" + requestCode);
    }

    @Override
    public void onError(Exception error) {
        CommonCall.PrintLog("Error received (" + error.getClass() + "): ", error.getMessage());
        CommonCall.PrintLog("error", error.toString());

        showDialog("An error occurred (" + error.getClass() + "): " + error.getMessage());
    }

    private void performReset() {
        mAuthorization = null;
        mCustomerId = Settings.getCustomerId(this);

        if (mBraintreeFragment == null) {
            mBraintreeFragment = (BraintreeFragment) getFragmentManager()
                    .findFragmentByTag(BraintreeFragment.TAG);
        }

        if (mBraintreeFragment != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getFragmentManager().beginTransaction().remove(mBraintreeFragment).commitNow();
            } else {
                getFragmentManager().beginTransaction().remove(mBraintreeFragment).commit();
                getFragmentManager().executePendingTransactions();
            }

            mBraintreeFragment = null;
        }

        reset();
        fetchAuthorization();
    }

    protected abstract void reset();

    protected abstract void onAuthorizationFetched();

    protected void fetchAuthorization() {
        if (mAuthorization != null) {
            onAuthorizationFetched();
        } else if (Settings.useTokenizationKey(this)) {
            mAuthorization = Settings.getEnvironmentTokenizationKey(this);
            onAuthorizationFetched();
        } else {



                new getClientToken().execute();


//            getApiClient(this).getClientToken("142",
//                     new Callback<ClientToken>() {
//                        @Override
//                        public void success(ClientToken clientToken, Response response) {
//                            if (TextUtils.isEmpty(clientToken.getClientToken())) {
//                                showDialog("Client token was empty");
//                            } else {
//                                mAuthorization = clientToken.getClientToken();
//                                onAuthorizationFetched();
//                            }
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                            showDialog("Unable to get a client token. Response Code: " +
//                                    error.getResponse().getStatus() + " Response body: " +
//                                    error.getResponse().getBody());
//                        }
//                    });
        }
        }


        class getClientToken extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... strings) {

                JSONObject request = new JSONObject();
                try {
                    request.put("user_id", PreferencesUtils.getData(Constants.user_id,getApplicationContext(),""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String res = NetworkCalls.POST(Urls.getPAYMENTTOKENURL(), request.toString());
                return res;
            }

            @Override
            protected void onPostExecute(String clientToken) {
                super.onPostExecute(clientToken);

                try {

                    JSONObject res = new JSONObject(clientToken);

                    if (res.getInt("status") == 1) {



                        mAuthorization = res.getString("data");
                                onAuthorizationFetched();


                    }else if(res.getInt("status")==2){
                        Toast.makeText(BaseActivity.this,res.getString("message"), Toast.LENGTH_SHORT).show();
                    }else if(res.getInt("status")==3){
                        CommonCall.sessionout(BaseActivity.this);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }



    protected void showDialog(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
