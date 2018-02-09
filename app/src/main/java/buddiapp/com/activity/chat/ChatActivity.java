package buddiapp.com.activity.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import buddiapp.com.Controller;
import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.Fragment.BookingHistory;
import buddiapp.com.activity.SessionReady;
import buddiapp.com.activity.chat.model.Consersation;
import buddiapp.com.activity.chat.model.Message;
import buddiapp.com.fcm.NotificationUtils;
import buddiapp.com.utils.CircleImageView;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.ConnectivityReceiver;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

import static buddiapp.com.Controller.chatConnect;
import static buddiapp.com.Controller.mSocket;
import static buddiapp.com.Controller.updateSocket;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private RecyclerView recyclerChat;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    private ListMessageAdapter adapter;
    private String roomId, fromId, toId, receiveMsg, chatName, chatImage;
    private String idFriend;
    private Consersation consersation;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    public String bitmapAvataUser;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intentData = getIntent();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("Buddi Chat");

        roomId = PreferencesUtils.getData(Constants.bookid, getApplicationContext(), "");
        CommonCall.PrintLog("socket : ", Controller.mSocket.connected() + "");
        PreferencesUtils.saveData(Constants.current_page, "chat",getApplicationContext());
        consersation = new Consersation();
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);


        bitmapAvataUser = null;
//        }

        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
//        if (idFriend != null && nameFriend != null) {
//            getSupportActionBar().setTitle(nameFriend);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(linearLayoutManager);

        adapter = new ListMessageAdapter(this, consersation);
        recyclerChat.setAdapter(adapter);
        root = findViewById(R.id.root);
        String content = "";
        String id = "";
        NotificationUtils.clearNotifications(getApplicationContext());

        new getMessages().execute();

        if(mSocket.connected()){

                }else{
                    updateSocket();
                    mSocket.connect();
                    chatConnect();
        }

    }


    BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Controller.firstConnect){
                Controller.firstConnect =false;
                fromId = intent.getStringExtra("CHAT_FROMID");
                receiveMsg = intent.getStringExtra("CHAT_MESSAGE");
                chatName = intent.getStringExtra("CHAT_NAME");
                chatImage = intent.getStringExtra("CHAT_IMAGE");

                if (!fromId.equals(PreferencesUtils.getData(Constants.user_id, getApplicationContext(), "")))
                    loadMessageFromSocket(receiveMsg, fromId, chatImage);

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if(!Controller.mSocket.connected()){
            Snackbar snackbar = Snackbar
                    .make(root, "Connection lost", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(
                                    chatReceiver , new IntentFilter("SOCKET_BUDDI_CHAT")
                            );
//
                            new getMessages().execute();

                        }
                    });

            snackbar.show();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
               chatReceiver , new IntentFilter("SOCKET_BUDDI_CHAT")
        );

        Controller.getInstance().setConnectivityListener(ChatActivity.this);
    }

    private void loadMessage(String content) {
        adapter = new ListMessageAdapter(this, consersation);
        Message newMessage = new Message();
        newMessage.idSender = PreferencesUtils.getData(Constants.user_id, getApplicationContext(), "");

        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), "");
        else
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), "");

        newMessage.text = content;
        newMessage.image = PreferencesUtils.getData(Constants.user_image,getApplicationContext(),"");
//        newMessage.timestamp = (long) mapMessage.get("timestamp");
        consersation.getListMessageData().add(newMessage);
        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
        recyclerChat.setAdapter(adapter);
    }
    public void loadMessageFromSocket(String receiveMsg, String fromId, String img){

        if(adapter == null){
            adapter = new ListMessageAdapter(getApplicationContext(),consersation);
            recyclerChat.setAdapter(adapter);
        }
        Message newMessage = new Message();
        newMessage.idSender = fromId;

        if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals("trainee"))
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainer_id,getApplicationContext(),"");
        else
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainee_id,getApplicationContext(),"");

        newMessage.text = receiveMsg;
        newMessage.image = img;
//        newMessage.timestamp = (long) mapMessage.get("timestamp");
        consersation.getListMessageData().add(newMessage);
//        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
        recyclerChat.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
//        recyclerChat.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                chatReceiver );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    chatReceiver );
            PreferencesUtils.saveData(Constants.current_page,"back",getApplicationContext());

            if(PreferencesUtils.getData(Constants.from,getApplicationContext(),"false").equals("splash")){
                startActivity(new Intent(getApplicationContext(), SessionReady.class));
                finish();
            }else{
                this.finish();
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                chatReceiver );
        PreferencesUtils.saveData(Constants.current_page,"back",getApplicationContext());

        if(PreferencesUtils.getData(Constants.from,getApplicationContext(),"false").equals("splash")){
            startActivity(new Intent(getApplicationContext(), SessionReady.class));
            finish();
        }else{
        this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(CommonCall.isNetworkAvailable()){
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.text = content;
                newMessage.idSender = PreferencesUtils.getData(Constants.user_id, getApplicationContext(), "");

                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
                    newMessage.idReceiver = PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), "");
                else
                    newMessage.idReceiver = PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), "");


                newMessage.timestamp = System.currentTimeMillis();
//                FirebaseDatabase.getInstance().getReference().child("bookingID_" + roomId).push().setValue(newMessage);
                emitUserMessage(content);
                loadMessage(content);
            }
        }
        }else{
            Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
        }

    }

    public void emitUserMessage(final String content) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", Urls.BASEURL+ ("/chat/sendMessage"));

                    JSONObject object = new JSONObject();

                    object.put("user_type", PreferencesUtils.getData(Constants.user_type, getApplicationContext(), ""));

                    object.put("book_id", PreferencesUtils.getData(Constants.bookid, getApplicationContext(), ""));
                    object.put("from_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));

                    if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
                        object.put("to_id", PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), ""));
                    else
                        object.put("to_id", PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), ""));

                    object.put("message", content);

                    jsonObject.put("data", object);
                    Controller.mSocket.emit("post", jsonObject);
                    CommonCall.PrintLog("emitMsg", jsonObject + "");
                    CommonCall.PrintLog("token", PreferencesUtils.getData(Constants.token, getApplicationContext(), ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if(mSocket.connected()){

            }else{
            updateSocket();
            mSocket.connect();
            chatConnect();
            }
        } else {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    chatReceiver );
            showSnack();
        }
  }

    // Showing the status in Snackbar
    private void showSnack() {
        Snackbar snackbar = Snackbar
                .make(root, "Connection lost", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(CommonCall.isNetworkAvailable()){

                            LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(
                                    chatReceiver , new IntentFilter("SOCKET_BUDDI_CHAT")
                            );
//

                        new getMessages().execute();
//                        }
//                        else{
//                            Toast.makeText(ChatActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });

        snackbar.show();

    }

    class getMessages extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
            CommonCall.showLoader(ChatActivity.this);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("book_id", PreferencesUtils.getData(Constants.bookid, getApplicationContext(), ""));


                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
                    reqData.put("to_id", PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), ""));
                else
                    reqData.put("to_id", PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return NetworkCalls.POST(Urls.getChatHistoryURL(), reqData.toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            String ss = s;
            CommonCall.PrintLog("AllMessages", ss);
            JSONObject obj = null;
            try {
                obj = new JSONObject(s);
            if (obj.getInt("status") == 1) {
                JSONArray jsonArray = obj.getJSONArray("data");
                for(int i =0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    loadMessageFromSocket(jsonObject.getString("message"),jsonObject.getString("from_id"),jsonObject.getString("from_img"));
                }
                if(mSocket.connected()){

                }else{
                    updateSocket();
                    mSocket.connect();
                    chatConnect();
                }
             }else if(obj.getInt("status") == 2){
                CommonCall.hideLoader();
                showSnack();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;


    public ListMessageAdapter(Context context, Consersation consersation) {
        this.context = context;
        this.consersation = consersation;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemMessageFriendHolder) {
            ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            CommonCall.LoadImage(context,consersation.getListMessageData().get(position).image,((ItemMessageFriendHolder) holder).avata,R.drawable.default_avata,R.drawable.default_avata);

//            Bitmap currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);
//            if (currentAvata != null) {
//                ((ItemMessageFriendHolder) holder).avata.setImageBitmap(currentAvata);
//            } else {

        } else if (holder instanceof ItemMessageUserHolder)
        {

            ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            CommonCall.LoadImage(context,consersation.getListMessageData().get(position).image,((ItemMessageUserHolder) holder).avata,R.drawable.default_avata,R.drawable.default_avata);

        }
        //            if (bitmapAvataUser != null) {
//                ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
   }
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        return consersation.getListMessageData().get(position).idSender.equals(PreferencesUtils.getData(Constants.user_id,context,"")) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
    }

    @Override
    public int getItemCount() {
        Log.e("converstation_count",consersation.getListMessageData().size()+"");
        return consersation.getListMessageData().size();
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView);
    }

}

