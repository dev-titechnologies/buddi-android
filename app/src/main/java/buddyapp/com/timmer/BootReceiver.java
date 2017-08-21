package buddyapp.com.timmer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.SessionReady;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (PreferencesUtils.getData(Constants.start_session, getApplicationContext(), "false").equals("true")) {

            context.startService(new Intent(getApplicationContext(), Timer_Service.class));

//            context.startActivity(new Intent(getApplicationContext(), SessionReady.class));

        }

    }
}