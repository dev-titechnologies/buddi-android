package buddyapp.com.activity.questions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

public class VideoUploadActivity extends AppCompatActivity {
    public ArrayList<String> sub_cat_selectedID;

    ImageView next, record, loadVideo;

    TextView maleText, femaleText, upload;
    DatabaseHandler db;

    JSONObject currentSubCat = new JSONObject();

    boolean allVideoUpload = false;
    int currentPos = 0;


    JSONObject videoUpload = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        db = new DatabaseHandler(getApplicationContext());
        sub_cat_selectedID = getIntent().getStringArrayListExtra("SUB_CAT");

        next = (ImageView) findViewById(R.id.next);
        loadVideo = (ImageView) findViewById(R.id.loadVideo);
        upload = (TextView) findViewById(R.id.upload);
        record = (ImageView) findViewById(R.id.record);
        maleText = (TextView) findViewById(R.id.male_text);
        femaleText = (TextView) findViewById(R.id.female_text);


        setData(currentPos);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoUrl.length() > 1) {
                    if (CommonCall.isNetworkAvailable())
                        new uploadVideo().execute();
                    else {
                        Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentPos < sub_cat_selectedID.size() - 1) {
                    currentPos++;




                    setData(currentPos);
                    next.setVisibility(View.GONE);
                    upload.setVisibility(View.GONE);

                    record.setEnabled(true);
                    loadVideo.setEnabled(true);


                } else {



                    try {
                        Constants.questionData.put("video_data",new JSONArray(videoUpload.toString()));

                        CommonCall.PrintLog("video data",Constants.questionData.toString());

                        if (CommonCall.isNetworkAvailable())
                            new completeQuestions().execute();
                        else {
                            Toast.makeText(getApplicationContext(), " Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }


            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allVideoUpload) {
                    upload.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                }
                videoUrl = "";
                recordVideo();
            }
        });
        loadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!allVideoUpload) {
                    upload.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                }
                videoUrl = "";
                galleryIntent();
            }
        });

    }


    String videoUrl = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1337) {
            if (resultCode == Activity.RESULT_OK) {

                Uri vid = data.getData();
                CommonCall.PrintLog("url video", data + "");
//                CommonCall.PrintLog("url video",checkVideoDurationValidation(vid)+"");
                if (checkVideoDurationValidation(vid) <= 90) {

                    videoUrl = getPathFromUri(vid);
                    upload.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);

                } else {

                    Toast.makeText(this, "Videos should be no longer than 90 seconds.", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == RESULT_LOAD_video) {
            if (resultCode == Activity.RESULT_OK) {
                Uri vid = data.getData();

                if (checkVideoDurationValidation(vid) <= 90) {


                    videoUrl = getPathFromUri(vid);
                    CommonCall.PrintLog("url video", videoUrl + "");
                    upload.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                } else {

                    Toast.makeText(this, "Videos should be no longer than 90 seconds.", Toast.LENGTH_LONG).show();
                }


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    public long checkVideoDurationValidation(Uri uri) {
        long timeInMillisec =0;
try {
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
    retriever.setDataSource(getApplicationContext(), uri);
    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
     timeInMillisec = Long.parseLong(time);
}catch (Exception e){
    e.printStackTrace();
}
        return TimeUnit.MILLISECONDS.toSeconds(timeInMillisec);
    }

    int RESULT_LOAD_video = 1336;

    private void galleryIntent() {
                /*Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("video/*");

        startActivityForResult(getIntent, RESULT_LOAD_video);
    }
    private final static int CAMERA_RQ = 1337;
    private void recordVideo() {
//        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90);
////        intent.putExtra("EXTRA_VIDEO_QUALITY", 1);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//        startActivityForResult(intent, 1337);
        File saveFolder = new File(Environment.getExternalStorageDirectory(), "Buddy App");
        saveFolder.mkdirs();


//            throw new RuntimeException("Unable to create save directory, make sure WRITE_EXTERNAL_STORAGE permission is granted.");

        new MaterialCamera(this)                               // Constructor takes an Activity
                .allowRetry(true)                                  // Whether or not 'Retry' is visible during playback
                .autoSubmit(false)                                 // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                .saveDir(saveFolder)                               // The folder recorded videos are saved to
                .primaryColorAttr(R.attr.colorPrimary)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                .showPortraitWarning(false)                         // Whether or not a warning is displayed if the user presses record in portrait orientation
                .defaultToFrontFacing(false)
                .countdownMinutes(1.5f)             // Whether or not the camera will initially show the front facing camera
//                .allowChangeCamera(true)                           // Allows the user to change cameras.
                .retryExits(false)                                 // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
                .restartTimerOnRetry(false)                        // If true, the countdown timer is reset to 0 when the user taps 'Retry' in playback
                .continueTimerInPlayback(false)                    // If true, the countdown timer will continue to go down during playback, rather than pausing.
                .videoEncodingBitRate(1024000)                     // Sets a custom bit rate for video recording.
                .audioEncodingBitRate(50000)                       // Sets a custom bit rate for audio recording.
                .videoFrameRate(24)                                // Sets a custom frame rate (FPS) for video recording.
                .qualityProfile(MaterialCamera.QUALITY_HIGH)       // Sets a quality profile, manually setting bit rates or frame rates with other settings will overwrite individual quality profile settings
                .videoPreferredHeight(720)                         // Sets a preferred height for the recorded video output.
                .videoPreferredAspect(4f / 3f)                     // Sets a preferred aspect ratio for the recorded video output.
                .maxAllowedFileSize(1024 * 1024 * 25)               // Sets a max file size of 5MB, recording will stop if file reaches this limit. Keep in mind, the FAT file system has a file size limit of 4GB.
                .iconRecord(R.drawable.mcam_action_capture)        // Sets a custom icon for the button used to start recording
                .iconStop(R.drawable.mcam_action_stop)             // Sets a custom icon for the button used to stop recording
                .iconFrontCamera(R.drawable.mcam_camera_front)     // Sets a custom icon for the button used to switch to the front camera
                .iconRearCamera(R.drawable.mcam_camera_rear)       // Sets a custom icon for the button used to switch to the rear camera
                .iconPlay(R.drawable.evp_action_play)              // Sets a custom icon used to start playback
                .iconPause(R.drawable.evp_action_pause)            // Sets a custom icon used to pause playback
                .iconRestart(R.drawable.evp_action_restart)        // Sets a custom icon used to restart playback
                .labelRetry(R.string.mcam_retry)                   // Sets a custom button label for the button used to retry recording, when available
                .labelConfirm(R.string.mcam_use_video)             // Sets a custom button label for the button used to confirm/submit a recording
//                .autoRecordWithDelaySec(5)                         // The video camera will start recording automatically after a 5 second countdown. This disables switching between the front and back camera initially.
//                .autoRecordWithDelayMs(5000)                       // Same as the above, expressed with milliseconds instead of seconds.
                .audioDisabled(false)                              // Set to true to record video without any audio.

                .start(CAMERA_RQ);                                 // Starts the camera activity, the result will be sent back to the current Activity


    }

    public String getPathFromUri(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(getApplicationContext(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(getApplicationContext(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getApplicationContext(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    void setData(int pos) {


        /*
        * storing current sub cat details
        * */
        currentSubCat = db.getSubCat(sub_cat_selectedID.get(pos));

        try {


            getSupportActionBar().setTitle(currentSubCat.getString("subCat_name"));


            switch (currentSubCat.getString("subCat_name")) {

                case "Squat": {

                    maleText.setText(R.string.male_squat_desc);

                    femaleText.setText(R.string.female_squat_desc);

                    break;
                }
                case "Deadlift": {

                    maleText.setText(R.string.male_deadlift_desc);

                    femaleText.setText(R.string.female_deadlift_desc);


                    break;
                }

                case "Bench Press": {


                    maleText.setText(R.string.male_bench_press_desc);

                    femaleText.setText(R.string.female_bench_press_desc);
                    break;
                }

                case "Snatch": {


                    maleText.setText(R.string.male_snatch_desc);

                    femaleText.setText(R.string.female_snatch_desc);
                    break;
                }


                case "Clean & Jerk": {
                    maleText.setText(R.string.male_cleanandjerk_desc);

                    femaleText.setText(R.string.female_cleanandjerk_desc);

                    break;
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    ProgressDialog pd;
    public  void showLoader(Activity yourActivity){

        pd = new ProgressDialog(yourActivity);
        pd.setMessage("Uploading");
        pd.setCancelable(false);
        pd.show();
    }

    class uploadVideo extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoader(VideoUploadActivity.this);
            try {
                reqData.put("file_name", videoUrl.substring(videoUrl.lastIndexOf('/') + 1));
                reqData.put("file_type", "vid");
                reqData.put("file", videoUrl);
                reqData.put("upload_type", "other");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... strings) {


            String res = NetworkCalls.UPLOADVideo(reqData, Urls.getUPLOADURL());


            return res;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                pd.dismiss();

                JSONObject obj = new JSONObject(s);
                if (obj.getInt(Constants.status) == 1) {


                    Toast.makeText(VideoUploadActivity.this, "Video Upload Success.", Toast.LENGTH_SHORT).show();

                    record.setEnabled(false);
                    loadVideo.setEnabled(false);

                    upload.setVisibility(View.GONE);
                    next.setVisibility(View.VISIBLE);


                    /*
                    * save data
                    *
                    * */

                    videoUpload.put("video_url",obj.getString("Url"));
                    videoUpload.put("subCat_name",currentSubCat.getString("subCat_name"));
                    videoUpload.put("subCat_id",currentSubCat.getString("subCat_id"));

                } else if (obj.getInt(Constants.status) == 2) {
                    Toast.makeText(VideoUploadActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    //session out
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    class completeQuestions extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            CommonCall.showLoader(VideoUploadActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = NetworkCalls.POST(Urls.getADDTRAINECATEGORYURL(), Constants.questionData.toString());

            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                CommonCall.hideLoader();
                JSONObject obj = new JSONObject(s);
                if (obj.getInt(Constants.status) == 1) {


                    Toast.makeText(VideoUploadActivity.this, "  Success.", Toast.LENGTH_SHORT).show();


                    PreferencesUtils.saveData(Constants.pending, ChooseCategory.cat_selectedID.toString(),getApplicationContext());
                    Intent exit = new Intent(getApplicationContext(), DoneActivity.class);
                    startActivity(exit);




                } else if (obj.getInt(Constants.status) == 2) {


                    Toast.makeText(VideoUploadActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();




                } else {


                    //session out



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
