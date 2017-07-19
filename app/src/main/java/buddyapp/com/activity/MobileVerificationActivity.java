package buddyapp.com.activity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.smscatcher.OnSmsCatchListener;
import buddyapp.com.smscatcher.SmsVerifyCatcher;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class MobileVerificationActivity extends AppCompatActivity {
    SmsVerifyCatcher smsVerifyCatcher;

    EditText otp;
String mobile;
    RelativeLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        root =(RelativeLayout)findViewById(R.id.root);
otp = (EditText)findViewById(R.id.otp);


        mobile= getIntent().getStringExtra("MOBILE");
new sendOtp().execute();
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otp.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });

    }
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }
    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    class sendOtp extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();



        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(MobileVerificationActivity.this);

            try {
                reqData.put("mobile",mobile);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {



            return  NetworkCalls.POST(Urls.getSendOTPURL(),reqData.toString());
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status)==1){



                }else if (response.getInt(Constants.status)==2){
                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                        snackbar1 = Snackbar.make(root, "OTP SEND SUCCESFULLY", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new sendOtp().execute();
                                }
                            });

                    snackbar.show();


                }else if (response.getInt(Constants.status)==3){



                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
