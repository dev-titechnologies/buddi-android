package buddyapp.com.utils;

import java.security.PublicKey;

/**
 * Created by root on 19/7/17.
 */

public class Urls {

  public static String BASEURL = "http://git.titechnologies.in:4001";
//    public static String BASEURL= "http://192.168.1.66:9002";

//    public static String BASEURL= "http://192.168.1.14:4001";

    public static String LOGIN= "/login/login";

    public static String REGISTER= "/register/Register";

    public static String UPLOAD= "/upload/Upload";


    public static String SENDOTP= "/register/sendOTP";


    public static String VERIFYOTP= "/register/verifyOTP";


    public static String CATEGORYLIST= "/category/listCategory";

    public static  String TRAINEEPROFILE= "/profile/viewProfile";

    public static String EDITTRAINEEPROFILE= "/profile/editProfile";

    public static String LOGOUT= "/login/logout";

    public static String BOOKINGHISTORY= "/booking/viewBookingHistory";

    public static String ADDTRAINERCAT= "/profile/addTrainerCategory";

    public static String GYMLIST= "/gym/listGyms ";

    public static String TRAINERSEARCH= "/search/searchTrainer";


    public static String PAYMENTTOKEN= "/payment/createClientToken";


    public static String applyPromoCode= "/booking/applyPromoCode";

    public static String checkout= "/payment/checkout";


    public static String getcheckoutURL(){

        return BASEURL+checkout;
    }
    public static String getapplyPromoCodeURL(){

        return BASEURL+applyPromoCode;
    }


    public static String getPAYMENTTOKENURL(){

        return BASEURL+PAYMENTTOKEN;
    }

    public static String getGYMLISTURL(){

        return BASEURL+GYMLIST;
    }



    public static String getADDTRAINECATEGORYURL(){

        return BASEURL+ADDTRAINERCAT;
    }




    public static String getCATEGORYURL(){

        return BASEURL+CATEGORYLIST;
    }


    public static String FORGOTPASSWORD= "/login/forgotPassword";


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

    public static String getResetURL(){
        return BASEURL+FORGOTPASSWORD;
    }

    public static String getTraineeProfileURL(){
        return BASEURL+TRAINEEPROFILE;
    }

    public static String getEditTraineeProfileURL()
    {
        return BASEURL+EDITTRAINEEPROFILE;
    }

    public static String getLogoutURL(){
        return BASEURL+LOGOUT;
    }

    public static String getBookingHistoryURL(){
        return BASEURL+BOOKINGHISTORY;
    }

    public static String getTrainerSearchURL(){
        return BASEURL+TRAINERSEARCH;
    }

}
