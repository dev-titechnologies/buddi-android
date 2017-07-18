package buddyapp.com.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;

public class RegisterScreen extends AppCompatActivity {
    TextView Google, facebook, next;
    EditText mobile;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    String email, fname, lname, gender, countrycode, mobilenumber;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        next = (TextView) findViewById(R.id.next);
        mobile = (EditText) findViewById(R.id.mobile);

        Google = (TextView) findViewById(R.id.googleplus);

        facebook_loginbutton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().logOut();
        facebook = (TextView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook_loginbutton.performClick();
                fblogin();
            }
        });

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Toast.makeText(getApplicationContext(),ccp.getSelectedCountryCode()+ "Updated " + ccp.getSelectedCountryName(), Toast.LENGTH_SHORT).show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobilenumber = String.valueOf(mobile.getText());
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(mobilenumber, ccp.getSelectedCountryNameCode());
                    boolean isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
                    if(isValid)
                        Log.e("Phone number", swissNumberProto+"");
                    else
                        Log.e("Invalid ", "Invalid");
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
            validateFeelds();
            }
        });
    }

    private void validateFeelds() {

    }


    /** facebook login **/
    private void fblogin() {
        try {
            callbackManager = CallbackManager.Factory.create();
            facebook_loginbutton.setReadPermissions("email");
            facebook_loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               GraphRequest request = GraphRequest.newMeRequest(
               loginResult.getAccessToken(),
               new GraphRequest.GraphJSONObjectCallback() {
                   @Override
                   public void onCompleted(JSONObject object, GraphResponse response) {
                     try {
                           Constants.fname = object.getString("first_name");
                           Constants.lname = object.getString("last_name");
                           if (object.getString("gender") != null && object.getString("gender").toString().length() > 0)
                               Constants.gender = object.getString("gender");
                           if ((object.toString().contains("email"))) {
                           if ((object.getString("email")).trim().length() > 0) {
                               Constants.email = object.getString("email");
//                                 new checkuserexists().execute();
                           }
                           }
                     }catch (JSONException e) {
                       e.printStackTrace();
                       }
                     }
               });
               Bundle parameters = new Bundle();
               parameters.putString("fields", "id,name,email,gender,birthday,first_name,last_name");
               request.setParameters(parameters);
               request.executeAsync();
        }
                @Override
                public void onCancel() {
                    Log.e("onCancel","onCancel");
                }
                @Override
                public void onError(FacebookException exception) {
                exception.printStackTrace();
                }
      });
     }catch (Exception e){
      e.printStackTrace();
     }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
