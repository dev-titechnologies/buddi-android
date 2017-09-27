package buddyapp.com.activity.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import buddyapp.com.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Help extends Fragment {
TextView sendMail;

    public Help() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        sendMail = (TextView) view.findViewById(R.id.send_mail);

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "h@buddiuniverse.com" });
                startActivity(Intent.createChooser(intent, ""));
            }
        });


        return view;
    }

}
