package buddiapp.com.activity.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.ChooseSpecification;
import buddiapp.com.activity.MapTrainee;
import buddiapp.com.adapter.HomeCategoryAdapter;
import buddiapp.com.database.DatabaseHandler;
import buddiapp.com.utils.AlertDialoge.RatingDialog;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeCategory extends Fragment {
    RelativeLayout root;
    DatabaseHandler db;
    GridView grid;
    HomeCategoryAdapter categoryAdapter;
    public static Button next;
    public static CardView instantCard;
    LinearLayout instantBooking;
    public static ArrayList<String> cat_selectedID = new ArrayList<>();
    RatingDialog ratingDialog;
    ImageView errorImage;
    Boolean releaseForm=false;
    public HomeCategory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_category, container, false);

        db = new DatabaseHandler(getActivity());
        root = (RelativeLayout) view.findViewById(R.id.root);
        grid = (GridView) view.findViewById(R.id.grid);
        next = (Button) view.findViewById(R.id.next);
        instantCard = (CardView) view.findViewById(R.id.card_instant);
        instantBooking = (LinearLayout) view.findViewById(R.id.instant_booking);
        errorImage = (ImageView) view.findViewById(R.id.errorImage);

        if (db.getAllCATForTrainee().length() > 0) {
            loadData(db.getAllCATForTrainee());

        } else {
            new getCategoryList().execute();
        }
        if (PreferencesUtils.getData(Constants.flag_rating, getActivity(), "").equals("true")) {
            PreferencesUtils.saveData(Constants.flag_rating, "false", getActivity());
            ratingDialog = new RatingDialog(getActivity());
            ratingDialog.show();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cat_selectedID.size() > 0) {
                    Intent intent = new Intent(getActivity(), ChooseSpecification.class);
                    startActivity(intent);
                }
            }
        });
        instantBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CommonCall.isNetworkAvailable()) {

                    if (PreferencesUtils.getData(Constants.settings_cat_id, getActivity(), "").length() == 0) {

                        Toast.makeText(getActivity(), "Please Save your deatils in Settings Screen inorder to use instant Booking", Toast.LENGTH_SHORT).show();
                    } else if (PreferencesUtils.getData(Constants.settings_address, getActivity(), "").length() == 0) {

                        Toast.makeText(getActivity(), "Please Save your deatils in Settings Screen inorder to use instant Booking", Toast.LENGTH_SHORT).show();

                    } else if (PreferencesUtils.getData(Constants.trainer_gender, getActivity(), "").length() == 0) {
                        Toast.makeText(getActivity(), "Please Save your deatils in Settings Screen inorder to use instant Booking", Toast.LENGTH_SHORT).show();


                    } else if (PreferencesUtils.getData(Constants.training_duration, getActivity(), "").length() == 0) {

                        Toast.makeText(getActivity(), "Please Save your deatils in Settings Screen inorder to use instant Booking", Toast.LENGTH_SHORT).show();

                    } else {
                        if(releaseForm){
                        new SearchTrainer().execute();
                        }else{
                            showReleseFormAlert();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    void loadData(JSONArray data) {

        categoryAdapter = new HomeCategoryAdapter(getActivity(), data);
        grid.setAdapter(categoryAdapter);

    }

    /*** get Category ***/

    class getCategoryList extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(getActivity());


        }

        @Override
        protected String doInBackground(String... strings) {


            return NetworkCalls.POST(Urls.getCATEGORYURL(), reqData.toString());
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {
                    errorImage.setVisibility(View.GONE);
                    db.insertCategory(response.getJSONArray("data"));
                    CommonCall.PrintLog("cat", db.getAllCATForTrainee().toString());


                    loadData(db.getAllCATForTrainee());

                } else if (response.getInt(Constants.status) == 2) {
                    errorImage.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getCategoryList().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {
                    CommonCall.sessionout(getActivity());

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void showReleseFormAlert() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog_releaseform);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        // set values for custom dialog components - text, image and button
        dialog.show();

        Button declineButton = (Button) dialog.findViewById(R.id.deny);
        final Button yes = (Button) dialog.findViewById(R.id.accept);
        final EditText participantSignature = dialog.findViewById(R.id.participant_signature);
        final EditText parentSignature = dialog.findViewById(R.id.parents_signature);
        final LinearLayout rootParent = dialog.findViewById(R.id.root_parent);
        TextView date1 = dialog.findViewById(R.id.date1);
        TextView date2 = dialog.findViewById(R.id.date2);

        date1.setText(DateFormat.getDateTimeInstance().format(new Date()));
        date2.setText(DateFormat.getDateTimeInstance().format(new Date()));
        // if decline button is clicked, close the custom dialog

        if(Integer.parseInt(PreferencesUtils.getData(Constants.age, getActivity(),"0"))<18){
            rootParent.setVisibility(View.VISIBLE);
        }


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(PreferencesUtils.getData(Constants.age,getActivity(),""))<18) {
                    if(participantSignature.getText().length()>0 && parentSignature.getText().length()>0) {
                        dialog.dismiss();
                        releaseForm = true;
                        PreferencesUtils.saveData(Constants.participant_signature,participantSignature.getText().toString(),getActivity());
                        PreferencesUtils.saveData(Constants.parent_signature,parentSignature.getText().toString(),getActivity());
                        next.performClick();
                    }else{
                        if(participantSignature.getText().length()>0)
                            parentSignature.setError("Please provide Parent signature");
                        else
                            participantSignature.setError("Please provide Participant signature");
                    }
                } else {

                    PreferencesUtils.saveData(Constants.participant_signature,participantSignature.getText().toString(),getActivity());
                    dialog.dismiss();
                    releaseForm = true;
                    next.performClick();
                }
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                Toast.makeText(getActivity(), "You must accept the Waiver Release Form to book a session!", Toast.LENGTH_SHORT).show();
                releaseForm = false;
                dialog.dismiss();

            }
        });
    }

    class SearchTrainer extends AsyncTask<String, String, String> {
        String response = "";
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
                reqData.put(Constants.gender, PreferencesUtils.getData(Constants.trainer_gender, getActivity(), ""));
                reqData.put("category", PreferencesUtils.getData(Constants.settings_cat_id, getActivity(), ""));
                reqData.put(Constants.latitude, PreferencesUtils.getData(Constants.settings_latitude, getActivity(), ""));
                reqData.put(Constants.longitude, PreferencesUtils.getData(Constants.settings_longitude, getActivity(), ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            response = NetworkCalls.POST(Urls.getTrainerSearchURL(), reqData.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                     if (jsonArray.length() != 0) {
                        PreferencesUtils.saveData("searchArray", obj.getJSONArray("data").toString(), getActivity());
                        PreferencesUtils.saveData(Constants.instant_booking, "true", getActivity());

                        PreferencesUtils.saveData(Constants.latitude,obj.getJSONArray("data").getJSONObject(0).getString("latitude"), getActivity());

                        PreferencesUtils.saveData(Constants.longitude, obj.getJSONArray("data").getJSONObject(0).getString("longitude"), getActivity());


                        Intent intent = new Intent(getActivity(), MapTrainee.class);

                        intent.putExtra(Constants.gender, PreferencesUtils.getData(Constants.trainer_gender, getActivity(), ""));
                        intent.putExtra("category", PreferencesUtils.getData(Constants.settings_cat_id, getActivity(), ""));
                        intent.putExtra(Constants.latitude, (PreferencesUtils.getData(Constants.settings_latitude, getActivity(), "")));
                        intent.putExtra(Constants.longitude, PreferencesUtils.getData(Constants.longitude, getActivity(), ""));
                        intent.putExtra(Constants.duration, (PreferencesUtils.getData(Constants.training_duration, getActivity(), "")));
                        startActivity(intent);

                    } else {
                        // No match found..........
                        Toast.makeText(getActivity(), "No trainer found", Toast.LENGTH_SHORT).show();
                    }
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(getActivity());
                }
            } catch (JSONException e) {

            }
        }
    }
}
