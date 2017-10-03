package buddyapp.com.activity;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import buddyapp.com.R;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.Iso8601;

public class Detail_history extends AppCompatActivity {

    ImageView background,profileImage;
    TextView name,category, training_status,payment_status,location,date, trainer, trainee,
            trainedDate,description, amount,textRated;
    RatingBar ratingBar;
    Float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Detail");

        trainedDate = (TextView) findViewById(R.id.trained_date);
        description = (TextView) findViewById(R.id.description);
        background = (ImageView) findViewById(R.id.background);
        profileImage = (ImageView) findViewById(R.id.profile_image);

        location = (TextView) findViewById(R.id.location);

        training_status = (TextView) findViewById(R.id.training_status);
        payment_status = (TextView) findViewById(R.id.payment_status);
        amount = (TextView) findViewById(R.id.amount);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        textRated = (TextView) findViewById(R.id.text_rated);

        trainedDate.setText(CommonCall.convertTime1(String.valueOf(getIntent().getExtras().get("trained_date"))));
        description.setText(getIntent().getExtras().getString("desc"));
        training_status.setText(getIntent().getExtras().getString("trainingStatus"));
        payment_status.setText(getIntent().getExtras().getString("paymentStatus"));
        amount.setText("$"+getIntent().getExtras().getString("amount"));
        textRated.setText("You rated "+getIntent().getExtras().getString("name"));

        if(getIntent().getExtras().getString("rating").equals("null"))
        rate =0f;
        else
        rate = Float.parseFloat(getIntent().getExtras().getString("rating"));

        ratingBar.setRating(rate);
        ratingBar.setClickable(false);

        try {
            CommonCall.LoadImage(getApplicationContext(), getIntent().getExtras().getString("image"), background, R.drawable.ic_account, R.drawable.ic_account);
            CommonCall.LoadImage(getApplicationContext(), getIntent().getExtras().getString("profileImage"), profileImage, R.drawable.ic_account, R.drawable.ic_account);

        }catch (Exception e){
            e.printStackTrace();
        }

        /*    intent.putExtra("traineeName",traineeName);
        intent.putExtra("trainerName",trainerName);
        intent.putExtra("category",category);
        intent.putExtra("trainingStatus",trainingStatus);
        intent.putExtra("paymentStatus",paymentStatus);
        intent.putExtra("location",location);
        intent.putExtra("trained_date",trained_date);
*/
         String CurrentString = getIntent().getExtras().getString("location");
            String[] separated = CurrentString.split("/");
            double lat = Double.parseDouble(separated[0]);
            double lng = Double.parseDouble(separated[1]);
            // location address
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e1){
            e1.printStackTrace();
        }
try{
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        location.setText(address);}
catch (Exception e){
    e.printStackTrace();
}
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default: return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public static int timeDifference(String date) {
        Date formattedDate;
        Date deviceDate;
        long mills;
        int time = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        try {
            formattedDate = sdf.parse(sdf.format((Iso8601.toCalendar(date).getTime())));
            deviceDate = Calendar.getInstance().getTime();
            mills = deviceDate.getTime() - formattedDate.getTime();
            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;
            if(Hours>=48)
                Hours=Hours+Mins;
            time = Hours;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
