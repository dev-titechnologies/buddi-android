package buddyapp.com.utils;

/**
 * Created by root on 19/7/17.
 */

public class Urls {

    public static String BASEURL= "http://192.168.1.20:9002";

    public static String LOGIN= "/login/login";

    public static String REGISTER= "/register/Register";

    public static String UPLOAD= "/upload/Upload";


    public static String SENDOTP= "/register/sendOTP";


    public static String VERIFYOTP= "";

    public static String getUPLOADURL(){

        return BASEURL+UPLOAD;
    }


    public static String getLoginURL(){

        return BASEURL+LOGIN;
    }

    public static String getRegisterURL(){

        return BASEURL+REGISTER;
    }

    public static String getSendOTPURL(){

        return BASEURL+SENDOTP;
    }

    public static String getVerifyOTPURL(){

        return BASEURL+VERIFYOTP;
    }

}
