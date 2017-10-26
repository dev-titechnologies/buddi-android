package buddyapp.com.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.activity.WelcomeActivity;
import buddyapp.com.services.LocationService;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static buddyapp.com.Settings.Constants.start_session;

/**
 * Created by Ajay on 15/6/16.
 */
public class CommonCall {
    public static DisplayImageOptions getOptions(int loadingImage, int errorImageResourse) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(loadingImage) // resource or drawable
                .showImageForEmptyUri(errorImageResourse) // resource or drawable
                .showImageOnFail(errorImageResourse) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(true) // default

                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.RGB_565) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default

                .build();
        return options;

    }


    public static void LoadImage(final Context context, final String path,
                                 final ImageView imgView, int loadingImage, int errorImageResourse) {
        try {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(loadingImage) // resource or drawable
                    .showImageForEmptyUri(errorImageResourse) // resource or drawable
                    .showImageOnFail(errorImageResourse) // resource or drawable
                    .resetViewBeforeLoading(false)  // default
                    .cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(true) // default

                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default

                    .build();

            ImageLoader.getInstance().displayImage(path, imgView, options);


        } catch (OutOfMemoryError e) {
            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().clearMemoryCache();
            System.gc();
            Runtime.getRuntime().gc();

            Toast.makeText(context, "MEMORY FULL", 2000).show();

//            CommonCall.PrintLog("OUT OF MEMORY CAUGHT", "OUT OF MEMORY CAUGHT");


//            reload image if out of memory occur
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(loadingImage) // resource or drawable
                    .showImageForEmptyUri(errorImageResourse) // resource or drawable
                    .showImageOnFail(errorImageResourse) // resource or drawable
                    .resetViewBeforeLoading(false)  // default
                    .cacheInMemory(true) // default
                    .cacheOnDisk(true) // default
                    .considerExifParams(true) // default

                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                    .bitmapConfig(Bitmap.Config.RGB_565) // default
                    .displayer(new SimpleBitmapDisplayer()) // default
                    .handler(new Handler()) // default

                    .build();

            ImageLoader.getInstance().displayImage(path, imgView, options);


        }

        /*CommonMethods.getUniversalImageLoader().displayImage(path,imgView);*/
    }


    public static void clearCache() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }

    public static void PrintLog(String s1, String s2) {

        Log.e(s1, s2);

    }

    public static String getToken(Context context) {
        return PreferencesUtils.getData(Constants.token, context, "");

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
            if (Hours >= 48)
                Hours = Hours + Mins;
            time = Hours;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String convertTime1(String date) {

        String formattedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyy, hh:mm aa");
        try {
            formattedDate = sdf.format((Iso8601.toCalendar(date).getTime()));

//            formattedDate = Long.parseLong(getTimeAgo(Iso8601.toCalendar(date).getTimeInMillis()));
//            formattedDate = Iso8601.toCalendar(date).getTimeInMillis();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "just now.";
//            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    /*public static String convertTime(String date) {
        long now = System.currentTimeMillis();
        String res="";
        if (convertTime1(date) > now || convertTime1(date) <= 0) {
            res= "just now.";
//            return null;
        }else {
             res = DateUtils.getRelativeTimeSpanString(
                    convertTime1(date),
                    now,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE) + "";
            if (res.equals("0 min. ago"))
                res = "just now";
         else   if (res.equals("0 mins ago"))
                res = "just now";
        }
                return res;
    }*/


    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    /*
    *
    * 1000 to 1K Converter
    *
    * */
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


    public static ProgressDialog pd;

    public static void showLoader(Activity yourActivity, String msg) {

        pd = new ProgressDialog(yourActivity);
        pd.setMessage(msg);
        pd.setCancelable(false);
        if (!((Activity) yourActivity).isFinishing()) {
            pd.show();
        }


    }


    public static void showLoader(Activity yourActivity) {

        pd = new ProgressDialog(yourActivity);
        pd.setMessage("loading");
        pd.setCancelable(false);
        if (!((Activity) yourActivity).isFinishing()) {
            pd.show();
        }


    }

    public static void hideLoader() {
        if (pd != null) {
            try {
                pd.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Controller.getAppContext().getSystemService(Controller.getAppContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    // log out **************
    public static void sessionout(Context context) {

        String fcmId = PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "");

        PreferencesUtils.cleardata(context);

        context.stopService(new Intent(context, LocationService.class));
        PreferencesUtils.saveData(Constants.device_id, fcmId, Controller.getAppContext());


        LoginManager.getInstance().logOut();
        Intent intent = new Intent(context, WelcomeActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }


    public static void emitTrainerLocation(final double lat, final double lng) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", Urls.BASEURL + String.format("/location/addLocation/"));

                    JSONObject object = new JSONObject();

                    object.put("user_id", PreferencesUtils.getData(Constants.user_id, Controller.getAppContext(), ""));
                    object.put("latitude", lat);
                    object.put("longitude", lng);
//                    object.put("avail_status",PreferencesUtils.getData(Constants.availStatus,Controller.getAppContext(),""));

                    if (PreferencesUtils.getData(Constants.start_session, Controller.getAppContext(), "false").equals("false"))

                        object.put("avail_status", "online");
                    else
                        object.put("avail_status", "booked");


                    jsonObject.put("data", object);
                    Controller.mSocket.emit("post", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void socketGetTrainerLocation() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", Urls.BASEURL + String.format("/location/receiveTrainerLocation"));

                    JSONObject object = new JSONObject();

                    object.put("user_id", PreferencesUtils.getData(Constants.user_id, Controller.getAppContext(), ""));
                    object.put("trainer_id", PreferencesUtils.getData(Constants.trainer_id, Controller.getAppContext(), ""));

                    jsonObject.put("data", object);
                    Controller.mSocket.emit("post", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void getSenderMessage() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", "http://git.titechnologies.in:4001" + String.format("/connectSocket/getMessages"));

                    JSONObject object = new JSONObject();

                    object.put("from_id", PreferencesUtils.getData(Constants.user_id, Controller.getAppContext(), ""));

                    if (PreferencesUtils.getData(Constants.user_type, Controller.getAppContext(), "").equals("trainee"))
                        object.put("to_id", PreferencesUtils.getData(Constants.trainer_id, Controller.getAppContext(), ""));
                    else
                        object.put("to_id", PreferencesUtils.getData(Constants.trainee_id, Controller.getAppContext(), ""));

                    jsonObject.put("data", object);
                    Controller.mSocket.emit("post", jsonObject);
                    CommonCall.PrintLog("getmsg", jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void intPayment(final Activity activity, final String sessionTime) {

        /*
*
*
* getting payment
*
* */
        CommonCall.showLoader(activity);


        {


//                if (PreferencesUtils.getData(Constants.clientToken,activity,"").length()==0)

            {

                final String nounce = "";


                if (sessionTime.equals("40"))
                    if (PreferencesUtils.getData(Constants.transactionId40, activity, "").length() > 1) {
                        PreferencesUtils.saveData(Constants.duration, "40", activity);

                    } else {

                        PreferencesUtils.saveData(Constants.duration, "0", activity);
                    }

                if (sessionTime.equals("60"))
                    if (PreferencesUtils.getData(Constants.transactionId60, activity, "").length() > 1) {
                        PreferencesUtils.saveData(Constants.duration, "60", activity);

                    } else {

                        PreferencesUtils.saveData(Constants.duration, "0", activity);
                    }


                if (PreferencesUtils.getData(Constants.transactionId40, activity, "").length() > 1
                        || PreferencesUtils.getData(Constants.transactionId60, activity, "").length() > 1)


                {

                    if (PreferencesUtils.getData(Constants.duration, activity, "").equals(sessionTime)) {

                        if (sessionTime.equals("40")) {
                            new extendSession(activity, PreferencesUtils.getData(Constants.transactionId40, activity, ""), PreferencesUtils.getData(Constants.bookid, activity, ""), sessionTime).execute();

                        } else {

                            new extendSession(activity, PreferencesUtils.getData(Constants.transactionId60, activity, ""), PreferencesUtils.getData(Constants.bookid, activity, ""), sessionTime).execute();

                        }


                    } else {


                        AlertDialog.Builder builder;


                        builder = new AlertDialog.Builder(activity);

                        builder.setCancelable(false);
                        if (PreferencesUtils.getData(Constants.duration, activity, "").equals("40") && sessionTime.equals("60"))
                            builder.setMessage("You've already been paid for a 40 minutes session. If you proceed, 1 hour session amount will be deducted. Would you like to continue with 1 hour session ?");
                        else
                            builder.setMessage("You've already been paid for a 1 hour session. If you proceed, 40 minutes session amount will be deducted. Would you like to continue with 40 minute session ?");


                        builder.setTitle("Warning!")


                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue  with delete


                                        new checkoutforExtendSession(activity, nounce, sessionTime).execute();


                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        picSessionTimeDialog(activity);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }


                } else {

                    new checkoutforExtendSession(activity, nounce, sessionTime).execute();
                }
            }
//                else {
//                    Toast.makeText(activity, "Something went Wrong!", Toast.LENGTH_SHORT).show();
//                }

        }

    }

    public static class checkoutforExtendSession extends AsyncTask<String, String, String> {

        Activity activity;
        String time;

        public checkoutforExtendSession(Activity act, String nounce, String time) {
            this.activity = act;
            this.nounce = nounce;
            this.time = time;
        }

        String nounce;

        @Override
        protected String doInBackground(String... strings) {

            JSONObject req = new JSONObject();
            try {
//                req.put("nonce", nounce);
                req.put("training_time", time);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String res = NetworkCalls.POST(Urls.getcheckoutURL(), req.toString());
            return res;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    JSONObject data = response.getJSONObject("data");
                    PreferencesUtils.saveData(Constants.transactionId, data.getString("id"), activity);
                    if (time.equals("40")) {
                        PreferencesUtils.saveData(Constants.transactionId40, data.getString("id"), activity);
                        PreferencesUtils.saveData(Constants.amount40, data.getString("amount"), activity);

                        PreferencesUtils.saveData(Constants.transaction_status40, data.getString("status"), activity);

                    } else if (time.equals("60")) {
                        PreferencesUtils.saveData(Constants.transaction_status60, data.getString("status"), activity);
                        PreferencesUtils.saveData(Constants.amount60, data.getString("amount"), activity);

                        PreferencesUtils.saveData(Constants.transactionId60, data.getString("id"), activity);

                    }

                    Toast.makeText(activity, "Payment  Successful!", Toast.LENGTH_SHORT).show();

                    new extendSession(activity, data.getString("id"), PreferencesUtils.getData(Constants.bookid, activity, ""), time).execute();

                } else if (response.getInt(Constants.status) == 2) {
                    CommonCall.hideLoader();

                } else if (response.getInt(Constants.status) == 3) {
                    CommonCall.hideLoader();
                    CommonCall.sessionout(activity);
                }


            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
                CommonCall.hideLoader();
            }
        }

    }

    public static void picSessionTimeDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setContentView(R.layout.session_time_pic_dialog);

        dialog.setCancelable(false);


        dialog.show();

        TextView fourty = (TextView) dialog.findViewById(R.id.fourty);
        TextView onehour = (TextView) dialog.findViewById(R.id.onehour);
        fourty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                intPayment(activity, "40");


            }
        });

        onehour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
                intPayment(activity, "60");
            }
        });

    }

    public static AlertDialog dialogExtend = null;

    public static void showExtendBokingDialog(final Activity activity) {


        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialogExtend.isShowing()) {
                    dialogExtend.dismiss();

//                    Intent intent = new Intent(activity, HomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    activity.startActivity(intent);
//                    activity.finish();


                    PreferencesUtils.saveData(Constants.flag_rating, "true", activity);

                    String bookid = PreferencesUtils.getData(Constants.bookid, activity, "");


                    new CommonCall.timerUpdate(activity, "complete", bookid, "").execute();

                }
            }
        };

        DialogInterface.OnClickListener extbuilderdialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        handler.removeCallbacks(runnable);
                        picSessionTimeDialog(activity);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        handler.removeCallbacks(runnable);

//                        Intent intent = new Intent(activity, HomeActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        activity.startActivity(intent);
//                        activity.finish();


                        String bookid = PreferencesUtils.getData(Constants.bookid, activity, "");


                        PreferencesUtils.saveData(Constants.flag_rating, "true", activity);


                        new CommonCall.timerUpdate(activity, "complete", bookid, "").execute();

                        break;
                }
            }
        };


        final AlertDialog.Builder extbuilder = new AlertDialog.Builder(activity);
        extbuilder.setCancelable(false);
        extbuilder.setMessage("Are you sure you want to Extend this session?").setPositiveButton("Yes", extbuilderdialogClickListener)
                .setNegativeButton("No", extbuilderdialogClickListener);

        dialogExtend = extbuilder.create();
        dialogExtend.show();

        handler.postDelayed(runnable, 30000);

        dialogExtend.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);


            }
        });

    }


    public static class extendSession extends AsyncTask<String, String, String> {


        Activity activity;
        String transaction_id, book_id, extended_time;

        public extendSession(Activity activity, String transaction_id, String book_id, String extended_time) {

            this.activity = activity;
            this.transaction_id = transaction_id;
            this.book_id = book_id;
            this.extended_time = extended_time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            try {
                JSONObject req = new JSONObject();

                req.put("book_id", book_id);

                req.put("extended_time", extended_time);
                req.put("transaction_id", transaction_id);

                res = NetworkCalls.POST(Urls.getextendBookingURL(), req.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            CommonCall.hideLoader();
            try {
                JSONObject res = new JSONObject(s);
                if (res.getInt(Constants.status) == 1) {

                    Intent intent = new Intent("BUDDI_SESSION_EXTEND");
                    intent.putExtra("extend_time", extended_time);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                    Toast.makeText(activity, "Your Training Session Has Extented!", Toast.LENGTH_SHORT).show();


                } else if (res.getInt(Constants.status) == 2) {
                    Toast.makeText(activity, res.getString(Constants.message), Toast.LENGTH_SHORT).show();


                } else {


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public static class timerUpdate extends AsyncTask<String, String, String> {


        String type, bookid, reason;
        Activity activity;

        public timerUpdate(Activity act, String type, String bookid, String reason) {
            this.type = type;
            this.bookid = bookid;
            this.reason = reason;

            this.activity = act;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (activity != null)
                showLoader(activity);
        }

        @Override
        protected String doInBackground(String... strings) {

            String res = "";
            try {
                JSONObject req = new JSONObject();

                req.put("book_id", bookid);

                req.put("reason", reason);
                req.put("action", type);

                res = NetworkCalls.POST(Urls.getbookingActionURL(), req.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return res;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            PreferencesUtils.saveData(start_session, "false", Controller.getAppContext());
            if (activity != null)
                CommonCall.hideLoader();

            try {

                final JSONObject obj = new JSONObject(s);
                if (obj.getInt("status") == 1) {

                    if (type.equals("complete")) {
                        PreferencesUtils.saveData(Constants.flag_rating, "true", activity);
                    }

                    //clearing last payment id to avoid multiple payments
                    PreferencesUtils.saveData(Constants.transactionId, "", activity);


                    PreferencesUtils.saveData(Constants.timerstarted, "false", activity);
                    PreferencesUtils.saveData(Constants.trainee_Data, "", activity);
                    PreferencesUtils.saveData(Constants.trainer_Data, "", activity);


                    Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, HomeActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();


                } else if (obj.getInt("status") == 2) {
                    Toast.makeText(activity, obj.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (obj.getInt("status") == 3) {
                    Toast.makeText(activity, "Session out", Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(activity);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}