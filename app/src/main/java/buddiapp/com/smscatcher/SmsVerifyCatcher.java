package buddiapp.com.smscatcher;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by Ajay on 8/15/16.
 */
public class SmsVerifyCatcher {
    private final static int PERMISSION_REQUEST_CODE = 12;

    private Activity activity;
    private Fragment fragment;
    private OnSmsCatchListener<String> onSmsCatchListener;
    private SmsReceiver smsReceiver;
    private String phoneNumber;
    private String filter;

    public SmsVerifyCatcher(Activity activity, OnSmsCatchListener<String> onSmsCatchListener) {
        this.activity = activity;
        this.onSmsCatchListener = onSmsCatchListener;
        smsReceiver = new SmsReceiver();
        smsReceiver.setCallback(this.onSmsCatchListener);
    }

    public SmsVerifyCatcher(Activity activity, Fragment fragment, OnSmsCatchListener<String> onSmsCatchListener) {
        this(activity, onSmsCatchListener);
        this.fragment = fragment;
    }

    public void onStart() {
        if (isStoragePermissionGranted(activity, fragment)) {
            registerReceiver();
        }
    }

    private void registerReceiver() {
        smsReceiver = new SmsReceiver();
        smsReceiver.setCallback(onSmsCatchListener);
        smsReceiver.setPhoneNumberFilter(phoneNumber);
        smsReceiver.setFilter(filter);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        activity.registerReceiver(smsReceiver, intentFilter);
    }

    public void setPhoneNumberFilter(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void onStop() {
        try {
            activity.unregisterReceiver(smsReceiver);
        } catch (IllegalArgumentException ignore) {
            //receiver not registered
        }
    }

    public void setFilter(String regexp) {
        this.filter = regexp;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                registerReceiver();
            }
        }
    }


    //For fragments
    public static boolean isStoragePermissionGranted(Activity activity, Fragment fragment) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (fragment == null) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                } else {
                    fragment.requestPermissions(
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS}, PERMISSION_REQUEST_CODE);
                }
                return false;
            }
        } else {
            return true;
        }
    }

}