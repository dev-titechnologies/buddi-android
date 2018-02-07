package buddiapp.com.activity.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import buddiapp.com.R;
import buddiapp.com.activity.SessionReady;

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

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(refresh,
                new IntentFilter("BUDDI_TRAINER_SESSION_FINISH"));
    }

    private BroadcastReceiver refresh = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //write your activity starting code here
            Intent intentB = new Intent(getActivity(), SessionReady.class);
            getActivity().startActivity(intentB);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(refresh);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(refresh);
    }


}
