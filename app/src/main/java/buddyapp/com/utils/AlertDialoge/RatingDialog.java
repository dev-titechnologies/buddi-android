package buddyapp.com.utils.AlertDialoge;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import buddyapp.com.R;

/**
 * Created by titech on 23/8/17.
 */

public class RatingDialog extends Dialog {
    ImageView userImage;
    TextView name, userType, submit;
    String rate;
    RatingBar rating;
    EditText comments;
    public RatingDialog(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_rating);

        userImage = (ImageView) findViewById(R.id.image);
        name = (TextView) findViewById(R.id.name);
        userType = (TextView) findViewById(R.id.user_type);
        rating = (RatingBar) findViewById(R.id.rating);
        comments = (EditText) findViewById(R.id.comments);
        submit  = (TextView) findViewById(R.id.submit);

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
                String msg = comments.getText().toString();
            }
        });
    }
}
