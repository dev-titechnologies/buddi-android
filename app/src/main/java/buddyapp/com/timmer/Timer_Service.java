package buddyapp.com.timmer;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.HomeActivity;
import buddyapp.com.utils.CommonCall;

public class Timer_Service extends Service {

    public static String str_receiver = "com.countdowntimerservice.receiver";

    private Handler mHandler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;

public static boolean stopFlag =false;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(str_receiver);
        CreateNotification();
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    strDate = simpleDateFormat.format(calendar.getTime());
                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();

                }

            });
        }

    }
    String bookid;
    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {


         bookid = intent.getStringExtra("bookid");

        return super.onStartCommand(intent, flags, startId);
    }

    public String twoDatesBetweenTime() {


        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (Exception e) {

        }

        try {
            date_diff = simpleDateFormat.parse(PreferencesUtils.getData("data",getApplicationContext(), ""));
        } catch (Exception e) {

        }

        try {


            long diff = date_current.getTime() - date_diff.getTime();
            int int_hours = Integer.valueOf(PreferencesUtils.getData("hours",getApplicationContext(), "0"));

            long int_timer = TimeUnit.MINUTES.toMillis(int_hours);
            long long_hours = int_timer - diff;
            long diffSeconds2 = long_hours / 1000 % 60;
            long diffMinutes2 = long_hours / (60 * 1000) % 60;
            long diffHours2 = long_hours / (60 * 60 * 1000) % 24;


            if (long_hours > 0) {
                String str_testing = diffHours2 + ":" + diffMinutes2 + ":" + diffSeconds2;

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
//                mEditor.putBoolean("finish", true).commit();
                mTimer.cancel();
                stopSelf();
            }
        }catch (Exception e){
            mTimer.cancel();
            mTimer.purge();


        }

        return "";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service finish","Finish");
        fn_update("Session Completed");
        sessionCompleted("Session Completed");
    }

    private void fn_update(String str_time){

        intent.putExtra("time",str_time);
        sendBroadcast(intent);

        updateTimerNoti(str_time);
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
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
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
//            startForeground(100, builder.build());
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


//        stopForeground(true);
        createStopSessionNoti("Session Completed");
        PreferencesUtils.saveData(Constants.timerstarted,"false",getApplicationContext());
        PreferencesUtils.saveData(Constants.trainee_Data,"",getApplicationContext());
        PreferencesUtils.saveData(Constants.trainer_Data,"",getApplicationContext());

if (!stopFlag)
        new CommonCall.timerUpdate(null,"complete",bookid).execute();
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
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))

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


    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher :  R.mipmap.ic_launcher ;
    }
}
