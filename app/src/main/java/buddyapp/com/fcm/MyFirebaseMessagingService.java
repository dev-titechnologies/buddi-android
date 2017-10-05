package buddyapp.com.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.Fragment.HomeCategory;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.activity.RequestActivity;
import buddyapp.com.activity.SessionReady;
import buddyapp.com.timmer.Timer_Service;
import buddyapp.com.utils.CommonCall;

import static buddyapp.com.Settings.Constants.start_session;
import static buddyapp.com.Settings.Constants.trainee_Data;
import static buddyapp.com.Settings.Constants.trainer_Data;
import static buddyapp.com.activity.MapTrainee.CountTimeout;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json,remoteMessage.getNotification().getBody());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
//        if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))

        {
// app is in foreground, broadcast the push message

//            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//            pushNotification.putExtra("message", message);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//            notificationUtils.playNotificationSound();


        }

//        else{
//            // If the app is in background, firebase itself handles the notification
//        }
    }

    private void handleDataMessage(JSONObject json,String title) {
        Log.e(TAG, "push json: " + json.toString());

        try {

            if (json.getInt("type")==1) {
                if (CountTimeout!=null)
                CountTimeout.cancel();//to cancel the timmer for timeout in maptrainee
                CommonCall.hideLoader();
                JSONObject data = json.getJSONObject("data");
                PreferencesUtils.saveData(Constants.trainer_id,data.getJSONObject("trainer_details").getString("trainer_id"),getApplicationContext());
                PreferencesUtils.saveData(trainer_Data,data.toString(),getApplicationContext());

                PreferencesUtils.saveData(start_session,"true",getApplicationContext());

//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
//            {
                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", data.toString());
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            }
// else

//                {
//
//                    // app is in background, show the notification in notification tray
                    Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
                    resultIntent.putExtra("message", data.toString());

                        showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);

                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
startActivity(resultIntent);


            }else if(json.getInt("type")==2){

/** Complete session **/
                Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
                resultIntent.putExtra("message","");
                resultIntent.putExtra("push_session","2");
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                PreferencesUtils.saveData(Constants.startSessionPush,"true",getApplicationContext());


                Intent intent = new Intent("BUDDI_TRAINER_START");
intent.putExtra("push_session","2");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }else if(json.getInt("type")==3){



                PreferencesUtils.saveData(start_session,"false", Controller.getAppContext());




                Intent resultIntent = new Intent(getApplicationContext(), HomeCategory.class);
                resultIntent.putExtra("message","");

                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                Intent intent = new Intent("BUDDI_TRAINER_CANCEL");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            }else if(json.getInt("type")==4){


                PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());

                PreferencesUtils.saveData(start_session,"false", Controller.getAppContext());



                Intent resultIntent = new Intent(getApplicationContext(), HomeCategory.class);
                resultIntent.putExtra("message","");
                showNotificationMessage(getApplicationContext(), "Buddi", title, "",resultIntent);


                Intent intent = new Intent("BUDDI_TRAINER_STOP");

                sendBroadcast(intent);


            }else if(json.getInt("type")==5){

                JSONObject data = json.getJSONObject("data");
                Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
                resultIntent.putExtra("message",data.toString());
                resultIntent.putExtra("title",title);
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

                startActivity(resultIntent);
                /*
*
*
* clearing notification of new request after 30 seconds
* */

//                Handler h = new Handler(Looper.getMainLooper());
//                long delayInMilliseconds = 30000;
//                h.postDelayed(new Runnable() {
//                    public void run() {
//
//                        NotificationUtils.clearNotifications(getApplicationContext());
//                    }
//                }, delayInMilliseconds);


            }
            else if(json.getInt("type")==6){


                if (PreferencesUtils.getData(Constants.user_type,Controller.getAppContext(),"").equals("trainer")) {
                    JSONObject data = json.getJSONObject("data");
                    Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    resultIntent.putExtra("title", title);
                    showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);

                    Intent intent = new Intent("BUDDI_SESSION_EXTEND");
                    intent.putExtra("extend_time",data.getString("extend_time"));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

            }



        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    /**
     * Showing notification with text only
     */
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }
    private void showNotificationMessage(Context context, String title, String message, String timeStamp) {

        //AppController.getSharedPreferences().edit().putString(Constants.CALL_CASE_ID, notifObject.getCaseDetails().getCaseID()).commit();
        Intent intent = new Intent(this, SessionReady.class);
        intent.putExtra("stop_session", "true");

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001, intent, 0);

        NotificationManager     mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //First time

        NotificationCompat.Builder    builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentText(getApplicationContext().getString(R.string.app_name))
                .setContentTitle("Session123")
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        pendingIntent
                );


        mNotificationManager.notify(1001, builder.build());

    }
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}