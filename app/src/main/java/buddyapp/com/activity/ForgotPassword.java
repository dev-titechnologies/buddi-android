package buddyapp.com.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class ForgotPassword extends AppCompatActivity {
    Button reset;
    EditText email;
    String semail="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email= (EditText) findViewById(R.id.email);
        reset= (Button) findViewById(R.id.reset);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                if (email.getText().length() == 0) {
                    email.setError("Please enter your email");
                    focusView = email;
                    focusView.requestFocus();
                } else if (!isEmailValid(email.getText().toString())) {
                    email.setError("Invalid email");
                    focusView = email;
                    focusView.requestFocus();
                }
                else{
                    semail=email.getText().toString();
                    new resetPassword().execute();
                }

            }
        });
    }
    private boolean isEmailValid(String email) {

        String EMAIL_PATTERN = "^[A-Za-z0-9]+([A-Z0-9a-z\\._]+[A-Za-z0-9])*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    class resetPassword extends AsyncTask<String,String,String>{

        JSONObject reqData = new JSONObject();
        String resetResponse;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(ForgotPassword.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.email,semail);

                resetResponse = NetworkCalls.POST(Urls.getResetURL(),reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resetResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                CommonCall.hideLoader();
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    Toast.makeText(ForgotPassword.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                    finish();
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(ForgotPassword.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 3) {

                } else {

                }
            }
            catch (JSONException e){

            }
        }
    }
}
