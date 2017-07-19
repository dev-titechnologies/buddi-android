package buddyapp.com.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;

public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView Google, facebook, login;
    String semail, sfname, slname, sgender="", scountrycode, smobilenumber, spassword, sfacebookId, sgoogleplusId;

    boolean isValid = false;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 101;

    EditText eMail,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        eMail =(EditText)findViewById(R.id.email);
        password =(EditText)findViewById(R.id.password);
        login = (TextView) findViewById(R.id.next);
        Google = (TextView) findViewById(R.id.googleplus);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
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
        facebook = (TextView) findViewById(R.id.facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook.setEnabled(false);
                facebook_loginbutton.performClick();
                fblogin();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try{
                if(eMail.getText().toString().length()==0){
                    eMail.setError("Please enter your email");
                    eMail.requestFocus();
                }else if(password.getText().toString().length()==0){
                    password.setError("Please enter Password");
                }
                else{
                    semail = eMail.getText().toString();
                    spassword = password.getText().toString();
//                    new checkuserexists().execute()
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            }
        });

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
                                        {sfname = object.getString("first_name");}
                                        if(object.getString("last_name").length()!=0)
                                        {slname = object.getString("last_name");}
                                        if (object.has("gender") && object.getString("gender") != null && object.getString("gender").toString().length() > 0)
                                        { sgender = object.getString("gender");}
                                        if ((object.has("email"))) {
                                            if ((object.getString("email")).trim().length() > 0) {
                                                semail= object.getString("email");
                                            }
                                        }
                                        sfacebookId = object.getString("id");
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
                String split[] = acct.getDisplayName().split("\\s+");
                sfname = split[0];
                slname = split[1];
                sgoogleplusId = acct.getId();

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

    class login extends AsyncTask<String,String,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
}
