package buddiapp.com.activity.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import buddiapp.com.R;
import buddiapp.com.activity.WebActivity;
import buddiapp.com.utils.Urls;

/**
 * A simple {@link Fragment} subclass.
 */
public class Legal extends Fragment {
TextView termsOfUse, privacyPolicy, disclaimer;

    public Legal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_legal, container, false);

        termsOfUse = view.findViewById(R.id.termsof_use);
        privacyPolicy = view.findViewById(R.id.privacy_policy);
        disclaimer = view.findViewById(R.id.disclaimer);

        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terms = new Intent(getActivity(), WebActivity.class);
                terms.putExtra("url", Urls.TERMS_OF_USE_URL);
                startActivity(terms);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacy = new Intent(getActivity(), WebActivity.class);
                privacy.putExtra("url", Urls.PRIVACY_POLICY_URL);
                startActivity(privacy);
            }
        });
        disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent disclaimer = new Intent(getActivity(), WebActivity.class);
                disclaimer.putExtra("url", Urls.DISCLAIMER_URL);
                startActivity(disclaimer);
            }
        });

        return view;
    }

}
