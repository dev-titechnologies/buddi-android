package buddyapp.com.timmer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import buddyapp.com.R;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.utils.CommonCall;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "buddyapp.com.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;


    // Instance of this service.
     static BroadcastService sMe;




    @Override
        public void onCreate() {       
            super.onCreate();



        Log.i(TAG, "Starting timer...");
        sMe=this;
        CreateNotification();

        cdt=null;


            cdt = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);



                    long second = (millisUntilFinished / 1000) % 60;
                    long minute = (millisUntilFinished / (1000 * 60)) % 60;
                    long hour = (millisUntilFinished / (1000 * 60 * 60)) % 24;

                    String time = hour+":"+minute+":"+second;


                    updateTimerNoti(time);

                    bi.putExtra("countdown", time);
                    sendBroadcast(bi);
                }

                @Override
                public void onFinish()
                {
                    CommonCall.PrintLog(TAG, "Timer finished");


                    sessionCompleted("Session Completed");

                    bi.putExtra("countdown", "Session Completed");
                    sendBroadcast(bi);

                }
            };

            cdt.start();
        }

        @Override
        public void onDestroy() {


            stopSelf();
            CommonCall.PrintLog(TAG, "Timer cancelled");
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {





            return START_NOT_STICKY;

        }

        @Override
        public IBinder onBind(Intent arg0) {       
            return null;
        }

    NotificationCompat.Builder builder;
    NotificationManager mNotificationManager;
        void CreateNotification(){
             mNotificationManager =

                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //First time
            builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentText(getApplicationContext().getString(R.string.app_name))
                    .setContentTitle("Session")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(
                            PendingIntent.getActivity(getApplicationContext(), 10,
                                    new Intent(getApplicationContext(), HomeActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                    0)
                    );


            mNotificationManager.notify(100, builder.build());
            startForeground(100, builder.build());
//            startForeground(100, builder.build());
        }

        void updateTimerNoti(String text){
            //Second time
            builder.setContentTitle(text);
            mNotificationManager.notify(100, builder.build());

        }

    void sessionCompleted(String text){
        //Second time


//        builder.setContentTitle(text);
//        builder.setOngoing(false);

//        mNotificationManager.notify(100, builder.build());


        stopForeground(true);
        createStopSessionNoti("Session Completed");

//        mNotificationManager.cancel(100);
//        mNotificationManager = null;
//        createStopSessionNoti(text);

    }

    void createStopSessionNoti(String text){




        mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentText(getApplicationContext().getString(R.string.app_name))
                .setContentTitle(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        PendingIntent.getActivity(getApplicationContext(), 10,
                                new Intent(getApplicationContext(), HomeActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                0)
                );


        mNotificationManager.notify(100, builder.build());
//        startForeground(100, builder.build());
    }
    /**
     * Called by the Android runtime to remove the notification icon.
     */
    public static void removeServiceNotification(
            int notificationId,
            Activity activity)
    {
        // We use a wrapper class to be backwards compatible.
        // Loading the wrapper class will throw an error on
        // platforms that does not support it.
        try
        {
            if (null != sMe)
            {
                new StopForegroundWrapper().
                        stopForegroundAndRemoveNotificationIcon(sMe);
            }
        }
        catch (java.lang.VerifyError error)
        {
            // We are below API level 5, and need to remove the
            // notification manually.
            NotificationManager mNotificationManager = (NotificationManager)
                    activity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
        }
    }

}