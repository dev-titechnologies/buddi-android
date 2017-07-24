package buddyapp.com.utils;

import android.content.Context;
import android.telephony.TelephonyManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import buddyapp.com.Controller;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by root on 18/2/16.
 */
public class NetworkCalls {
    public static String POSTWITHMULTIPART(String url, String Json) {

        try {


            final OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
            RequestBody requestBody = null;
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("REQUEST", Json)
                    .build();

            Request request = new Request.Builder()
//                .header("token","8fa138e211e08db26b7c8f98")
                    .header("device_imei", getdevice())
                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_type", "android")
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .url(url)
                    .post(requestBody)
                    .build();

            Response response = null;
            try {
                if (call != null && call.isExecuted()) {
                    call.cancel();
                }
                call = client.newCall(request);
                response = call.execute();

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                String returnREsponse = response.body().string();
//            CommonCall.PrintLog("response ",response.body().string());

//                CommonCall.PrintLog("response", returnREsponse);
                return returnREsponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }
        return createResponse().toString();

    }


    public static String UPLOADVideo(JSONObject  file, String url) {
        CommonCall.PrintLog("REQUEST", file.toString());
        CommonCall.PrintLog("URL", url);

        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = null;
        //case to handle multiple images

        // path == >files.get(0).toString()
        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)


                    .addFormDataPart("file_type", "vid")
                    .addFormDataPart("upload_type", "other")



                    .addFormDataPart("file_name", "comment_image" + file.getString("file").substring(file.getString("file").lastIndexOf(".")),
                            RequestBody.create(MEDIA_TYPE_PNG, (new File(file.getString("file")))))

                    .build();


            Request request = new Request.Builder()
//                .header("token","8fa138e211e08db26b7c8f98")
                    .header("device_imei", getdevice())
                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_type", "android")
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .url(url)
                    .post(requestBody)
                    .build();

            Response response = null;

            if (call != null && call.isExecuted()) {
                call.cancel();
            }
            call = client.newCall(request);
            response = call.execute();

            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            String returnREsponse = response.body().string();
//          CommonCall.PrintLog("response ",response.body().string());

            CommonCall.PrintLog("response", returnREsponse);
            return returnREsponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }
        return createResponse().toString();
    }
    public static String UPLOAD(String  file, String url) {
        CommonCall.PrintLog("REQUEST", file.toString());
        CommonCall.PrintLog("URL", url);

        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        RequestBody requestBody = null;
        //case to handle multiple images

        // path == >files.get(0).toString()
        try {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)

//                    .addFormDataPart("REQUEST", file.toString())

                    .addFormDataPart("file_name", "comment_image" + file.substring(file.lastIndexOf(".")),
                            RequestBody.create(MEDIA_TYPE_PNG, (new File(file))))

                    .build();


            Request request = new Request.Builder()
//                .header("token","8fa138e211e08db26b7c8f98")
                    .header("device_imei", getdevice())
                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_type", "android")
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .url(url)
                    .post(requestBody)
                    .build();

            Response response = null;

            if (call != null && call.isExecuted()) {
                call.cancel();
            }
            call = client.newCall(request);
            response = call.execute();

            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            String returnREsponse = response.body().string();
//          CommonCall.PrintLog("response ",response.body().string());

            CommonCall.PrintLog("response", returnREsponse);
            return returnREsponse;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }
        return createResponse().toString();
    }

    public static String GET(String url) {


        try {
//            OkHttpClient client = new OkHttpClient();

            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();

//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("user_id", PreferencesUtils.getData(Constant.userid,Controller.getAppContext(),"0"))
//                    .build();
            String token = PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0");
            Request request = new Request.Builder()
                    .url(url)
                    .header("device_imei", getdevice())
                    .header("token", token)

                    .header("device_type", "android")
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
//                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
//            CommonCall.PrintLog("RESPONSE ",response.body().string());
            return response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }


        return createResponse().toString();
    }

    public static String POSTQUICK(String url, String params) {
        CommonCall.PrintLog("REQ url", url);
        CommonCall.PrintLog("REQ PARAMS", params);


        try {

            Response response;
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)

                    .build();


            client.newBuilder().readTimeout(5, TimeUnit.SECONDS).
                    connectTimeout(5, TimeUnit.SECONDS).build();

            RequestBody body = RequestBody.create(JSON, params);


            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("device_type", "android")
                    .header("device_imei", getdevice())

                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .post(body)
                    .build();
            if (call != null && call.isExecuted()) {
                call.cancel();
            }
            call = client.newCall(request);
            response = call.execute();
            String returnREsponse = response.body().string();


            CommonCall.PrintLog("response", returnREsponse);
            return returnREsponse;
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse().toString();


        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }

    }

    public static String POST(String url, String params) {
        CommonCall.PrintLog("REQ url", url);
        CommonCall.PrintLog("REQ PARAMS", params);


        try {

            Response response;
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)

                    .build();


            client.newBuilder().readTimeout(30, TimeUnit.SECONDS).
                    connectTimeout(30, TimeUnit.SECONDS).build();

            RequestBody body = RequestBody.create(JSON, params);


            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("device_type", "android")
                    .header("device_imei", getdevice())

                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .post(body)
                    .build();
            if (call != null && call.isExecuted()) {
                call.cancel();
            }
            call = client.newCall(request);
            response = call.execute();
            String returnREsponse = response.body().string();


            CommonCall.PrintLog("response", returnREsponse);
            return returnREsponse;
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse().toString();


        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }

    }

    public static JSONObject createResponse() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("status", "2");
            temp.put(Constants.message, Constants.server_error_message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp;
    }



    public static Call call;

    public static String POSTwithParams(String url, RequestBody params) {
        CommonCall.PrintLog("REQ url", url);
        CommonCall.PrintLog("REQ PARAMS", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"));


        try {
            Response response;
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");

            OkHttpClient client;

            client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)

                    .build();


            client.newBuilder().readTimeout(15, TimeUnit.SECONDS).
                    connectTimeout(15, TimeUnit.SECONDS).build();


//            CommonCall.PrintLog("device_id", PreferencesUtils.getData(Constant.device_id, Controller.getAppContext(), "0"));
            Request request = new Request.Builder()
                    .url(url)
//                .header("token","8fa138e211e08db26b7c8f98")


                    .header("device_type", "android")
                    .header("device_imei", getdevice())

                    .header("token", PreferencesUtils.getData(Constants.token, Controller.getAppContext(), "0"))
                    .header("device_id", PreferencesUtils.getData(Constants.device_id, Controller.getAppContext(), "0"))
                    .post(params)
                    .build();

            if (call != null && call.isExecuted()) {
                call.cancel();
            }
            call = client.newCall(request);

            response = call.execute();
            String returnREsponse = response.body().string();
//            CommonCall.PrintLog("response ",response.body().string());

            CommonCall.PrintLog("response", returnREsponse);
            return returnREsponse;
        } catch (IOException e) {
            e.printStackTrace();
            return createResponse().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse().toString();
        }

    }


    private static String getdevice() {
        TelephonyManager telephonyManager = (TelephonyManager) Controller.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
//        CommonCall.PrintLog("imei", telephonyManager.getDeviceId());


        return telephonyManager.getDeviceId();

    }
}