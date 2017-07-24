package buddyapp.com.activity.questions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import buddyapp.com.R;

import static buddyapp.com.activity.questions.Question4.sub_cat_selectedID;


public class BeforeVideoActivity extends AppCompatActivity {


Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_video);

        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), VideoUploadActivity.class).putExtra("SUB_CAT",sub_cat_selectedID));

            }
        });

        getSupportActionBar().setTitle(R.string.before_video_titile);
    }
}
