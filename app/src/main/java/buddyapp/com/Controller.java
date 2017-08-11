package buddyapp.com;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
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
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.Urls;

import static buddyapp.com.utils.Urls.BASEURL;


public class Controller extends Application {
    private static Context context;
    private static Controller mInstance;
    public static Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();


        initImageLoader(getApplicationContext());
        Controller.context = getApplicationContext();
        mInstance = this;

        if(mSocket==null || !mSocket.connected()){
            {
                    socket();
                listenEvent();
//                if (PreferencesUtils.getData(Constants.token,context,"").length()>0 &&
//                        PreferencesUtils.getData(Constants.user_type,context,"").equals(Constants.trainer)&&
//                        PreferencesUtils.getData(Constants.availStatus,context,"online").equals("online"))
//                    mSocket.connect();

            }
        }


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
    public static void listenEvent() {
    mSocket.on("message", new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            final JSONObject jsonObject = (JSONObject)args[0];
            CommonCall.PrintLog("received socket", jsonObject.toString());
            try {
                JSONObject object = jsonObject.getJSONObject("message");

                sendBroadcastTrainerLocation(object.getString("latitude"),object.getString("longitude"));
                CommonCall.PrintLog("lat", object.getString("latitude"));
                CommonCall.PrintLog("lng", object.getString("longitude"));
                CommonCall.PrintLog("availabilityStatus", object.getString("availabilityStatus"));
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



    }

    public static void sendBroadcastTrainerLocation(String lat,String lng) {
        {
            Intent intent = new Intent("SOCKET_BUDDI_TRAINER_LOCATION");
            intent.putExtra("trainer_latitude", lat);
            intent.putExtra("trainer_longitude",lng);
            LocalBroadcastManager.getInstance(getAppContext()).sendBroadcast(intent);
        }
    }


}