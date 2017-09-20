package buddyapp.com;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.utils.AnalyticsTrackers;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.Urls;

import static buddyapp.com.utils.Urls.BASEURL;


public class Controller extends Application {
    private static Context context;
    private static Controller mInstance;
    public static Socket mSocket;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }
    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }
    @Override
    public void onCreate() {
        super.onCreate();

//        sAnalytics = GoogleAnalytics.getInstance(this);
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        initImageLoader(getApplicationContext());
        Controller.context = getApplicationContext();
        mInstance = this;

        if(mSocket==null || !mSocket.connected()){
            {
//                mSocket= null;
                    socket();

//                listenEvent();

//                if (PreferencesUtils.getData(Constants.token,context,"").length()>0 &&
//                        PreferencesUtils.getData(Constants.user_type,context,"").equals(Constants.trainer)&&
//                        PreferencesUtils.getData(Constants.availStatus,context,"online").equals("online"))
//                    mSocket.connect();
//                mSocket.connect();

            }
        }


    }
    public  static void chatConnect(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", Urls.BASEURL+String.format("/connectSocket/connectSocket/"));
//
//                    JSONObject object = new JSONObject();
//
//                    object.put("user_id", PreferencesUtils.getData(Constants.user_id,Controller.getAppContext(),""));
//                    object.put("trainer_id",PreferencesUtils.getData(Constants.trainer_id,Controller.getAppContext(),""));
//
//                    jsonObject.put("data",object);
                    Controller.mSocket.emit("post", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    void socket(){

        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;

            options.reconnection = true;
            options.query = "__sails_io_sdk_version=0.12.13&token=" + PreferencesUtils.getData(Constants.token,getAppContext(),""); // Added this line

            mSocket = IO.socket(BASEURL,options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                CommonCall.PrintLog("Socket","Connected");
            }
        });

        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                CommonCall.PrintLog("Socket","Disconnected");
            }
        });
    }


    public static boolean listenFlag=true;
    public static void listenEvent() {

        if (listenFlag)
    mSocket.on("message",  new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            listenFlag=false;
            final JSONObject jsonObject = (JSONObject)args[0];
            CommonCall.PrintLog("received socket", jsonObject.toString());
            try {
                if(jsonObject.getString("type").equals("location")) {

//                    JSONObject object = jsonObject.getJSONObject("message");

//                    sendBroadcastTrainerLocation(object.getString("latitude"), object.getString("longitude"));
//                    CommonCall.PrintLog("lat", object.getString("latitude"));
//                    CommonCall.PrintLog("lng", object.getString("longitude"));
//                    CommonCall.PrintLog("availabilityStatus", object.getString("availabilityStatus"));
                }else if(jsonObject.getString("type").equals("chat")){
                    JSONObject object = jsonObject.getJSONObject("message");
                    sendBroadcastChatMessage(object.getString("text"),object.getString("from_id"),
                            object.getString("from_name"),object.getString("from_img"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });
    }

        public static ImageLoader initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCache(new LargestLimitedMemoryCache(200 * 1024 * 1024));
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(200 * 1024 * 1024); // 150 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app


        // Initialize ImageLoader with configuration.
        ImageLoader imgLoader = ImageLoader.getInstance();
        imgLoader.init(config.build());
        return imgLoader;
    }

    public static Context getAppContext() {
        return Controller.context;
    }


    public static synchronized Controller getInstance() {
        return mInstance;
    }

    public static void updateSocket(){

        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;

            options.reconnection = true;
            options.query = "__sails_io_sdk_version=0.12.13&token=" + PreferencesUtils.getData(Constants.token,getAppContext(),""); // Added this line

            mSocket = IO.socket(BASEURL,options);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void sendBroadcastTrainerLocation(String lat,String lng) {
        {
            Intent intent = new Intent("SOCKET_BUDDI_TRAINER_LOCATION");
            intent.putExtra("trainer_latitude", lat);
            intent.putExtra("trainer_longitude",lng);
            LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(intent);
        }
    }

    public static void sendBroadcastChatMessage( String msg, String fromId,String fromName,String image) {
        Intent intent = new Intent("SOCKET_BUDDI_CHAT");
        intent.putExtra("CHAT_FROMID", fromId);
        intent.putExtra("CHAT_MESSAGE",msg);
        intent.putExtra("CHAT_NAME",fromName);
        intent.putExtra("CHAT_IMAGE",image);
        LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(intent);
    }

}