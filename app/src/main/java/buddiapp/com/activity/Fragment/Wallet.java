package buddiapp.com.activity.Fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.HomeActivity;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;


/**
 * A simple {@link Fragment} subclass.
 */
public class Wallet extends Fragment {
    TextView walletAmount, proceed, allTransaction;
    EditText inputAmount;
    FrameLayout root;
    public Wallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        ((HomeActivity)getActivity()).setDrawerLocked(true);
        walletAmount = view.findViewById(R.id.wallet_amount);
        proceed = view.findViewById(R.id.proceed);
        allTransaction = view.findViewById(R.id.all_transaction);
        inputAmount = view.findViewById(R.id.input_amount);
        root = view.findViewById(R.id.root);

        new GetWalletBalance().execute();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputAmount.getText().length()>0){
                    new AddToWallet().execute();
                }
            }
        });
        inputAmount.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(!s.equals("") && s.length()>0 )
                {
                    proceed.setBackgroundResource(R.color.grey);
                }else
                {
                    proceed.setBackgroundResource(R.color.ash);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        allTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).setDrawerLocked(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((HomeActivity)getActivity()).setDrawerLocked(false);
    }

    public class AddToWallet extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity(), "Processing your request...");
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                reqData.put("amount", inputAmount.getText());

            } catch (Exception e) {
                e.printStackTrace();
            }
            String response = NetworkCalls.POST(Urls.getAddToWalletURL(), reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {
                    Toast.makeText(getActivity(),response.getString("message"), Toast.LENGTH_SHORT).show();
                    walletAmount.setText(response.getJSONObject("data").getString("walletBalance"));
                    PreferencesUtils.saveData(Constants.wallet,response.getJSONObject("data").getString("walletBalance"),getActivity());
                    inputAmount.setText("");
//                    wallet.setText("$"+response.getJSONObject("data").getString("walletBalance"));
                }else if (response.getInt(Constants.status) == 2) {
                    if(response.getString("message").equals("Cannot charge a customer that has no active card")){
                        @SuppressLint("RestrictedApi") final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.cancelDialog));

                        builder.setMessage("You are not added your card details with buddi. Please add it in Payment method tab");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                            alert.dismiss();

                            }
                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.getInt(Constants.status) == 3) {
                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getActivity());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetWalletBalance extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

            String response = NetworkCalls.POST(Urls.getWalletBalanceURL(), "");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    walletAmount.setText(response.getJSONObject("data").getString("walletBalance"));
                    PreferencesUtils.saveData(Constants.wallet,response.getJSONObject("data").getString("walletBalance"),getActivity());
//                    wallet.setText("$"+response.getJSONObject("data").getString("walletBalance"));
                }else if (response.getInt(Constants.status) == 2) {

                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    new GetWalletBalance().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {
                    Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getActivity());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
