package buddiapp.com.utils;


/**
 * Created by root on 19/7/17.
 */

public class Urls {

  public static String BASEURL = "http://git.titechnologies.in:4001";

//    public static String BASEURL = "http://buddiapi.buddiadmin.com";


//  public static String BASEURL = "http://192.168.1.66:9002";

//  public static String BASEURL= "http://192.168.1.20:9002";


    public static String terms = "http://design.titechnologies.in/1015_buddy/html/termsofuse.html";


    public static String listCardinStripe = "/payment/listCardinStripe";

    public static String LOGIN = "/login/login";

    public static String REGISTER = "/register/Register";

    public static String UPLOAD = "/upload/Upload";


    public static String SENDOTP = "/register/sendOTP";


    public static String VERIFYOTP = "/register/verifyOTP";


    public static String CATEGORYLIST = "/category/listCategory";

    public static String TRAINEEPROFILE = "/profile/viewProfile";

    public static String EDITTRAINEEPROFILE = "/profile/editProfile";

    public static String LOGOUT = "/login/logout";

    public static String BOOKINGHISTORY = "/booking/viewBookingHistory";

    public static String ADDTRAINERCAT = "/profile/addTrainerCategory";

    public static String GYMLIST = "/gym/listGyms ";

    public static String TRAINERSEARCH = "/search/searchTrainer";


    public static String PAYMENTTOKEN = "/payment/createClientToken";


    public static String applyPromoCode = "/booking/applyPromoCode";

    public static String checkout = "/payment/checkoutPay";


    public static String bookingAction = "/booking/bookingAction";
    public static String categoryStatus = "/login/categoryStatus";

    public static String acceptSelection = "/search/acceptSelection";

    public static String declineSelection = "/search/declineSelection";

    public static String extendBooking = "/booking/extendBooking";

    public static String addCardtoStripe = "/payment/addCardtoStripe";
    public static String pendingTransaction = "/booking/pendingTransaction";

    public static String pendingBooking = "/booking/pendingBooking";


    public static String updateStripeAcc = "/payment/updateStripeAcc";

    public static String getupdateStripeAccURL() {

        return BASEURL + updateStripeAcc;
    }


    public static String getpendingBookingURL() {

        return BASEURL + pendingBooking;
    }

    public static String getpendingTransactionURL() {

        return BASEURL + pendingTransaction;
    }


    public static String getaddCardtoStripeURL() {

        return BASEURL + addCardtoStripe;
    }


    public static String getlistcardURL() {

        return BASEURL + listCardinStripe;
    }


    public static String getextendBookingURL() {

        return BASEURL + extendBooking;
    }

    public static String getDeclineSelectionURL() {

        return BASEURL + declineSelection;
    }


    public static String getAcceptSelectionURL() {

        return BASEURL + acceptSelection;
    }


    public static String getcategoryStatusURL() {

        return BASEURL + categoryStatus;
    }


    public static String getbookingActionURL() {

        return BASEURL + bookingAction;
    }

    public static String getcheckoutURL() {

        return BASEURL + checkout;
    }

    public static String getapplyPromoCodeURL() {

        return BASEURL + applyPromoCode;
    }


    public static String getPAYMENTTOKENURL() {

        return BASEURL + PAYMENTTOKEN;
    }

    public static String RANDOMSELECTOR = "/search/randomSelector";

    public static String getGYMLISTURL() {

        return BASEURL + GYMLIST;
    }


    public static String getADDTRAINECATEGORYURL() {

        return BASEURL + ADDTRAINERCAT;
    }


    public static String getCATEGORYURL() {

        return BASEURL + CATEGORYLIST;
    }


    public static String FORGOTPASSWORD = "/login/forgotPassword";


    public static String getUPLOADURL() {

        return BASEURL + UPLOAD;
    }


    public static String getLoginURL() {

        return BASEURL + LOGIN;
    }

    public static String getRegisterURL() {

        return BASEURL + REGISTER;
    }

    public static String getSendOTPURL() {

        return BASEURL + SENDOTP;
    }

    public static String getVerifyOTPURL() {

        return BASEURL + VERIFYOTP;
    }

    public static String getResetURL() {
        return BASEURL + FORGOTPASSWORD;
    }

    public static String getTraineeProfileURL() {
        return BASEURL + TRAINEEPROFILE;
    }

    public static String getEditTraineeProfileURL() {
        return BASEURL + EDITTRAINEEPROFILE;
    }

    public static String getLogoutURL() {
        return BASEURL + LOGOUT;
    }

    public static String getBookingHistoryURL() {
        return BASEURL + BOOKINGHISTORY;
    }

    public static String getTrainerSearchURL() {
        return BASEURL + TRAINERSEARCH;
    }

    public static String getRandomSelectURL() {
        return BASEURL + RANDOMSELECTOR;
    }

    public static String TRAINERSTATUS = "/location/updateLocationStatus/";

    public static String getStatusURL() {
        return BASEURL + TRAINERSTATUS;
    }

    public static String STARTSESSION = "/booking/startSession";

    public static String getStartSessionURL() {
        return BASEURL + STARTSESSION;
    }

    public static String EXTENDBOOKING = "/booking/extendBooking";

    public static String getExtendSessionURL() {
        return BASEURL + EXTENDBOOKING;
    }

    public static String getAllMessage = "/chat/receiveMessage";

    public static String getAllMessageURL() {
        return BASEURL + getAllMessage;
    }

    public static String getInvite = "/invite/inviteContacts";

    public static String getInviteURL() {
        return BASEURL + getInvite;
    }

    public static String addReview = "/review/addReview";

    public static String getAddReviewURL() {
        return BASEURL + addReview;
    }

    public static String chatHistory = "/chat/chatHistory";

    public static String getChatHistoryURL() {
        return BASEURL + chatHistory;
    }

}
