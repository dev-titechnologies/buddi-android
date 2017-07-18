package buddyapp.com.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;

public class RegisterScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    TextView Google, facebook;
    LoginButton facebook_loginbutton;
    CallbackManager callbackManager;
    String email, fname, lname, gender;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    int RC_SIGN_IN = 101;

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
                facebook_loginbutton.performClick();
                fblogin();
            }
        });
    }


    /**
     * facebook login
     **/
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
                    Log.e("onCancel", "onCancel");
                }

                @Override
                public void onError(FacebookException exception) {
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
