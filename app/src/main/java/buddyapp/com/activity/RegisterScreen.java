package buddyapp.com.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import buddyapp.com.R;

public class RegisterScreen extends AppCompatActivity {
TextView Google, facebook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
    }
}
