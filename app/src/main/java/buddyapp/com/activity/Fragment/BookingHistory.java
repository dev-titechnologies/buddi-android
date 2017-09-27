package buddyapp.com.activity.Fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    public BookingHistory() {
        // Required empty public constructor
    }

ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=  inflater.inflate(R.layout.fragment_booking_history, container, false);
        list = (ListView)view.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        db = new DatabaseHandler(getActivity());
       if(db.getAllHistory().length()>1) {
           JSONArray jsonarray= db.getAllHistory();
           historyAdapter = new HistoryAdapter(getActivity(),jsonarray);
           list.setAdapter(historyAdapter);
       }else{
           new LoadBookingHistory().execute();
       }

       swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               swipeRefreshLayout.setRefreshing(true);
               db.deleteHistory();
               new LoadBookingHistory().execute();
           }
       });

        return  view;
    }

    class LoadBookingHistory extends AsyncTask<String,String,String> {
        JSONObject reqData = new JSONObject();
        String Response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
            swipeRefreshLayout.setRefreshing(false);
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
                    db.deleteHistory();
                    JSONArray jsonArray = obj.getJSONArray("data");
                    if(jsonArray.length()!=0){
                        JSONArray jsonarray = new JSONArray();
                        for(int i=jsonArray.length()-1; i>=0;i--)
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
                            jsonObject.put("profile_img",jsonObject.getString("profile_img"));
                            jsonObject.put("category",jsonObject.getString("category"));
                            jsonObject.put("amount",jsonObject.getString("amount"));
                            jsonarray.put(jsonObject);
                            db.insertHistroy(jsonObject);

                        }
                        jsonarray= db.getAllHistory();
                        historyAdapter = new HistoryAdapter(getActivity(),jsonarray);
                        list.setAdapter(historyAdapter);
                    }
                }else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(getActivity());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
