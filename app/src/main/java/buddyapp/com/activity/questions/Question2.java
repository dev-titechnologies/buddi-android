package buddyapp.com.activity.questions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.MultiSelectionSpinner;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class Question2 extends Activity {
    Button next;
    TextView yes_military_installations,no_military_installations,gymtext;
    MultiSelectionSpinner gym_sub;
    String military_installations_selected = "";
    ImageView back;
    RelativeLayout root;
    boolean check1= false, check2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);


        root=(RelativeLayout)findViewById(R.id.root);
        next=(Button)findViewById(R.id.next);
        gymtext=(TextView)findViewById(R.id.gymtext);
        yes_military_installations=(TextView)findViewById(R.id.yes_military_installations);
        no_military_installations=(TextView)findViewById(R.id.no_military_installations);
        gym_sub=(MultiSelectionSpinner)findViewById(R.id.gym);


        new getGYMList().execute();




        gym_sub.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                check2 = true;
                gymtext.setVisibility(View.GONE);
                gym_sub.setVisibility(View.VISIBLE);
                return false;
            }
        });

        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        yes_military_installations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check2)
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                military_installations_selected ="yes";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    yes_military_installations.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    no_military_installations.setBackgroundColor(getResources().getColor(R.color.white));

                    no_military_installations.setTextColor(getResources().getColor(R.color.black));
                    yes_military_installations.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });
        no_military_installations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check2)
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    yes_military_installations.setBackgroundColor(getResources().getColor(R.color.white));
                    no_military_installations.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    yes_military_installations.setTextColor(getResources().getColor(R.color.black));

                    no_military_installations.setTextColor(getResources().getColor(R.color.white));

                }
                military_installations_selected ="no";

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*if (gym_sub!=null)
                if ( ( gym_sub.getSelectedItemsAsString().trim().length()<2 || gym_sub.getSelectedItemsAsString().toString().equals(getString(R.string.please_select_gym)))) {


                    Toast.makeText(Question2.this, getString(R.string.please_select_gym), Toast.LENGTH_SHORT).show();


                }else*/
                    if (military_installations_selected.length()==0) {


                    Toast.makeText(Question2.this, "Please Select Military Installations", Toast.LENGTH_SHORT).show();

                }  else{

                    List<String> temp = new ArrayList<String>();
                    for (int i=0;i<gym_sub.getSelectedStrings().size();i++){

                        temp.add(gymlistid.get(gymlist.indexOf(gym_sub.getSelectedStrings().get(i))));
                    }


                    PreferencesUtils.saveData(Constants.gym_subscriptions,temp.toString(),getApplicationContext());

                    PreferencesUtils.saveData(Constants.military_installations,military_installations_selected,getApplicationContext());
                    startActivity(new Intent(getApplicationContext(), Question3.class));



                }
            }
        });
    }

    List<String> gymlist = new ArrayList<String>();
    List<String> gymlistid = new ArrayList<String>();
    class getGYMList extends AsyncTask<String, String, String> {

        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(Question2.this);


        }

        @Override
        protected String doInBackground(String... strings) {


            return NetworkCalls.POST(Urls.getGYMLISTURL(), reqData.toString());
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {





                    for (int i =0;i<response.getJSONArray("data").length();i++){

                        gymlist.add(response.getJSONArray("data").getJSONObject(i).getString("gym_name"));

                        gymlistid.add(response.getJSONArray("data").getJSONObject(i).getString("gym_id"));


                    }




                    gym_sub.setItems(gymlist);



                } else if (response.getInt(Constants.status) == 2) {

                    Snackbar snackbar = Snackbar
                            .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    Snackbar snackbar1 = null;

                                    snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);

                                    snackbar1.show();
                                    new getGYMList().execute();

                                }
                            });

                    snackbar.show();
                } else if (response.getInt(Constants.status) == 3) {


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
