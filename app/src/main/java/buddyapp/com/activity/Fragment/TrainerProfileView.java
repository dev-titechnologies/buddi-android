package buddyapp.com.activity.Fragment;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import buddyapp.com.R;
import buddyapp.com.adapter.CustomExpandableListAdapter;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TrainerProfileView extends Fragment {
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    public TrainerProfileView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_profile_view, container, false);

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter( getView(),getContext(), expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    public static class ExpandableListDataPump {
        public static HashMap<String, List<String>> getData() {
            HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

            List<String> cricket = new ArrayList<String>();
            cricket.add("serial 1");
            cricket.add("serial 2");
            cricket.add("serial 3");
            cricket.add("serial 4");
            cricket.add("serial 5");

            List<String> category = new ArrayList<String>();
            category.add("Crossfit");
            category.add("Powerlifting");
            category.add("Physique training");

            List<String> football = new ArrayList<String>();
            football.add("Maxxfit Fitness");

            List<String> certification = new ArrayList<String>();
            certification.add("CSCS");
            certification.add("ACE");
            certification.add("NASM");

            expandableListDetail.put("Certification", certification);
            expandableListDetail.put("Coaching History", football);
            expandableListDetail.put("Training History", cricket);
            expandableListDetail.put("Training Category", category);
            expandableListDetail.put("Gym Subscriptions", football);
            return expandableListDetail;
        }
    }

}
