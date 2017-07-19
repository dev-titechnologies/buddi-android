package buddyapp.com.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import buddyapp.com.R;
public class RegisterScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView Google, facebook, next;
    String semail, sfname, slname, sgender="", scountrycode, smobilenumber, spassword;
    CountryCodePicker ccp;
    boolean isValid = false;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 101;
    RadioGroup rg; RadioButton rbmale, rbfemale;
    EditText firstName,lastName,eMail,password,mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        firstName =(EditText)findViewById(R.id.first_name);
        lastName =(EditText)findViewById(R.id.last_name);
        eMail =(EditText)findViewById(R.id.email);
        password =(EditText)findViewById(R.id.password);
        mobile =(EditText)findViewById(R.id.mobile);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rbmale = (RadioButton) findViewById(R.id.male);
        rbfemale = (RadioButton) findViewById(R.id.female);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        next = (TextView) findViewById(R.id.next);
        mobile = (EditText) findViewById(R.id.mobile);

        Google = (TextView) findViewById(R.id.googleplus);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        facebook_loginbutton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().logOut();
        facebook = (TextView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook.setEnabled(false);
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

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.male){
                sgender = "male";
                }
                else if(i==R.id.female){
                sgender = "female";
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                scountrycode = ccp.getSelectedCountryCode();
                smobilenumber = String.valueOf(mobile.getText());
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(smobilenumber, ccp.getSelectedCountryNameCode());
                isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true

                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
                if(validateFeelds())
                {
//                    new checkuserexists().execute();
                }
            }
        });
    }

    private boolean validateFeelds() {
        View focusView = null;
        if(firstName.getText().length()== 0 ){
            firstName.setError("Invalid Firstname");
            focusView = firstName;
            focusView.requestFocus();
            return false;
        }else if(lastName.getText().length()== 0 ) {
            lastName.setError("Invalid Lastname");
            focusView = lastName;
            focusView.requestFocus();
            return false;
        }else if(eMail.getText().length()==0){
            eMail.setError("Please enter your email");
            focusView = eMail;
            focusView.requestFocus();
            return false;
        }else if(!isEmailValid(eMail.getText().toString())){
            eMail.setError("Invalid email");
            focusView = eMail;
            focusView.requestFocus();
            return false;
        }else if(mobile.getText().length()==0){
            mobile.setError("Please enter your mobile number");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        }else if(!isValid){
            mobile.setError("Please check your mobile number and country code");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        }else if(sgender.length()==0){
            Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
            focusView = rg;
            focusView.requestFocus();
            return false;
        }else if(password.getText().length()==0)
        {
            password.setError("Please enter password");
            focusView = password;
            focusView.requestFocus();
            return false;
        }else if(password.getText().length()<8)
        {
            password.setError("Password must be 8 characters or more");
            focusView = password;
            focusView.requestFocus();
            return false;
        }
        else{
            sfname =firstName.getText().toString();
            slname =lastName.getText().toString();
            semail =eMail.getText().toString();
            spassword =password.getText().toString();
            return true;
        }
    }

    private boolean isEmailValid(String email) {

        String EMAIL_PATTERN = "^[A-Za-z0-9]+([A-Z0-9a-z\\._]+[A-Za-z0-9])*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }
     /** facebook login**/
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
                                        if(object.getString("first_name").length()!=0)
                                        {sfname = object.getString("first_name");
                                        firstName.setText(sfname);}
                                        if(object.getString("last_name").length()!=0)
                                        {slname = object.getString("last_name");
                                        lastName.setText(slname);}
                                        if (object.has("gender") && object.getString("gender") != null && object.getString("gender").toString().length() > 0)
                                        { sgender = object.getString("gender");
                                        if(object.getString("gender").equalsIgnoreCase("male"))
                                            rbmale.setChecked(true);
                                        else if(object.getString("gender").equalsIgnoreCase("female"))
                                            rbfemale.setChecked(true);}
                                        if ((object.has("email"))) {
                                            if ((object.getString("email")).trim().length() > 0) {
                                                semail= object.getString("email");
                                                eMail.setText(semail);
                                            }
                                        }
                                    } catch (JSONException e) {
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
                    facebook.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT);
                }

                @Override
                public void onError(FacebookException exception) {
                    facebook.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT);
                    exception.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
//                mFullName = acct.getDisplayName();
//                mEmail = acct.getEmail();
                firstName.setText(acct.getDisplayName());
                lastName.setText(acct.getDisplayName());


                if (acct.getEmail()!=null)
                eMail.setText(acct.getEmail());
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        }else

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Connection Failed!", Toast.LENGTH_SHORT).show();
    }
}
