package buddyapp.com.utils.AlertDialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

/**
 * Created by titech on 23/8/17.
 */

public class RatingDialog extends Dialog {
    ImageView userImage;
    TextView name, userType, submit;
    String rate;
    RatingBar rating;
    EditText comments;
    Activity context;
    String message;
    public RatingDialog(Activity context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_rating);
        setCancelable(false);
        userImage = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        userType = (TextView) findViewById(R.id.user_type);
        rating = (RatingBar) findViewById(R.id.rating);
        comments = (EditText) findViewById(R.id.comments);
        submit  = (TextView) findViewById(R.id.submit);

        if(PreferencesUtils.getData(Constants.user_type,context,"").equals("trainee")){

        if(PreferencesUtils.getData(Constants.trainer_image,context,"").length()>1)
            CommonCall.LoadImage(context,PreferencesUtils.getData(Constants.trainer_image,context,""),userImage,R.drawable.ic_account,R.drawable.ic_account);
        else
            userImage.setImageResource(R.drawable.ic_account);

        name.setText(PreferencesUtils.getData(Constants.trainer_name,context,"").toString());
        userType.setText("Trainer");

        }else{
            name.setText(PreferencesUtils.getData(Constants.trainee_name,context,"").toString());
            userType.setText("Trainee");

            if(PreferencesUtils.getData(Constants.trainee_image,context,"").length()>1)
             CommonCall.LoadImage(context,PreferencesUtils.getData(Constants.trainee_image,context,""),userImage,R.drawable.ic_account,R.drawable.ic_account);
         else
             userImage.setImageResource(R.drawable.ic_account);
        }
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = String.valueOf(rating.getRating());
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate = String.valueOf(rating.getRating());
                message = comments.getText().toString();
                new AddReview().execute();
            }
        });
    }
    private class AddReview extends AsyncTask<String, Void, String> {
        String response;
        JSONObject reqData = new JSONObject();
        @Override
        protected String doInBackground(String... url) {
            try {
                reqData.put("book_id", PreferencesUtils.getData(Constants.bookid,context,""));
                reqData.put("user_id",PreferencesUtils.getData(Constants.user_id,context,""));
                reqData.put("user_type",PreferencesUtils.getData(Constants.user_type,context,""));
                reqData.put("rating_count",rate);
                reqData.put("rating_comment",message);
                response = NetworkCalls.POST(Urls.getAddReviewURL(), reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    dismiss();
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 3) {
                    CommonCall.sessionout(context);
                } else {
                dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
