package buddyapp.com.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;
import buddyapp.com.utils.Utility;

public class TraineeProfileView extends AppCompatActivity {
    CountryCodePicker ccp;
    String smobile;
    boolean editflag = false;
    private String userChoosenTask;
    boolean isValid = false;
    Uri image_uri;

    EditText rbmale;
    EditText firstName, lastName, eMail, password, mobile;
    CircleImageView userImageView;
    LinearLayout placeLayout, imageTrainer, imageUser;
    String semail, sfname, slname, sgender = "", scountrycode, spassword, sfacebookId = "", sgoogleplusId = "";
    String register_type = "normal";
    private PopupMenu popupMenu;
    String user_image;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    int REQUEST_CROP_PICTURE = 222;
    String imageurl = "";
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_profile_view);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            ccp = (CountryCodePicker) findViewById(R.id.ccp);
            firstName = (EditText) findViewById(R.id.first_name);
            lastName = (EditText) findViewById(R.id.last_name);
            eMail = (EditText) findViewById(R.id.email);
            mobile = (EditText) findViewById(R.id.mobile);

            rbmale = (EditText) findViewById(R.id.male);

            userImageView = (CircleImageView) findViewById(R.id.userimageView);
            placeLayout = (LinearLayout) findViewById(R.id.place_layout);
            imageTrainer = (LinearLayout) findViewById(R.id.image_trainer); // Trainer profile image View layout
            imageUser = (LinearLayout) findViewById(R.id.image_user); // user profile image View layout
            //****
            // *****check for trainer or trainee

                imageUser.setVisibility(View.VISIBLE);
                CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image, getApplicationContext(), ""), userImageView,R.drawable.ic_account, R.drawable.ic_account);

                placeLayout.setVisibility(View.VISIBLE);

            // load profile --->
//            loadProfile();

            new getProfile().execute();

        }

    /*Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "BuddyApp.com/Profile");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                //Log.d("DitherMyProfile", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // loading profile from db
    private void loadProfile() {

        firstName.setEnabled(false);
        ccp.setCcpClickable(false);
        lastName.setEnabled(false);
        eMail.setEnabled(true);
        eMail.setEnabled(false);
//        password.setEnabled(false);
        mobile.setEnabled(false);

        try {
            firstName.setText(PreferencesUtils.getData(Constants.fname, getApplicationContext(), ""));
            lastName.setText(PreferencesUtils.getData(Constants.lname, getApplicationContext(), ""));
            eMail.setText(PreferencesUtils.getData(Constants.email, getApplicationContext(), ""));
            imageurl = PreferencesUtils.getData(Constants.user_image, getApplicationContext(), "");

            String combined = PreferencesUtils.getData(Constants.mobile, getApplicationContext(), "");
            if (combined.contains("-")) {
                String[] parts = combined.split("-");
                scountrycode = parts[0].replace("+", "");
                smobile = parts[1];
                ccp.setCountryForPhoneCode(Integer.parseInt(scountrycode));
                mobile.setText(smobile);
            }
            String gender = PreferencesUtils.getData(Constants.gender, getApplicationContext(), "");

            if (gender.equalsIgnoreCase("male")) {
                rbmale.setVisibility(View.VISIBLE);
                rbmale.setText("Male");
                sgender = "male";
            } else {
                rbmale.setVisibility(View.VISIBLE);
                rbmale.setText("Female");
                sgender = "female";
            }
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
            //Do something after 100ms

                CommonCall.LoadImage(getApplicationContext(), imageurl, userImageView, R.drawable.ic_account, R.drawable.ic_account);

            CommonCall.hideLoader();
//                }
//            }, 3000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // getting profile details from server
    class getProfile extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(TraineeProfileView.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.token, PreferencesUtils.getData(Constants.token, getApplicationContext(), ""));
                reqData.put(Constants.user_type, Constants.trainee);
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), ""));
                response = NetworkCalls.POST(Urls.getTraineeProfileURL(), reqData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt(Constants.status) == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.email, jsonObject.getString(Constants.email), getApplicationContext());
                    PreferencesUtils.saveData(Constants.fname, jsonObject.getString(Constants.fname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.lname, jsonObject.getString(Constants.lname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.user_image, jsonObject.getString(Constants.user_image), getApplicationContext());
                    PreferencesUtils.saveData(Constants.gender, jsonObject.getString(Constants.gender), getApplicationContext());
//                    PreferencesUtils.saveData(Constants.user_type, jsonObject.getString(Constants.user_type), getApplicationContext());
                    PreferencesUtils.saveData(Constants.mobile, jsonObject.getString(Constants.mobile), getApplicationContext());

                    loadProfile();

                } else if (obj.getInt(Constants.status) == 2) {
                    Toast.makeText(TraineeProfileView.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    //session out
                    CommonCall.sessionout(TraineeProfileView.this);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


    }
}