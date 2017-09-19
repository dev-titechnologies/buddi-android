package buddyapp.com.utils.AlertDialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import buddyapp.com.R;
import buddyapp.com.activity.ProfileScreen;

/**
 * Created by titech on 13/9/17.
 */

public class SocialMediaURLAlertDialog extends Dialog   {
TextView submit, title;
    EditText input;
    String from;
    Activity context;
    public SocialMediaURLAlertDialog(Activity context, String from) {
        super(context);
        this.context = context;
        this.from = from;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        setCancelable(false);
        submit = (TextView) findViewById(R.id.submit);
        title = (TextView) findViewById(R.id.title_text);
        input = (EditText) findViewById(R.id.input_url);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input.getText().length()>0){
                    /*switch (from){
                        case "facebook" :
                            ProfileScreen.fbusername = input.getText().toString();
                            break;
                        case "instagram" :
                            ProfileScreen.instagramusername = input.getText().toString();
                            break;
                        case "snapchat" :
                            ProfileScreen.snapchatusername = input.getText().toString();
                            break;
                        case "linkedin" :
                            ProfileScreen.linkedinusername = input.getText().toString();
                            break;
                        case "twitter" :
                            ProfileScreen.twitterusername = input.getText().toString();
                            break;
                        case "youtube" :
                            ProfileScreen.youtubeusername = input.getText().toString();
                            break;
                        default:
                            break;
                     }*/
                     dismiss();
                }else{
                    input.setHint("Enter here");
                }
            }
        });
    }
}
