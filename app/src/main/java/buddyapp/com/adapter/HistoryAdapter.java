package buddyapp.com.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;

/**
 * Created by titech on 25/7/17.
 */

public class HistoryAdapter extends BaseAdapter {
    Context context;
    JSONArray jsonArray;
    String bookingId, traineeId,traineeName,trainerName,trainerId,
    category,trainingStatus,paymentStatus,location,trained_date;
    public HistoryAdapter(Context context, JSONArray jsonArray){
        this.context = context;
        this.jsonArray = jsonArray;
    }
    @Override
    public int getCount() {
        return jsonArray.length()   ;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CustomViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.history_list_item, viewGroup, false);
            view.setTag(holder);
            holder = new CustomViewHolder();
            holder.trainer = (TextView) view.findViewById(R.id.trainer_txt);
            holder.trainee = (TextView) view.findViewById(R.id.trainee_txt);
            if(PreferencesUtils.getData(Constants.user_type,context,"").equals("trainer"))
                holder.trainee.setVisibility(View.VISIBLE);
            else
                holder.trainer.setVisibility(View.VISIBLE);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.training_status = (TextView) view.findViewById(R.id.training_status);
            holder.payment_status = (TextView) view.findViewById(R.id.training_status);
        } else {

            holder = (CustomViewHolder) view.getTag();

        }
        try{
            JSONObject jsonObject= jsonArray.getJSONObject(i);
            bookingId = jsonObject.getString("booking_id");
            traineeId = jsonObject.getString("trainee_id");
            traineeName= jsonObject.getString("trainee_name");
            trainerName= jsonObject.getString("trainer_name");
            trainerId= jsonObject.getString("trainer_id");
            category= jsonObject.getString("category");
            trainingStatus= jsonObject.getString("training_status");
            paymentStatus= jsonObject.getString("payment_status");
            location= jsonObject.getString("location");
            trained_date= jsonObject.getString("trained_date");

            holder.name.setText(trainerName);
            holder.training_status.setText(trainingStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {

        TextView name,category, training_status,payment_status,location,date, trainer, trainee;
        CardView cat_card;

    }
}
