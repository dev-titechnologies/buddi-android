package buddyapp.com.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

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
            holder.category = (TextView) view.findViewById(R.id.category);
            holder.location = (TextView) view.findViewById(R.id.location);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.training_status = (TextView) view.findViewById(R.id.training_status);
            holder.payment_status = (TextView) view.findViewById(R.id.training_status);
            holder.date = (TextView) view.findViewById(R.id.date);
            if(PreferencesUtils.getData(Constants.user_type,context,"").equals("trainer")) {
                holder.trainee.setVisibility(View.VISIBLE);
                holder.trainer.setVisibility(View.GONE);
            } else{
                holder.trainer.setVisibility(View.VISIBLE);
                holder.trainee.setVisibility(View.GONE);

            }

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


/*          String CurrentString = location;
            String[] separated = CurrentString.split("/");
            double lat = Double.parseDouble(separated[0]);
            double lng = Double.parseDouble(separated[1]);
            // location address
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
*/
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();



            if (PreferencesUtils.getData(Constants.user_type,context,"").equals("trainer"))

            holder.name.setText(traineeName);
                else
                holder.name.setText(trainerName);

            holder.date.setText(trained_date);
            holder.location.setText(location);
            holder.category.setText(category);
            holder.payment_status.setText(paymentStatus);
            holder.training_status.setText(trainingStatus);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {

        TextView name,category, training_status,payment_status,location,date, trainer, trainee;
        CardView cat_card;

    }
}
