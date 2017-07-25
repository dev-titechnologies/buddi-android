package buddyapp.com.activity.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.LoginScreen;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingHistory extends Fragment {


    public BookingHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                }
            } catch (JSONException e) {

            }
        }
    }
}
