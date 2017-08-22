package buddyapp.com.activity.questions;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static buddyapp.com.Settings.Constants.source_become_trainer;

public class DoneActivity extends AppCompatActivity {
Button exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        exit=(Button)findViewById(R.id.exit);
        new checkStatus().execute();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (source_become_trainer) {

                    PreferencesUtils.saveData(Constants.trainer_type,"true",getApplicationContext());
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


            }
        });
    }


    class checkStatus extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {

            JSONObject req = new JSONObject();
            try {
                req.put("user_id",PreferencesUtils.getData(Constants.user_id,getApplicationContext(),""));
                req.put("user_type",PreferencesUtils.getData(Constants.user_type,getApplicationContext(),""));


            } catch (JSONException e) {
                e.printStackTrace();
            }


            String res = NetworkCalls.POST(Urls.getcategoryStatusURL(),req.toString());



            return res;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);


            try {
                JSONObject obj= new JSONObject(res);
                CommonCall.PrintLog("result",obj.toString());
                if(obj.getInt("status")==1){

                    PreferencesUtils.saveData(Constants.approved, obj.getJSONObject("data").getJSONArray(Constants.approved).toString(), getApplicationContext());


                        PreferencesUtils.saveData(Constants.trainer_type,"true",getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                }else if(obj.getInt("status")==2){
                    Toast.makeText(DoneActivity.this,obj.getString("message"), Toast.LENGTH_SHORT).show();
                }else if(obj.getInt("status")==3){
                    CommonCall.sessionout(DoneActivity.this);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        return;
    }
}
