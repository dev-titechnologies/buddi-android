package buddiapp.com.activity.chat;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import buddiapp.com.R;

public class ChatScreen extends AppCompatActivity {
    EditText message;
    String smsg;
    ImageView send;
    TextView client, user;
    LinearLayout chatlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        message = (EditText) findViewById(R.id.message);
        send = (ImageView) findViewById(R.id.send);
        chatlayout = (LinearLayout) findViewById(R.id.chatScreen);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(message.getText().length()>0){
                smsg = message.getText().toString();
                message.setText("");
                appendReceiverText(smsg);
                    hideSoftKeyboard(ChatScreen.this);
                }
            }
        });
    }

    private void appendSenderText(String message) {

        TextView msg = new TextView(ChatScreen.this);
        msg.setBackgroundResource(R.drawable.rounded_corner);
        msg.setText(message);
        msg.setPadding(10, 10, 10, 10);
        msg.setTextColor(getResources().getColor(R.color.black));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 15, 0, 0);
        params.gravity = Gravity.LEFT;
        msg.setLayoutParams(params);
        msg.setGravity(Gravity.CENTER);
        chatlayout.addView(msg);
    }
    private void appendReceiverText(String message) {
        TextView you = new TextView(ChatScreen.this);
        you.setBackgroundResource(R.drawable.rounded_corner1);
        you.setText("You");
        you.setTextSize(9);
        you.setTextColor(Color.BLUE);
        you.setPadding(5, 5, 5, 5);
        you.setTextColor(getResources().getColor(R.color.black));

        TextView msg = new TextView(ChatScreen.this);
        msg.setBackgroundResource(R.drawable.rounded_corner1);
        msg.setText(message);
        msg.setTextSize(18);
        msg.setPadding(10, 10, 10, 10);
        msg.setTextColor(getResources().getColor(R.color.black));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 5, 0);
        params.gravity = Gravity.RIGHT;

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins(0, 0, 5, 15);
        param.gravity = Gravity.RIGHT;

        you.setLayoutParams(param);
        msg.setLayoutParams(params);
        msg.setGravity(Gravity.CENTER);
        chatlayout.addView(msg);
        chatlayout.addView(you);
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
