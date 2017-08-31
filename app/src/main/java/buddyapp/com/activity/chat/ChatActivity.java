package buddyapp.com.activity.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import buddyapp.com.Controller;
import buddyapp.com.R;
import buddyapp.com.Settings.Constants;
import buddyapp.com.Settings.PreferencesUtils;
import buddyapp.com.activity.chat.model.Consersation;
import buddyapp.com.activity.chat.model.Message;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;
import buddyapp.com.utils.NetworkCalls;
import buddyapp.com.utils.Urls;

import static buddyapp.com.Controller.chatConnect;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
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
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intentData = getIntent();
//        if(PreferencesUtils.getData(Constants.user_type,getApplicationContext(),"").equals("trainee")){
//            StaticConfig.UID=PreferencesUtils.getData(Constants.trainee_id,getApplicationContext(),"");
//        }else{
//            StaticConfig.UID=PreferencesUtils.getData(Constants.trainer_id,getApplicationContext(),"");
//        }

        roomId = PreferencesUtils.getData(Constants.bookid, getApplicationContext(), "");


        CommonCall.PrintLog("socket : ", Controller.mSocket.connected() + "");
        Log.e("idFrnd + roomid ", idFriend + "  " + roomId);
//        String nameFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);

        consersation = new Consersation();
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

//        String base64AvataUser = SharedPreferenceHelper.getInstance(this).getUserInfo().avata;
//        if (!base64AvataUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
//            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
//            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        } else {
        bitmapAvataUser = null;
//        }

        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
//        if (idFriend != null && nameFriend != null) {
//            getSupportActionBar().setTitle(nameFriend);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
        recyclerChat.setLayoutManager(linearLayoutManager);
//            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser);
    /*      FirebaseDatabase.getInstance().getReference().child("bookingID_" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/
//            recyclerChat.setAdapter(adapter);
//        }
        String content = "";
        String id = "";

        chatConnect();
        new getMessages().execute();
//        loadMessage(content, id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        fromId = intent.getStringExtra("CHAT_FROMID");
                        receiveMsg = intent.getStringExtra("CHAT_MESSAGE");
                        chatName = intent.getStringExtra("CHAT_NAME");
                        chatImage = intent.getStringExtra("CHAT_IMAGE");
                    }
                }, new IntentFilter("SOCKET_BUDDI_CHAT")


        );
    }

    private void loadMessage(String content) {
        adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser);
        Message newMessage = new Message();
        newMessage.idSender = PreferencesUtils.getData(Constants.user_id, getApplicationContext(), "");

        if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), "");
        else
            newMessage.idReceiver = PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), "");

        newMessage.text = content;
//        newMessage.timestamp = (long) mapMessage.get("timestamp");
        consersation.getListMessageData().add(newMessage);
        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
        recyclerChat.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        Intent result = new Intent();
//        result.putExtra("idFriend", idFriend.get(0));
//        setResult(RESULT_OK, result);
        this.finish();
    }

    @Override
    public void onClick(View view) {
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
    }

    public void emitUserMessage(final String content) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {


                JSONObject jsonObject = new JSONObject();
                try {

                    jsonObject.put("url", "http://git.titechnologies.in:4001" + ("/chat/sendMessage"));

                    JSONObject object = new JSONObject();

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

    class getMessages extends AsyncTask<String, String, String> {
        JSONObject reqData = new JSONObject();

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                reqData.put("from_id", PreferencesUtils.getData(Constants.user_id, getApplicationContext(), ""));


                if (PreferencesUtils.getData(Constants.user_type, getApplicationContext(), "").equals("trainee"))
                    reqData.put("to_id", PreferencesUtils.getData(Constants.trainer_id, getApplicationContext(), ""));
                else
                    reqData.put("to_id", PreferencesUtils.getData(Constants.trainee_id, getApplicationContext(), ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return NetworkCalls.POST(Urls.getAllMessageURL(), reqData.toString());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String ss = s;
//                final JSONObject response = new JSONObject(s);
            CommonCall.PrintLog("AllMessages", ss);

        }
    }
}

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;
    private HashMap<String, Bitmap> bitmapAvata;
    private HashMap<String, DatabaseReference> bitmapAvataDB;
    private Bitmap bitmapAvataUser;

    public ListMessageAdapter(Context context, Consersation consersation, HashMap<String, Bitmap> bitmapAvata, Bitmap bitmapAvataUser) {
        this.context = context;
        this.consersation = consersation;
        this.bitmapAvata = bitmapAvata;
        this.bitmapAvataUser = bitmapAvataUser;
        bitmapAvataDB = new HashMap<>();
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
//            Bitmap currentAvata = bitmapAvata.get(consersation.getListMessageData().get(position).idSender);
//            if (currentAvata != null) {
//                ((ItemMessageFriendHolder) holder).avata.setImageBitmap(currentAvata);
//            } else {
            final String id = consersation.getListMessageData().get(position).idSender;
            if (bitmapAvataDB.get(id) == null) {
                bitmapAvataDB.put(id, FirebaseDatabase.getInstance().getReference().child("user/" + id + "/avata"));
                bitmapAvataDB.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String avataStr = (String) dataSnapshot.getValue();
                            if (!avataStr.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                byte[] decodedString = Base64.decode(avataStr, Base64.DEFAULT);
                                ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                            } else {
                                ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avata));
                            }
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else if (holder instanceof ItemMessageUserHolder)

        {
            ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
        }
        //            if (bitmapAvataUser != null) {
//                ((ItemMessageUserHolder) holder).avata.setImageBitmap(bitmapAvataUser);
    }
//        }
//    }

    @Override
    public int getItemViewType(int position) {
        return consersation.getListMessageData().get(position).idSender.equals(StaticConfig.UID) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return consersation.getListMessageData().size();
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView2);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        avata = (CircleImageView) itemView.findViewById(R.id.imageView3);
    }

}

