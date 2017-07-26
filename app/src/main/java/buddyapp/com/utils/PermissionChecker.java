//package buddyapp.com.utils;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by titech on 26/7/17.
// */
//
//public class PermissionChecker {
//    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
//
//    String[] permissions= new String[]{
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION};
//
//
//        if (checkPermissions())
//            //  permissions  granted.
//        }
//
//    private  boolean checkPermissions() {
//        int result;
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        for (String p:permissions) {
//            result = ContextCompat.checkSelfPermission(getActivity(),p);
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(p);
//            }
//        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
//            return false;
//        }
//        return true;
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MULTIPLE_PERMISSIONS:{
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    // permissions granted.
//                } else {
//                    String permissions = "";
//                    for (String per : permissionsList) {
//                        permissions += "\n" + per;
//                    }
//                    // permissions list of don't granted permission
//                }
//                return;
//            }
//        }
//    }
//}
