package buddiapp.com.activity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.utils.AlertDialoge.SocialMediaURLAlertDialog;
import buddiapp.com.utils.CircleImageView;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;
import buddiapp.com.utils.Utility;

public class ProfileScreen extends AppCompatActivity {
    CountryCodePicker ccp;
    JSONObject socialMediaLinks = new JSONObject();
    String smobile;
    boolean editflag = false;
    private String userChoosenTask;
    boolean isValid = false;
    Uri image_uri;
    ImageView facebook, instagram, twitter, youtube;
    EditText rbmale;
    EditText firstName, lastName, eMail, password, mobile;
    CircleImageView userImageView, trainerImageView;
    LinearLayout trainerCategory, placeLayout, imageTrainer, imageUser;
    String semail, sfname, slname, sgender = "", scountrycode, spassword, sfacebookId = "", sgoogleplusId = "";
    String register_type = "normal", facebookUrl="", instagramUrl="", twitterUrl="", youtubeUrl="";
    private PopupMenu popupMenu;
    String user_image;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    int REQUEST_CROP_PICTURE = 222;
    String imageurl = "";
    Menu menu;
    TextView fullname, typeAge, height, weight, category;
    String fbusername, snapchatusername, twitterusername, linkedinusername, instagramusername, youtubeusername;
    SocialMediaURLAlertDialog dialog;
    String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        fullname = (TextView) findViewById(R.id.fullname);
        typeAge = (TextView) findViewById(R.id.type_age);
        height = (TextView) findViewById(R.id.height);
        weight = (TextView) findViewById(R.id.weight);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        eMail = (EditText) findViewById(R.id.email);
//        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);

        rbmale = (EditText) findViewById(R.id.male);
        category = (TextView) findViewById(R.id.category);
        userImageView = (CircleImageView) findViewById(R.id.userimageView);
        trainerCategory = (LinearLayout) findViewById(R.id.trainer_category);
        placeLayout = (LinearLayout) findViewById(R.id.place_layout);
        trainerImageView = (CircleImageView) findViewById(R.id.trainerimageView);
        imageTrainer = (LinearLayout) findViewById(R.id.image_trainer); // Trainer profile image View layout
        imageUser = (LinearLayout) findViewById(R.id.image_user); // user profile image View layout

        facebook = (ImageView) findViewById(R.id.nav_facebook);
        instagram = (ImageView) findViewById(R.id.nav_instagram);
        twitter = (ImageView) findViewById(R.id.nav_twitter);
        youtube = (ImageView) findViewById(R.id.nav_youtube);

        //****
        // *****check for trainer or trainee
        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
            trainerCategory.setVisibility(View.VISIBLE);
            imageUser.setVisibility(View.GONE);
            CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image, getApplicationContext(), ""), trainerImageView, R.drawable.ic_account, R.drawable.ic_account);
            placeLayout.setVisibility(View.GONE);
            imageTrainer.setVisibility(View.VISIBLE);
        } else {
            imageUser.setVisibility(View.VISIBLE);
            trainerCategory.setVisibility(View.GONE);
            CommonCall.LoadImage(getApplicationContext(), PreferencesUtils.getData(Constants.user_image, getApplicationContext(), ""), userImageView, R.drawable.ic_account, R.drawable.ic_account);
            trainerCategory.setVisibility(View.GONE);
            placeLayout.setVisibility(View.VISIBLE);
        }
        // load profile --->
        loadProfile();

        new getProfile().execute();


        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrainerCategory.class);
                startActivity(intent);
            }
        });

        trainerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userImageView.performClick();
            }
        });
        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileScreen.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(ProfileScreen.this);

                        if (items[item].equals("Take Photo")) {
                            userChoosenTask = "Take Photo";

                            cameraIntent();

                        } else if (items[item].equals("Choose from Library")) {
                            userChoosenTask = "Choose from Library";

                            galleryIntent();

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showDialogpop("Enter or paste your facebook link", "facebook", facebookUrl);

            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogpop("Enter or paste your instagram link", "instagram", instagramUrl);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogpop("Enter or paste your twitter link", "twitter", twitterUrl);
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogpop("Enter or paste your youtube link", "youtube", youtubeUrl);
            }
        });
    }


    private void galleryIntent() {
                /*Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        startActivityForResult(getIntent, RESULT_LOAD_IMAGE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        image_uri = getOutputMediaFileUri(1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, CAMERA_REQUEST);

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
                "BuddiApp.com/Profile");
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item).setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item:
                if (!editflag) {
                    item.setIcon(R.drawable.ic_check_white_24dp);
                    editflag = true;
//                    userImageView.setClickable(true);
                    editProfile();

                } else {
                    if (validateFeelds()) {
                        item.setIcon(R.drawable.ic_edit_white);
                        editflag = false;
                        if (CommonCall.isNetworkAvailable()) {
                            Toast.makeText(this, "Saving Please wait...", Toast.LENGTH_SHORT).show();
                            //                        userImageView.setClickable(false);
                            new updateProfile().execute();
                        }
                    }
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void editProfile() {

        firstName.setEnabled(true);
        ccp.setCcpClickable(true);
        lastName.setEnabled(true);
        facebook.setEnabled(true);
        twitter.setEnabled(true);
        instagram.setEnabled(true);
        youtube.setEnabled(true);
        eMail.setEnabled(false);


//        password.setEnabled(false);

//        mobile.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // loading profile from db
    private void loadProfile() {

        facebook.setEnabled(false);
        twitter.setEnabled(false);
        instagram.setEnabled(false);
        youtube.setEnabled(false);

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
            if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainer")) {
                CommonCall.LoadImage(getApplicationContext(), imageurl, trainerImageView, R.drawable.ic_account, R.drawable.ic_account);

            } else {
                CommonCall.LoadImage(getApplicationContext(), imageurl, userImageView, R.drawable.ic_account, R.drawable.ic_account);
            }

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
            CommonCall.showLoader(ProfileScreen.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.token, PreferencesUtils.getData(Constants.token, getApplicationContext(), ""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
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
                    if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals(Constants.trainer)) {
                        fullname.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));

                        if (jsonObject.getString("age").toString().equals("null")) {
                            typeAge.setText("Trainer");
                        } else {
                            typeAge.setText("Trainer(" + jsonObject.getString("age") + ")");
                        }
                        if (jsonObject.getString("height").toString().equals("null")) {
                            height.setVisibility(View.GONE);
                        } else {
                            height.setText(jsonObject.getString("height"));
                        }
                        if (jsonObject.getString("weight").toString().equals("null")) {
                            weight.setVisibility(View.GONE);
                        } else {
                            weight.setText(jsonObject.getString("weight"));
                        }
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("social_media_links"));
                        JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonArray.getJSONObject(0)));

                        if (jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook").equals("null") || jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook").length()==0) {
                            facebookUrl = "";
                        } else{
                            facebookUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("facebook");
                        }
                        if (jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram").equals("null")|| jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram").length()==0) {
                            instagramUrl = "";
                        } else {
                            instagramUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("instagram");
                        }
                        if (jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter").equals("null")|| jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter").length()==0) {
                            twitterUrl = "";
                        } else {
                            twitterUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("twitter");
                        }
                        if (jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube").equals("null")|| jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube").length()==0)  {
                            youtubeUrl = "";
                        } else {
                            youtubeUrl = jsonObject1.getJSONObject(Constants.social_media_links).getString("youtube");
                        }

                        PreferencesUtils.saveData(Constants.category_submitted, obj.getJSONArray("category_submitted").toString(), getApplicationContext());
                        PreferencesUtils.saveData(Constants.category_approved, obj.getJSONArray("category_approved").toString(), getApplicationContext());

                    }
                    loadProfile();

                } else if (obj.getInt(Constants.status) == 2) {
                    Toast.makeText(ProfileScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    //session out
                    CommonCall.sessionout(ProfileScreen.this);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Updating profile
    class updateProfile extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String registerResponse;

        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(ProfileScreen.this);


        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                socialMediaLinks.put("facebook", facebookUrl);
                socialMediaLinks.put("instagram", instagramUrl);
                socialMediaLinks.put("youtube", youtubeUrl);
                socialMediaLinks.put("twitter", twitterUrl);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.social_media_links,socialMediaLinks);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(0,jsonObject);

                reqData.put("first_name", sfname);
                reqData.put("last_name", slname);
                reqData.put("mobile", smobile);
                reqData.put("gender", sgender);
                reqData.put("user_image", user_image);
                reqData.put("social_media_links", jsonArray.toString());
                reqData.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));
                reqData.put("profile_desc", "Description");

                registerResponse = NetworkCalls.POST(Urls.getEditTraineeProfileURL(), reqData.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return registerResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.email, jsonObject.getString(Constants.email), getApplicationContext());
                    PreferencesUtils.saveData(Constants.fname, jsonObject.getString(Constants.fname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.lname, jsonObject.getString(Constants.lname), getApplicationContext());
                    PreferencesUtils.saveData(Constants.user_image, jsonObject.getString(Constants.user_image), getApplicationContext());
                    PreferencesUtils.saveData(Constants.gender, jsonObject.getString(Constants.gender), getApplicationContext());
                    PreferencesUtils.saveData(Constants.mobile, jsonObject.getString(Constants.mobile), getApplicationContext());
                    PreferencesUtils.saveData(Constants.social_media_links, jsonObject.getString(Constants.social_media_links),getApplicationContext());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loadProfile();

                        }
                    }, 3000);
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(ProfileScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    CommonCall.sessionout(ProfileScreen.this);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }


    /********************** Field validation *******************/

    private boolean validateFeelds() {

        try {

            scountrycode = ccp.getSelectedCountryCode();
            smobile = String.valueOf(mobile.getText());
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(smobile, ccp.getSelectedCountryNameCode());
            CommonCall.PrintLog("Phone number++", swissNumberProto + "");
            isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
            if (isValid) {
                CommonCall.PrintLog("Phone number", swissNumberProto + "");
                smobile = "+" + scountrycode + "-" + smobile;
            } else
                CommonCall.PrintLog("Invalid", "Invalid");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        View focusView = null;
        if (firstName.getText().length() == 0) {
            firstName.setError("Invalid Firstname");
            focusView = firstName;
            focusView.requestFocus();
            return false;
        } else if (lastName.getText().length() == 0) {
            lastName.setError("Invalid Lastname");
            focusView = lastName;
            focusView.requestFocus();
            return false;
        } else if (mobile.getText().length() == 0) {
            mobile.setError("Please enter your mobile number");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        } else if (!isValid) {
            mobile.setError("Please check your mobile number and country code");
            focusView = mobile;
            focusView.requestFocus();
            return false;
        }
// else if (sgender.length() == 0) {
////            Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_SHORT).show();
////            focusView.requestFocus();
//            return false;
//        }
        else if (imageurl.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please select profile image", Toast.LENGTH_SHORT).show();
            return false;
        }
        /* else if (password.getText().length() == 0) {
            password.setError("Please enter password");
            focusView = password;
            focusView.requestFocus();
            return false;
        } else if (password.getText().length() < 8) {
            password.setError("Password must be 8 characters or more");
            focusView = password;
            focusView.requestFocus();
            return false;
        } */
        else {
            sfname = firstName.getText().toString();
            slname = lastName.getText().toString();
            semail = eMail.getText().toString();
            user_image = imageurl;
//            spassword = password.getText().toString();
            return true;
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

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {


            try {
                imageurl = image_uri.getPath();
//                userImageView.setImageURI(Uri.parse(imageurl));
                new upLoad().execute();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("imageeeeeeeeee exception");
                e.printStackTrace();
            }


        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();


//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;

            //Returns null, sizes are in the options variable
//            Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
//            System.out.println("wistdh" + options.outWidth);
            imageurl = getPathFromUri(selectedImage);

            Log.e("CUrent source", "gallery");

            new upLoad().execute();

        }

    }


    class upLoad extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(ProfileScreen.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            response = NetworkCalls.UPLOAD(imageurl, Urls.getUPLOADURL());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    imageurl = obj.getString("Url");
                    if (validateFeelds())
                        new updateProfile().execute();


                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(ProfileScreen.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    CommonCall.sessionout(ProfileScreen.this);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialogpop(String dialog_title, final String from, String surl) {
        String title = dialog_title;
        String sUrl = surl;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        if(sUrl.length()>5){
            input.setText(sUrl);
            input.setEnabled(false);
            builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    input.setEnabled(true);
                }
            });
        }else{
            input.setEnabled(true);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if (m_Text.length() > 0)
                    savedata(m_Text, from);

            }
        });
        builder.show();
    }


    /********************* saving social media url  **********/
    public void savedata(String text, String from) {
        switch (from) {
            case "facebook":
                facebookUrl = text;
                Log.e("youtube url", text);
                break;
            case "instagram":
                instagramUrl = text;
                break;
            case "twitter":
                twitterUrl = text;
                break;
            case "youtube":
                youtubeUrl = text;
                Log.e("youtube url", text);
                break;
            default:
                break;
        }
    }

}