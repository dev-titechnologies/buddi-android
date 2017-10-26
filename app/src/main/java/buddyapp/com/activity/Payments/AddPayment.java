package buddyapp.com.activity.Payments;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class AddPayment extends AppCompatActivity {
    CardMultilineWidget mCardInputWidget;
    LinearLayout root, sub_view_ssn;
    Button save;
    Card cardToSave;

    EditText ssn, dob, city, address_line1, address_line2, postalcode, state;


    boolean validate() {

        boolean status;


        if ((PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))) {

            return true;
        } else if (ssn.getText().toString().trim().length() == 0) {

            ssn.setError("Please enter your SSN!");

            status = false;
        } else if (dob.getText().toString().trim().length() == 0) {
            dob.setError("Please enter your Date of Birth!");
            status = false;

        } else if (city.getText().toString().trim().length() == 0) {

            city.setError("Please enter your  city!");
            status = false;

        } else if (address_line1.getText().toString().trim().length() == 0) {

            address_line1.setError("Please enter your address!");
            status = false;

        } else if (address_line2.getText().toString().trim().length() == 0) {
            address_line2.setError("Please enter your address!");
            status = false;

        } else if (postalcode.getText().toString().trim().length() == 0) {
            postalcode.setError("Please enter your postal code!");
            status = false;
        } else if (state.getText().toString().trim().length() == 0) {
            state.setError("Please enter your state!");
            status = false;
        } else {
            status = true;
        }
        return status;
    }

    DatePickerDialog.OnDateSetListener dateListner;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        mCardInputWidget = (CardMultilineWidget) findViewById(R.id.card_input_widget);

        ssn = (EditText) findViewById(R.id.ssn);
        dob = (EditText) findViewById(R.id.dob);
        city = (EditText) findViewById(R.id.city);
        address_line1 = (EditText) findViewById(R.id.address_line1);
        address_line2 = (EditText) findViewById(R.id.address_line2);
        postalcode = (EditText) findViewById(R.id.postalcode);
        state = (EditText) findViewById(R.id.state);


        dateListner = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dob.setText(sdf.format(myCalendar.getTime()));
            }

        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddPayment.this, dateListner, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });


        save = (Button) findViewById(R.id.save);

        root = (LinearLayout) findViewById(R.id.root);
        sub_view_ssn = (LinearLayout) findViewById(R.id.sub_view_ssn);
        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee")) {

            sub_view_ssn.setVisibility(View.GONE);
        } else {

            sub_view_ssn.setVisibility(View.VISIBLE);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Payments");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonCall.showLoader(AddPayment.this);
                cardToSave = mCardInputWidget.getCard();

                if (cardToSave == null) {
                    CommonCall.hideLoader();
                    Toast.makeText(AddPayment.this, "Invalid Card Data", Toast.LENGTH_SHORT).show();

                } else if (!cardToSave.validateCard()) {
                    // Do not continue token creation.
                    CommonCall.hideLoader();
                    Toast.makeText(AddPayment.this, "Invalid Card Data.", Toast.LENGTH_SHORT).show();

                } else if (validate())

                {

                    hideSoftKeyboard();
                    Stripe stripe = new Stripe(getApplicationContext(), getString(R.string.stripe_key));

                    cardToSave.setCurrency("usd");
                    stripe.createToken(
                            cardToSave,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server


                                    new addCard(token.getId()).execute();

                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    Toast.makeText(getApplicationContext(),
                                            error.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();

                                    CommonCall.hideLoader();
                                }
                            }
                    );


                } else {
                    CommonCall.hideLoader();
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

    private class addCard extends AsyncTask<String, String, String> {

        String token;

        public addCard(String token) {

            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {


            JSONObject req = new JSONObject();
            String res = null;
            try {
                req.put("card_token", token);

                res = NetworkCalls.POST(Urls.getaddCardtoStripeURL(), req.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {


//                    Toast.makeText(AddPayment.this, "Card Saved Applied Succesfully!", Toast.LENGTH_SHORT).show();


                    if ((PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")))

                        new updateStripeAcc().execute();
                    else {

                        CommonCall.hideLoader();
                        finish();
                    }

                } else if (response.getInt(Constants.status) == 2) {
                    CommonCall.hideLoader();
                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new addCard(token).execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {
                    CommonCall.hideLoader();
                    CommonCall.sessionout(getApplicationContext());
                }


            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
            }
        }
    }

    private class updateStripeAcc extends AsyncTask<String, String, String> {

        String token;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {


            JSONObject req = new JSONObject();
            String res = null;
            try {

                req.put("last_4_snn", ssn.getText().toString());
                req.put("city", city.getText().toString());
                req.put("line1", address_line1.getText().toString());
                req.put("line2", address_line2.getText().toString());
                req.put("postal", postalcode.getText().toString());
                req.put("state", state.getText().toString());

                req.put("day", myCalendar.get(Calendar.DAY_OF_MONTH));
                req.put("month", myCalendar.get(Calendar.MONTH));
                req.put("year", myCalendar.get(Calendar.YEAR));

                res = NetworkCalls.POST(Urls.getupdateStripeAccURL(), req.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();


            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {


                    Toast.makeText(AddPayment.this, "Card Saved Applied Succesfully!", Toast.LENGTH_SHORT).show();


                    finish();


                } else if (response.getInt(Constants.status) == 2) {

                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new addCard(token).execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {

                    CommonCall.sessionout(getApplicationContext());
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
