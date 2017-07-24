package buddyapp.com.activity.questions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import buddyapp.com.R;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class VideoUploadActivity extends AppCompatActivity {
    public ArrayList<String> sub_cat_selectedID;

    ImageView next,record;

    TextView maleText, femaleText;
    DatabaseHandler db;

    JSONObject currentSubCat = new JSONObject();


    int currentPos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        db = new DatabaseHandler(getApplicationContext());
        sub_cat_selectedID = getIntent().getStringArrayListExtra("SUB_CAT");

        next = (ImageView) findViewById(R.id.next);

        record = (ImageView) findViewById(R.id.record);
        maleText = (TextView) findViewById(R.id.male_text);
        femaleText = (TextView) findViewById(R.id.female_text);



        setData(currentPos);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPos<sub_cat_selectedID.size()-1) {
                    currentPos++;


                    setData(currentPos);

                }
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordVideo();
            }
        });

    }


    String videoUrl= "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1337) {
            if(resultCode == Activity.RESULT_OK){

                Uri vid = data.getData();
//                CommonCall.PrintLog("url video",checkVideoDurationValidation(vid)+"");
                if (checkVideoDurationValidation(vid)>=30) {

                    videoUrl = getRealPathFromURI(vid);
//

                }else{

                    Toast.makeText(this, "Recorded video should be more than 30 seconds.", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public  long checkVideoDurationValidation( Uri uri){

        Cursor cursor = MediaStore.Video.query(getContentResolver(), uri, new
                String[]{MediaStore.Video.VideoColumns.DURATION});
        long duration = 0;
        if (cursor != null && cursor.moveToFirst()) {
            duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video
                    .VideoColumns.DURATION));
        }
        cursor.close();

        return TimeUnit.MILLISECONDS.toSeconds(duration);
    }
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90);
        intent.putExtra("EXTRA_VIDEO_QUALITY", 0);
        startActivityForResult(intent, 1337);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

void setData(int pos){


        /*
        * storing current sub cat details
        * */
    currentSubCat = db.getSubCat(sub_cat_selectedID.get(pos));

    try {


        getSupportActionBar().setTitle(currentSubCat.getString("subCat_name"));


        switch(currentSubCat.getString("subCat_name"))
        {

            case "Squat":{

                maleText.setText(R.string.male_squat_desc);

                femaleText.setText(R.string.female_squat_desc);

                break;
            }
            case "Deadlift":{

                maleText.setText(R.string.male_deadlift_desc);

                femaleText.setText(R.string.female_deadlift_desc);


                break;
            }

            case "Bench Press":{


                maleText.setText(R.string.male_bench_press_desc);

                femaleText.setText(R.string.female_bench_press_desc);
                break;
            }

            case "Snatch":{


                maleText.setText(R.string.male_snatch_desc);

                femaleText.setText(R.string.female_snatch_desc);
                break;
            }


            case "Clean & Jerk":{
                maleText.setText(R.string.male_cleanandjerk_desc);

                femaleText.setText(R.string.female_cleanandjerk_desc);

                break;
            }


        }

    } catch (JSONException e) {
        e.printStackTrace();
    }

}
}
