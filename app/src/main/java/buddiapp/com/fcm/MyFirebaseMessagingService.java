package buddiapp.com.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.Controller;
import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.Fragment.HomeCategory;
import buddiapp.com.activity.HomeActivity;
import buddiapp.com.activity.RequestActivity;
import buddiapp.com.activity.SessionReady;
import buddiapp.com.activity.chat.ChatActivity;
import buddiapp.com.database.DatabaseHandler;
import buddiapp.com.utils.CommonCall;

import static buddiapp.com.Settings.Constants.start_session;
import static buddiapp.com.Settings.Constants.trainer_Data;
import static buddiapp.com.activity.MapTrainee.CountTimeout;


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
                handleDataMessage(json, remoteMessage.getNotification().getBody());
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
//            notificato viewtionUtils.playNotificationSound();


        }

//        else{
//            // If the app is in background, firebase itself handles the notification
//        }
    }

    private void handleDataMessage(JSONObject json, String title) {
        Log.e(TAG, "push json: " + json.toString());

        try {

            if (json.getInt("type") == 1) {
                if (CountTimeout != null)
                    CountTimeout.cancel();//to cancel the timmer for timeout in maptrainee
                CommonCall.hideLoader();
                JSONObject data = json.getJSONObject("data");
                PreferencesUtils.saveData(Constants.trainer_id, data.getJSONObject("trainer_details").getString("trainer_id"), getApplicationContext());
                PreferencesUtils.saveData(trainer_Data, data.toString(), getApplicationContext());

                PreferencesUtils.saveData(start_session, "true", getApplicationContext());

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

                DatabaseHandler db;
                db = new DatabaseHandler(getApplicationContext());



                Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
                resultIntent.putExtra("message", data.toString());

                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);

                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(resultIntent);


                /*if(PreferencesUtils.getData(Constants.twitterShare, getApplicationContext(),"").equals("true")){
                    CommonCall.postTwitter("I have booked a "+data.getString("training_time")+" Minutes "
                            + db.getCatName(data.getString("cat_id")) +" training session with "+data.getJSONObject("trainer_details").getString("trainer_first_name")+
                            " "+data.getJSONObject("trainer_details").getString("trainer_last_name")+" at "+
                            data.getString("pick_location"));
                    CommonCall.PrintLog("tweet","Success");
                }
                if(PreferencesUtils.getData(Constants.facebookShare, getApplicationContext(),"").equals("true")){
                    CommonCall.postFacebook("I have booked a "+data.getString("training_time")+" Minutes "
                            + db.getCatName(data.getString("cat_id")) +" training session with "+data.getJSONObject("trainer_details").getString("trainer_first_name")+
                            " "+data.getJSONObject("trainer_details").getString("trainer_last_name")+" at "+
                            data.getString("pick_location"));
                    CommonCall.PrintLog("fbshare","Success");
                }*/

            } else if (json.getInt("type") == 2) {
/** Session started **/
/** Complete session **/
                Intent resultIntent = new Intent(getApplicationContext(), SessionReady.class);
                resultIntent.putExtra("message", "");
                resultIntent.putExtra("push_session", "2");
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                PreferencesUtils.saveData(Constants.startSessionPush, "true", getApplicationContext());

// if trainee stop the session
                PreferencesUtils.saveData("stopped_s","true",getApplicationContext());
                Intent intent = new Intent("BUDDI_TRAINER_START");
                intent.putExtra("push_session", "2");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            } else if (json.getInt("type") == 3) {

                PreferencesUtils.saveData(start_session, "false", Controller.getAppContext());

                if(PreferencesUtils.getData("stopped_s", getApplicationContext(),"false").equals("true")){
                    PreferencesUtils.saveData("cancel_dialog","false", Controller.getAppContext());
                    PreferencesUtils.saveData("stopped_s","false",getApplicationContext());
                }else{
                    PreferencesUtils.saveData("cancel_dialog","true", Controller.getAppContext());
                }

                Intent resultIntent = new Intent(getApplicationContext(), HomeCategory.class);
                resultIntent.putExtra("message", "");

                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                Intent intent = new Intent("BUDDI_TRAINER_CANCEL");

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            } else if (json.getInt("type") == 4) {


                PreferencesUtils.saveData(Constants.timerstarted, "false", getApplicationContext());

                PreferencesUtils.saveData(start_session, "false", Controller.getAppContext());


                Intent resultIntent = new Intent(getApplicationContext(), HomeCategory.class);
                resultIntent.putExtra("message", "");
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                Intent intent = new Intent("BUDDI_TRAINER_STOP");

                sendBroadcast(intent);


            } else if (json.getInt("type") == 5) {
/******* Session Request *******/
                JSONObject data = json.getJSONObject("data");
                Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
                resultIntent.putExtra("message", data.toString());
                resultIntent.putExtra("title", title);
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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


            } else if (json.getInt("type") == 6) {


                if (PreferencesUtils.getData(Constants.user_type, Controller.getAppContext(), "").equals("trainer")) {
                    JSONObject data = json.getJSONObject("data");
                    Intent resultIntent = new Intent(getApplicationContext(), RequestActivity.class);
                    resultIntent.putExtra("message", data.toString());
                    resultIntent.putExtra("title", title);
                    showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);

                    Intent intent = new Intent("BUDDI_SESSION_EXTEND");
                    intent.putExtra("extend_time", data.getString("extend_time"));
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

            } else if (json.getInt("type") == 7) {


                if (PreferencesUtils.getData(Constants.user_type, Controller.getAppContext(), "").equals("trainer")) {

                    Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);

                    showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);


                }

            } else if (json.getInt("type") == 8) {

                JSONObject data = json.getJSONObject("data");
                Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                resultIntent.putExtra("message", data.toString());
                resultIntent.putExtra("title", title);
                showNotificationMessage(getApplicationContext(), "Buddi", title, "", resultIntent);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(resultIntent);
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

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //First time

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentText(getApplicationContext().getString(R.string.app_name))
                .setContentTitle("Session123")
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
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