package buddyapp.com.utils.AlertDialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import buddyapp.com.R;
import buddyapp.com.activity.Session;

/**
 * Created by titech on 8/8/17.
 */

public class Alertdialoge extends Dialog {
    public Activity c;
    public Dialog d;
    public TextView allow, cancel;

    public Alertdialoge(Activity context) {

        super(context);
        this.c= context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.alertdialoge);
        setCancelable(false);
        allow = (TextView) findViewById(R.id.okay);

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Intent home = new Intent(c, Session.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(home);
                c.finish();
            }
        });

    }
    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);

    }
}
