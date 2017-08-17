package buddyapp.com.Settings;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by titech on 18/7/17.
 */

public class Constants {


    public static String clientToken="clientToken";
    public static String fname="first_name";
    public static String lname= "last_name";
    public static String email = "email";
    public static String gender = "gender";
    public static String token = "token";
    public static String device_id = "device_id";
    public static String server_error_message = "Server not responding";
    public static String message = "message";
    public static String status = "status";
    public static String user_type = "user_type";
    public static String user_id = "user_id";
    public static String trainer = "trainer";
    public static String user_image = "user_image";
    public static String mobile = "mobile";
    public static String trainer_type = "trainer_type";
    public static String trainer_status = "trainer_status";
    public static String trainer_id = "trainer_id";

    public static String trainee_id = "trainee_id";

    public static String timerstarted = "timerstarted";


    public static String approved = "category_approved";
    public static String trainer_Data = "trainer_Data";
    public static String trainee_Data = "trainee_Data";

    public static String start_session = "start_session";

    public static String pending = "category_pending";

    public static boolean stop=false;//for timmer

    public static String trainee = "trainee";

    public static String zipcode = "zipcode";

    public static String gym_subscriptions = "gym_subscriptions";

    public static String military_installations = "military_installations";


    public static String training_exp = "training_exp";
    public static String competed_category = "competed_category";
    public static String coached_anybody = "coached_anybody";
    public static String certified_trainer = "certified_trainer";


    public static JSONObject questionData = new JSONObject();


    public static boolean source_become_trainer = false;

    public static JSONArray searchArray = new JSONArray();
    public static String latitude = "latitude";
    public static  String longitude = "longitude";
    public static String duration = "duration";
    public static String availStatus = "avail_status";

    public static String transactionId = "transactionId";
    public static String amount ="amount";
    public static String transaction_status = "transaction_status";

}
