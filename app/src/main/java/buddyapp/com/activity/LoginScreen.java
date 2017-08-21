package buddyapp.com.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.questions.DoneActivity;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView login, forgotpassword;
    String semail, sfname, slname, sgender = "", scountrycode, smobilenumber, spassword, sfacebookId = "", sgoogleplusId = "";
    ImageView Google, facebook;
    boolean isValid = false;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 101;
    String login_type = "normal";
    EditText eMail, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        eMail = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (TextView) findViewById(R.id.next);
        forgotpassword = (TextView) findViewById(R.id.forgotpassword);
        Google = (ImageView) findViewById(R.id.googleplus);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, LoginScreen.this)
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
        facebook = (ImageView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonCall.isNetworkAvailable()) {
                    LoginManager.getInstance().logOut();
                    facebook.setEnabled(false);
                    facebook_loginbutton.performClick();
                    fblogin();
                } else {
                    Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (eMail.getText().toString().length() == 0) {
                        eMail.setError("Please enter your email");
                        eMail.requestFocus();
                    } else if (password.getText().toString().length() == 0) {
                        password.setError("Please enter Password");
                    } else {
                        semail = eMail.getText().toString();
                        spassword = password.getText().toString();
                        if (CommonCall.isNetworkAvailable())
                            new login().execute();
                        else {
                            Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
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

    /**
     * facebook login
     **/
    private void fblogin() {
        try {
            callbackManager = CallbackManager.Factory.create();
            facebook_loginbutton.setReadPermissions("email");
            facebook.setEnabled(true);
            facebook_loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        login_type = "facebook";
                                        if (object.getString("first_name").length() != 0) {
                                            sfname = object.getString("first_name");
                                        }
                                        if (object.getString("last_name").length() != 0) {
                                            slname = object.getString("last_name");
                                        }
                                        if (object.has("gender") && object.getString("gender") != null && object.getString("gender").toString().length() > 0) {
                                            sgender = object.getString("gender");
                                        }
                                        if ((object.has("email"))) {
                                            if ((object.getString("email")).trim().length() > 0) {
                                                semail = object.getString("email");
                                            }
                                        }
                                        sfacebookId = object.getString("id");
                                        if (CommonCall.isNetworkAvailable())
                                            new login().execute();
                                        else {
                                            Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    facebook.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
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
                try{
                    String split[] = acct.getDisplayName().split("\\s+");
                sfname = split[0];
                slname = split[1];
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                sgoogleplusId = acct.getId();
                login_type = "google";
                if (acct.getEmail() != null)
                    semail=acct.getEmail();
                if (CommonCall.isNetworkAvailable())
                    new login().execute();
                else {
                    Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        } else

            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Connection Failed!", Toast.LENGTH_SHORT).show();
    }

    /******************* Login *******************/
    class login extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String loginResponse = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(LoginScreen.this);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("login_type", login_type);
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
                CommonCall.hideLoader();
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
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }else {


                                Intent intent = new Intent(getApplicationContext(), DoneActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    Toast.makeText(LoginScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    if(obj.getString("status_type").equals("UserNotRegistered")) {
                        Intent intent = new Intent(getApplicationContext(), RegisterScreen.class);
                        intent.putExtra("login_type", login_type);
                        intent.putExtra("email", semail);
                        intent.putExtra("sfname", sfname);
                        intent.putExtra("slname", slname);
                        intent.putExtra("facebook_id", sfacebookId);
                        intent.putExtra("google_id", sgoogleplusId);
                        startActivity(intent);
                        finish();
                    }
                } else if (obj.getInt("status") == 3) {

                    Toast.makeText(LoginScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
