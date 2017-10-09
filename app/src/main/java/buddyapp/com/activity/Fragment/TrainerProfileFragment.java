package buddyapp.com.activity.Fragment;


import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import buddyapp.com.activity.ChooseSpecification;
import buddyapp.com.activity.ProfileScreen;
import buddyapp.com.activity.TrainerCategory;
import buddyapp.com.services.LocationService;
import buddyapp.com.utils.AlertDialoge.RatingDialog;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;
import buddyapp.com.utils.Utility;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static buddyapp.com.Controller.mSocket;
import static buddyapp.com.Controller.updateSocket;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainerProfileFragment extends Fragment {
    ImageView facebook, instagram,linkedin,snapchat,twitter,youtube;
    CountryCodePicker ccp;
    String smobile;
    boolean editflag = false;
    private String userChoosenTask;
    boolean isValid = false;
    Uri image_uri;
    TextView fullname, typeAge, height , weight, category;
    EditText rbmale;
    EditText firstName, lastName, eMail, password, mobile;
    CircleImageView  trainerImageView;
    LinearLayout trainerCategory,placeLayout, imageTrainer, imageUser;
    String email, sfname, slname, sgender = "", scountrycode, spassword, sfacebookId = "", sgoogleplusId = "";
    String register_type = "normal";
    private PopupMenu popupMenu;
    String user_image;
    private static final int CAMERA_REQUEST = 1888;
    private static int RESULT_LOAD_IMAGE = 1;
    int REQUEST_CROP_PICTURE = 222;
    String imageurl = "";
    Menu menu;
    Switch toggle;
    public TrainerProfileFragment() {
        // Required empty public constructor
    }
    LocationManager mLocationManager;
    RatingDialog ratingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_profile, container, false);
        toggle = (Switch) view.findViewById(R.id.togglebutton);
        if (PreferencesUtils.getData(Constants.availStatus, getActivity(), "").equals("online")) {
            toggle.setChecked(true);
        }

        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        eMail = (EditText) view.findViewById(R.id.email);
//        password = (EditText) view.findViewById(R.id.password);
        mobile = (EditText) view.findViewById(R.id.mobile);

        rbmale = (EditText) view.findViewById(R.id.male);
        fullname = (TextView) view.findViewById(R.id.fullname);
        typeAge = (TextView) view.findViewById(R.id.type_age);
        height = (TextView) view.findViewById(R.id.height);
        weight = (TextView) view.findViewById(R.id.weight);

        facebook = (ImageView) view.findViewById(R.id.nav_facebook);
        instagram = (ImageView) view.findViewById(R.id.nav_instagram);
        linkedin = (ImageView) view.findViewById(R.id.nav_linkedin);
        snapchat = (ImageView) view.findViewById(R.id.nav_snapchat);
        twitter = (ImageView) view.findViewById(R.id.nav_twitter);
        youtube = (ImageView) view.findViewById(R.id.nav_youtube);

        category = (TextView) view.findViewById(R.id.category);


        trainerCategory = (LinearLayout) view.findViewById(R.id.trainer_category);
        placeLayout = (LinearLayout) view.findViewById(R.id.place_layout);
        trainerImageView = (CircleImageView) view.findViewById(R.id.trainerimageView);
        imageTrainer = (LinearLayout) view.findViewById(R.id.image_trainer); // Trainer profile image View layout
        if(PreferencesUtils.getData(Constants.flag_rating,getActivity(),"false").equals("true")){
            PreferencesUtils.saveData(Constants.flag_rating,"false",getActivity());
            ratingDialog = new RatingDialog(getActivity());
            ratingDialog.show();
        }

  if (  PreferencesUtils.getData(Constants.availStatus, getActivity(), "").equals("online")) {
      updateSocket();
        mSocket.connect();
        toggle.setChecked(true);
        getActivity().startService(new Intent(getActivity(), LocationService.class));
        new updateStatus().execute();
  }else{
      getActivity().stopService(new Intent(getActivity(), LocationService.class));
  }


        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        //  checking online or offline
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }

                    updateSocket();
                        mSocket.connect();

                    PreferencesUtils.saveData(Constants.availStatus, "online", getActivity());
                        getActivity().startService(new Intent(getActivity(), LocationService.class));
                        new updateStatus().execute();

                    }
                else {
                    PreferencesUtils.saveData(Constants.availStatus, "offline", getActivity());
                    mSocket.disconnect();
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                    new updateStatus().execute();
                    Toast.makeText(getActivity(), "You are now Offline", Toast.LENGTH_SHORT).show();
                    // The toggle is disabled
                }
            }
        });

        loadProfile();

        new getProfile().execute();

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TrainerCategory.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void buildAlertMessageNoGps() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        toggle.setChecked(false);
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void loadProfile() {

        firstName.setEnabled(false);
        ccp.setCcpClickable(false);
        lastName.setEnabled(false);
        eMail.setEnabled(true);
        eMail.setEnabled(false);
//        password.setEnabled(false);
        mobile.setEnabled(false);

        try {

            firstName.setText(PreferencesUtils.getData(Constants.fname, getActivity(), ""));
            lastName.setText(PreferencesUtils.getData(Constants.lname, getActivity(), ""));
            eMail.setText(PreferencesUtils.getData(Constants.email, getActivity(), ""));
            imageurl = PreferencesUtils.getData(Constants.user_image, getActivity(), "");

            fullname.setText(PreferencesUtils.getData(Constants.fname, getActivity(), "") + " "+PreferencesUtils.getData(Constants.lname, getActivity(), ""));

            /*fullname.setText(jsonObject.getString(Constants.fname) + " "+jsonObject.getString(Constants.lname));
            typeAge.setText("Trainer("+jsonObject.getString("age")+")");
            height.setText(jsonObject.getString("height"));
            weight.setText(jsonObject.getString("weight"));
*/
            String combined = PreferencesUtils.getData(Constants.mobile, getActivity(), "");
            if (combined.contains("-")) {
                String[] parts = combined.split("-");
                scountrycode = parts[0].replace("+", "");
                smobile = parts[1];
                ccp.setCountryForPhoneCode(Integer.parseInt(scountrycode));
                mobile.setText(smobile);
            }
            String gender = PreferencesUtils.getData(Constants.gender, getActivity(), "");

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

                CommonCall.LoadImage(getActivity(), imageurl, trainerImageView, R.drawable.ic_account, R.drawable.ic_account);

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
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.token, PreferencesUtils.getData(Constants.token, getActivity(), ""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getActivity(), ""));
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
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
                    PreferencesUtils.saveData(Constants.email, jsonObject.getString(Constants.email), getActivity());
                    PreferencesUtils.saveData(Constants.fname, jsonObject.getString(Constants.fname), getActivity());
                    PreferencesUtils.saveData(Constants.lname, jsonObject.getString(Constants.lname), getActivity());
                    PreferencesUtils.saveData(Constants.user_image, jsonObject.getString(Constants.user_image), getActivity());
                    PreferencesUtils.saveData(Constants.gender, jsonObject.getString(Constants.gender), getActivity());
//                    PreferencesUtils.saveData(Constants.user_type, jsonObject.getString(Constants.user_type), getApplicationContext());
                    PreferencesUtils.saveData(Constants.mobile, jsonObject.getString(Constants.mobile), getActivity());
                    fullname.setText(jsonObject.getString(Constants.fname) + " "+jsonObject.getString(Constants.lname));
                    if(jsonObject.getString("age").toString().equals("null")){
                        typeAge.setText("Trainer");
                    }else
                        typeAge.setText("Trainer("+jsonObject.getString("age")+")");
                    if(jsonObject.getString("height").toString().equals("null")){
                        height.setVisibility(View.GONE);
                    }else{
                        height.setVisibility(View.VISIBLE);
                        height.setText(jsonObject.getString("height"));}
                    if(jsonObject.getString("weight").toString().equals("null")){
                        weight.setVisibility(View.GONE);
                    }else
                    { height.setVisibility(View.VISIBLE);
                        weight.setText(jsonObject.getString("weight"));}
                    loadProfile();

                } else if (obj.getInt(Constants.status) == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    //session out
                    CommonCall.sessionout(getActivity());
                    getActivity().finish();
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
            CommonCall.showLoader(getActivity());


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("first_name", sfname);
                reqData.put("last_name", slname);
                reqData.put("mobile", smobile);
                reqData.put("gender", sgender);
                reqData.put("user_image", user_image);
                reqData.put("user_type", PreferencesUtils.getData(Constants.user_type, getActivity(), ""));
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
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
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {
                    JSONObject jsonObject = obj.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.email, jsonObject.getString(Constants.email), getActivity());
                    PreferencesUtils.saveData(Constants.fname, jsonObject.getString(Constants.fname), getActivity());
                    PreferencesUtils.saveData(Constants.lname, jsonObject.getString(Constants.lname), getActivity());
                    PreferencesUtils.saveData(Constants.user_image, jsonObject.getString(Constants.user_image), getActivity());
                    PreferencesUtils.saveData(Constants.gender, jsonObject.getString(Constants.gender), getActivity());
                    PreferencesUtils.saveData(Constants.mobile, jsonObject.getString(Constants.mobile), getActivity());
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loadProfile();

                        }
                    }, 3000);
                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    CommonCall.sessionout(getActivity());
                    getActivity().finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    /*********
     * update trainer status -------
     *********/
    class updateStatus extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put(Constants.user_id, PreferencesUtils.getData(Constants.user_id, getActivity(), ""));
                reqData.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type, getActivity(), ""));
                reqData.put(Constants.availStatus, PreferencesUtils.getData(Constants.availStatus, getActivity(), ""));
                response = NetworkCalls.POST(Urls.getStatusURL(), reqData.toString());
            } catch (Exception e) {
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
                    JSONObject data = obj.getJSONObject("data");

                    Toast.makeText(getActivity(), "You are now Online", Toast.LENGTH_SHORT).show();
                } else if (obj.getInt("status") == 2) {
//                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    new updateStatus().execute();
                }else if (obj.getInt("status") == 3) {
                    Toast.makeText(getActivity(), "Session out", Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(getActivity());
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
            Toast.makeText(getActivity(), "Please select profile image", Toast.LENGTH_SHORT).show();
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
//            semail = eMail.getText().toString();
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
        if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), uri)) {
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

                return getDataColumn(getActivity(), contentUri, null, null);
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

                return getDataColumn(getActivity(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getActivity(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

//            userImageView.setImageURI(Uri.parse(imageurl));
            new upLoad().execute();
//                CommonMethods.createAlert(CommentsPage.this, "Please select an image with minimum width of 580"
//                        , "Alert");


        }
//		    if(requestCode == CAMERA_REQUEST && resultCode==0){
//		    	Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				//startActivityForResult(pickPhoto , 1);
//	            startActivityForResult(takePicture, CAMERA_REQUEST);
//		    }

    }


    class upLoad extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {
            response = NetworkCalls.UPLOAD(imageurl,Urls.getUPLOADURL());
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
                    if(validateFeelds())
                        new updateProfile().execute();


                }else if (obj.getInt("status") == 2) {
                    Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }  else {
                    CommonCall.sessionout(getActivity());
                    getActivity().finish();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
