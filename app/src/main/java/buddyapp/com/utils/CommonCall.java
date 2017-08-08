package buddyapp.com.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import buddyapp.com.activity.Payments.PaymentType;
import buddyapp.com.activity.WelcomeActivity;

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

    public static void PrintLog(String s1,String s2){

        Log.e(s1,s2);

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
            if(Hours>=48)
                Hours=Hours+Mins;
            time = Hours;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static long convertTime1(String date) {

        long formattedDate = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        try {
//            formattedDate = sdf.format((Iso8601.toCalendar(date).getTime()));

//            formattedDate = Long.parseLong(getTimeAgo(Iso8601.toCalendar(date).getTimeInMillis()));
            formattedDate = Iso8601.toCalendar(date).getTimeInMillis();

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

    public static String convertTime(String date) {
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
    }








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




    public static   ProgressDialog pd;

public static void showLoader(Activity yourActivity){

     pd = new ProgressDialog(yourActivity);
    pd.setMessage("loading");
    pd.setCancelable(false);
    pd.show();
}

public static void hideLoader(){
    if (pd!=null){

        pd.dismiss();
    }
}


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Controller.getAppContext(). getSystemService(Controller.getAppContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    // log out **************
    public static void sessionout( Context context){
                    PreferencesUtils.saveData(Constants.token,"", context);
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(context,WelcomeActivity.class);
                    context.startActivity(intent);
    }

public static class checkout extends  AsyncTask<String,String,String>{

Activity activity;
    public checkout(Activity act){
        this.activity=act;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        CommonCall.showLoader(activity);
    }

    @Override
    protected String doInBackground(String... strings) {

JSONObject req= new JSONObject();
        try {
            req.put("user_id",PreferencesUtils.getData(Constants.user_id,activity,""));
            req.put("amount","100");//for testing only
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String res = NetworkCalls.POST(Urls.getcheckoutURL(),req.toString());
        return res;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
CommonCall.hideLoader();
        try {
            final JSONObject response = new JSONObject(s);

            if (response.getInt(Constants.status) == 1) {


                Toast.makeText(activity, "Payment  Successful!", Toast.LENGTH_SHORT).show();


            } else if (response.getInt(Constants.status) == 2) {

//                Snackbar snackbar = Snackbar
//                        .make(root, response.getString(Constants.message), Snackbar.LENGTH_INDEFINITE)
//                        .setAction("RETRY", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//
//                                Snackbar snackbar1 = null;
//
//                                snackbar1 = Snackbar.make(root, "Loading", Snackbar.LENGTH_SHORT);
//
//                                snackbar1.show();
//                                new PaymentType.applyPromo().execute();
//
//                            }
//                        });
//
//                snackbar.show();
            } else if (response.getInt(Constants.status) == 3) {

                CommonCall.sessionout(activity);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

}