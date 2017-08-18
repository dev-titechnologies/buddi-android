package buddyapp.com.activity.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.LoginScreen;
import buddyapp.com.adapter.HistoryAdapter;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingHistory extends Fragment {
    DatabaseHandler db;
HistoryAdapter historyAdapter;
    public BookingHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = new DatabaseHandler(getActivity());
        new LoadBookingHistory().execute();
        return inflater.inflate(R.layout.fragment_booking_history, container, false);

    }

    class LoadBookingHistory extends AsyncTask<String,String,String> {
        JSONObject reqData = new JSONObject();
        String Response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id,getActivity(),""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type,getActivity(),""));
            }catch(Exception e){
                e.printStackTrace();
            }
            Response = NetworkCalls.POST(Urls.getBookingHistoryURL(),reqData.toString());
            return Response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                CommonCall.hideLoader();
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONArray jsonArray = obj.getJSONArray("data");
                    if(jsonArray.length()!=0){

                        for(int i=0; i<jsonArray.length();i++)
                        {
                            JSONObject jsonObject= jsonArray.getJSONObject(i);
                            jsonObject.put("booking_id",jsonObject.getString("booking_id"));
                            jsonObject.put("trainee_id",jsonObject.getString("trainee_id"));
                            jsonObject.put("trainee_name",jsonObject.getString("trainee_name"));
                            jsonObject.put("trainer_name",jsonObject.getString("trainer_name"));
                            jsonObject.put("trainer_id",jsonObject.getString("trainer_id"));
                            jsonObject.put("category",jsonObject.getString("category"));
                            jsonObject.put("training_status",jsonObject.getString("training_status"));
                            jsonObject.put("payment_status",jsonObject.getString("payment_status"));
                            jsonObject.put("location",jsonObject.getString("location"));
                            jsonObject.put("trained_date",jsonObject.getString("trained_date"));

                            db.insertHistroy(jsonObject);

                        }
//                        loadHistory(db.getAllHistory());
                        historyAdapter = new HistoryAdapter(getActivity(),jsonArray);
                    }
                }
            } catch (JSONException e) {

            }
        }
    }

    private void loadHistory(JSONArray allHistory) {

    }
}
