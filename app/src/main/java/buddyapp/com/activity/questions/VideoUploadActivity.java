package buddyapp.com.activity.questions;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
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
                        Constants.questionData.put("video data",videoUpload);

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
                if (checkVideoDurationValidation(vid) >= 30) {

                    videoUrl = getPathFromUri(vid);
                    upload.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);

                } else {

                    Toast.makeText(this, "Recorded video should be more than 30 seconds.", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == RESULT_LOAD_video) {
            if (resultCode == Activity.RESULT_OK) {
                Uri vid = data.getData();

                if (checkVideoDurationValidation(vid) >= 30) {


                    videoUrl = getPathFromUri(vid);
                    CommonCall.PrintLog("url video", videoUrl + "");
                    upload.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                } else {

                    Toast.makeText(this, "Selected video should be more than 30 seconds.", Toast.LENGTH_LONG).show();
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

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90);
//        intent.putExtra("EXTRA_VIDEO_QUALITY", 1);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1337);
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


    class uploadVideo extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(VideoUploadActivity.this);
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

                CommonCall.hideLoader();
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
