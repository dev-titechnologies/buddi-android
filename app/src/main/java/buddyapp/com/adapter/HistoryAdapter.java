package buddyapp.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Detail_history;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.Iso8601;

/**
 * Created by titech on 25/7/17.
 */

public class HistoryAdapter extends BaseAdapter {
    Context context;
    JSONArray jsonArray;
    String bookingId, traineeId,traineeName,trainerName,trainerId,samount,
    category,trainingStatus,paymentStatus,location,trained_date, desc, image, name,profileImage;
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
//        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.history_list_item, viewGroup, false);
            view.setTag(holder);
            holder = new CustomViewHolder();

            holder.background = (ImageView) view.findViewById(R.id.background);
            holder.trainedDate = (TextView) view.findViewById(R.id.trained_date);
            holder.description = (TextView) view.findViewById(R.id.description);
            holder.amount = (TextView) view.findViewById(R.id.amount);


//        } else {
//
//            holder = (CustomViewHolder) view.getTag();
//
//        }
        try{
            JSONObject jsonObject= jsonArray.getJSONObject(i);
            bookingId = jsonObject.getString("booking_id");
            traineeId = jsonObject.getString("trainee_id");
            traineeName= jsonObject.getString("trainee_name");
            trainerName= jsonObject.getString("trainer_name");
            trainerId= jsonObject.getString("trainer_id");
            profileImage =jsonObject.getString("profile_img");
            if (PreferencesUtils.getData(Constants.user_type,context,"").equals("trainer"))
                name=traineeName;
            else
                name=trainerName;

            final JSONArray array = new JSONArray(jsonObject.getString("category"));
            category = array.getJSONObject(0).getString("categoryName");

            image = array.getJSONObject(0).getString("categoryBookImage");
            CommonCall.LoadImage(context,image,holder.background,R.drawable.ic_no_image,R.drawable.ic_no_image);

            trainingStatus= jsonObject.getString("training_status");
            paymentStatus= jsonObject.getString("payment_status");
            location= jsonObject.getString("location");
            trained_date= jsonObject.getString("trained_date");
            samount = jsonObject.getString("amount");
            desc = category+" session with "+name;

            view.setTag(jsonObject);

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
            holder.amount.setText("$"+samount);
            holder.trainedDate.setText(CommonCall.convertTime1(trained_date));
            CommonCall.PrintLog("date",trained_date);
            holder.description.setText(category+" session with "+name);
            view.setTag(jsonObject);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject jsonObject =  new JSONObject(view.getTag().toString());
                        final JSONArray array = new JSONArray(jsonObject.getString("category"));
                        category = array.getJSONObject(0).getString("categoryName");

                        if (PreferencesUtils.getData(Constants.user_type,context,"").equals("trainer"))
                            name=jsonObject.getString("trainee_name");
                        else
                            name=jsonObject.getString("trainer_name");

                        desc = category+" session with "+name;

                    Intent intent = new Intent(context, Detail_history.class);
                    intent.putExtra("traineeName",jsonObject.getString("trainee_name"));
                    intent.putExtra("trainerName",jsonObject.getString("trainer_name"));
                    intent.putExtra("category",category);
                    intent.putExtra("trainingStatus",jsonObject.getString("training_status"));
                    intent.putExtra("paymentStatus",jsonObject.getString("payment_status"));
                    intent.putExtra("location",jsonObject.getString("location"));
                    intent.putExtra("trained_date",jsonObject.getString("trained_date"));
                    intent.putExtra("profileImage",jsonObject.getString("profile_img"));
                    intent.putExtra("desc",desc);
                    intent.putExtra("name",name);
                    intent.putExtra("amount",samount);
                    intent.putExtra("rating",jsonObject.getString("rating"));
                    intent.putExtra("image",array.getJSONObject(0).getString("categoryBookImage"));

                        if(!jsonObject.getString("start_time").equals("null")) {
                            String time = timeDifference(jsonObject.getString("start_time"), jsonObject.getString("end_time"));
                            intent.putExtra("duration", time+"");
                        }
                        else{
                            intent.putExtra("duration", "null");
                        }
                        if(jsonObject.getString("extended").equals("true"))
                            intent.putExtra("extended","true");
                        else{}

                    context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {

        TextView category, trainedDate,description,amount;
        CardView cat_card;
        ImageView background;

    }

    public static String timeDifference(String start,String end) {
        Date startTime;
        Date endTime;
        long mills;
        int time = 0;
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        try {
            startTime = sdf.parse(sdf.format((Iso8601.toCalendar(start).getTime())));
            endTime =sdf.parse(sdf.format((Iso8601.toCalendar(end).getTime())));
            mills = endTime.getTime() - startTime.getTime();
//            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;

            long seconds = mills / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            result =  hours % 24 + ":" + minutes % 60 + " Minutes" ;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
