package buddyapp.com.activity;

import android.content.Intent;

import android.os.AsyncTask;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import buddyapp.com.R;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.questions.DoneActivity;
import buddyapp.com.utils.CommonCall;

import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class RegisterScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView  next;
    String semail, sfname,  slname, sgender="", scountrycode, smobilenumber, validnumber, spassword, sfacebookId="",sgoogleplusId="";
    String register_type= "normal";
    ImageView Google, facebook;
    String user_image;

    CountryCodePicker ccp;
    boolean isValid = false;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 101;

    RadioGroup rg;
    RadioButton rbmale, rbfemale;
    EditText firstName, lastName, eMail, password, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);


        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        eMail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);
        rg = (RadioGroup) findViewById(R.id.radioGroup);
        rbmale = (RadioButton) findViewById(R.id.male);
        rbfemale = (RadioButton) findViewById(R.id.female);


        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        next = (TextView) findViewById(R.id.next);
        mobile = (EditText) findViewById(R.id.mobile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign Up");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Google = (ImageView) findViewById(R.id.googleplus);
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
                if(CommonCall.isNetworkAvailable()) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    }
            }
        });

        facebook_loginbutton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().logOut();
        facebook = (ImageView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook.setEnabled(false);
                if(CommonCall.isNetworkAvailable()) {
                    facebook_loginbutton.performClick();
                    fblogin();
                }else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    }
            }
        });

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {

            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.male) {
                    sgender = "male";
                } else if (i == R.id.female) {
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
                    CommonCall.PrintLog("Phone number++", swissNumberProto + "");
                    isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
                    if (isValid) {
                        CommonCall.PrintLog("Phone number", swissNumberProto.getNationalNumber() + "");
                        validnumber = "+"+scountrycode+"-"+swissNumberProto.getNationalNumber();
                    }
                        else
                        CommonCall.PrintLog("Invalid", "Invalid");
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
                if(validateFeelds()) {
                    if(CommonCall.isNetworkAvailable())
                   new sendOtp().execute();
                    else{
                        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    }
//                    Intent mobReg = new Intent(getApplicationContext(), MobileVerificationActivity.class);
//                    mobReg.putExtra("MOBILE", smobilenumber);
//                    startActivityForResult(mobReg, 156);//for otp verification handling
                }
            }
        });
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
/********************** Field validation *******************/

    private boolean validateFeelds() {
        View focusView = null;
        if (firstName.getText().length() == 0) {
            firstName.setError("Invalid Firstname");
            focusView = firstName;
            focusView.requestFocus();
            return false;
        } else if (lastName.getText().length() == 0) {
            lastName.setError("Invalid Lastname");
            focusView = lastName;
            focusView.requestFocus();
            return false;
        } else if (eMail.getText().length() == 0) {
            eMail.setError("Please enter your email");
            focusView = eMail;
            focusView.requestFocus();
            return false;
        } else if (!isEmailValid(eMail.getText().toString())) {
            eMail.setError("Invalid email");
            focusView = eMail;
            focusView.requestFocus();
            return false;
        } else if (mobile.getText().length() == 0) {
            mobile.setError("Please enter your mobile number");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        } else if (!isValid) {
            mobile.setError("Please check your mobile number and country code");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        } else if (sgender.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            focusView = rg;
            focusView.requestFocus();
            return false;
        } else if (password.getText().length() == 0) {
            password.setError("Please enter password");
            focusView = password;
            focusView.requestFocus();
            return false;
        } else if (password.getText().length() < 8) {
            password.setError("Password must be 8 characters or more");
            focusView = password;
            focusView.requestFocus();
            return false;
        } else {
            sfname = firstName.getText().toString();
            slname = lastName.getText().toString();
            semail = eMail.getText().toString();
            spassword = password.getText().toString();
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

     /*********************** facebook login *********************/

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


                                        CommonCall.PrintLog("facebookresponsse",object.toString());
                                        register_type="facebook";
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
                                                semail = object.getString("email");
                                                eMail.setText(semail);
                                            }
                                        }
                                        sfacebookId = object.getString("id");
                                        try {
                                            URL image_url = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                                            user_image = image_url.toString();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                   new login().execute();

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
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT);
                }

                @Override
                public void onError(FacebookException exception) {
                    facebook.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT);
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
                register_type="google";
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
//                mFullName = acct.getDisplayName();
//                mEmail = acct.getEmail();
                String[] splitednaame = acct.getDisplayName().split("\\s+");
                if (splitednaame[0]!=null)
                firstName.setText(splitednaame[0]);

                if (splitednaame[1]!=null)
                lastName.setText(splitednaame[1]);
                sgoogleplusId = acct.getId();

               user_image = acct.getPhotoUrl().toString();

                if (acct.getEmail()!=null)
                eMail.setText(acct.getEmail());
                new login().execute();
//                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 156) { // otp verification post process
            if (resultCode == RESULT_OK) {
                CommonCall.PrintLog("otp", "done");
                new register().execute();

            }
        } else

            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Connection Failed!", Toast.LENGTH_SHORT).show();
    }

/***** register user ****/
    class register extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();
        String registerResponse;

        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(RegisterScreen.this);


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("register_type", register_type);
                reqData.put("email",semail);
                reqData.put("password",spassword);
                reqData.put("first_name",sfname);
                reqData.put("last_name",slname);
                reqData.put("mobile",validnumber);
                reqData.put("gender",sgender);
                reqData.put("user_image", user_image);
                reqData.put("user_type", PreferencesUtils.getData(Constants.user_type,getApplicationContext(),""));
                reqData.put("profile_desc","Description");
                reqData.put("facebook_id",sfacebookId);
                reqData.put("google_id",sgoogleplusId);

                registerResponse = NetworkCalls.POST(Urls.getRegisterURL(),reqData.toString());


            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return registerResponse;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj= new JSONObject(s);
                if(obj.getInt("status")==1){

                PreferencesUtils.saveData(Constants.token,obj.getString(Constants.token),getApplicationContext());
                PreferencesUtils.saveData(Constants.user_id,obj.getString(Constants.user_id),getApplicationContext());


                    if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals(Constants.trainer)){

                       Intent intent = new Intent(getApplicationContext(),ChooseCategory.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();

                   }else {
                       Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       startActivity(intent);
                       finish();
                   }
                }else if(obj.getInt("status")==2){
                    Toast.makeText(RegisterScreen.this,obj.getString("message"), Toast.LENGTH_SHORT).show();
                }else if(obj.getInt("status")==3){
                    CommonCall.sessionout(RegisterScreen.this);
                    finish();
                }else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            CommonCall.hideLoader();

        }
    }
/*** OTP SENDING  ***/
class sendOtp extends AsyncTask<String, String, String> {

    JSONObject reqData = new JSONObject();


    @Override
    protected void onPreExecute() {
        CommonCall.showLoader(RegisterScreen.this);

        try {
            reqData.put("mobile", validnumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... strings) {


        return NetworkCalls.POST(Urls.getSendOTPURL(), reqData.toString());
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        CommonCall.hideLoader();

        try {
            final JSONObject response = new JSONObject(s);

            if (response.getInt(Constants.status) == 1) {
                Intent mobReg = new Intent(getApplicationContext(), MobileVerificationActivity.class);
                mobReg.putExtra("MOBILE", validnumber);
                startActivityForResult(mobReg, 156);
            } else if (response.getInt(Constants.status) == 2) {
                Toast.makeText(getApplicationContext(),response.getString("message"),Toast.LENGTH_SHORT).show();

            } else if (response.getInt(Constants.status) == 3) {


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}


    class login extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String loginResponse = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(RegisterScreen.this);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("login_type", register_type);
                reqData.put("email", semail);
                reqData.put("password", spassword);
                reqData.put("facebook_id", sfacebookId);
                reqData.put("google_id", sgoogleplusId);
                reqData.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));
                loginResponse = NetworkCalls.POST(Urls.getLoginURL(), reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return loginResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                facebook.setEnabled(true);
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.token, jsonObject.getString(Constants.token), getApplicationContext());
                    PreferencesUtils.saveData(Constants.user_id, jsonObject.getString(Constants.user_id), getApplicationContext());
                    PreferencesUtils.saveData(Constants.email, jsonObject.getString(Constants.email), getApplicationContext());
                    PreferencesUtils.saveData(Constants.fname, jsonObject.getString(Constants.fname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.lname, jsonObject.getString(Constants.lname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.user_image, jsonObject.getString(Constants.user_image), getApplicationContext());
                    PreferencesUtils.saveData(Constants.gender, jsonObject.getString(Constants.gender), getApplicationContext());
                    PreferencesUtils.saveData(Constants.mobile, jsonObject.getString(Constants.mobile), getApplicationContext());
                    PreferencesUtils.saveData(Constants.trainer_type, jsonObject.getString(Constants.trainer_type), getApplicationContext());


                    if (!PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee")) {

                        PreferencesUtils.saveData(Constants.approved, jsonObject.getString(Constants.approved), getApplicationContext());
                        PreferencesUtils.saveData(Constants.pending, jsonObject.getString(Constants.pending), getApplicationContext());


                        if (new JSONArray( PreferencesUtils.getData(Constants.approved, getApplicationContext(), "[]")).length() == 0) {


                            if (new JSONArray(PreferencesUtils.getData(Constants.pending, getApplicationContext(), "[]")).length() == 0) {


                                Intent intent = new Intent(getApplicationContext(), ChooseCategory.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }else {


                                Intent intent = new Intent(getApplicationContext(), DoneActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {

//                        if (PreferencesUtils.getData(Constants.pending, getApplicationContext(), "").length() == 0) {
//
//
//                            Intent intent = new Intent(getApplicationContext(), ChooseCategory.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
//                            finish();
//
//                        }else

                            {


                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();


                            }
                        }


                    }else{
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        finish();
                    }

                } else if (obj.getInt("status") == 2) {

//                    Toast.makeText(RegisterScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//                    if(obj.getString("status_type").equals("UserDoesntExist")) {
//                        Intent intent = new Intent(getApplicationContext(), RegisterScreen.class);
//                        startActivity(intent);
//                        finish();
//                    }

                } else if (obj.getInt("status") == 3) {

//                    Toast.makeText(RegisterScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            CommonCall.hideLoader();
        }
    }
}
