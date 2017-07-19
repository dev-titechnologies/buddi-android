package buddyapp.com;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LargestLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


public class Controller extends Application {
    private static Context context;
    private static Controller mInstance;

    @Override
    public void onCreate() {
        super.onCreate();


        initImageLoader(getApplicationContext());
        Controller.context = getApplicationContext();
        mInstance = this;


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


}