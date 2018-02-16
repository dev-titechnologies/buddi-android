package buddiapp.com.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.ChooseSpecification;

/**
 * Created by titech on 14/2/18.
 */

public class DurationAdapter extends BaseAdapter {
    JSONArray dataArray = new JSONArray();
    Activity activity;
    onDurationSelectedListener listnerObject;

    ArrayList<String> selected_id = new ArrayList<>();
    public DurationAdapter(JSONArray jsonArray, Activity activity,onDurationSelectedListener durationSelectedListener){
        dataArray = jsonArray;
        this.activity = activity;
        listnerObject = durationSelectedListener;
    }
    @Override
    public int getCount() {
        return dataArray.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CustomViewHolder holder = null;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.duration_list_item, viewGroup, false);
        view.setTag(holder);
        holder = new CustomViewHolder();
        holder.showTextName = view.findViewById(R.id.text_view_name);
        holder.showTextCost = view.findViewById(R.id.text_view_cost);
        holder.item = view.findViewById(R.id.layout);
        try {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            holder.item.setTag(i);

            holder.showTextName.setText(jsonObject.getString("session_name"));
            holder.showTextCost.setText(jsonObject.getString("session_cost"));

            if(selected_id.size()>0 && selected_id.contains(i+"")){
                holder.showTextName.setTextColor(activity.getResources().getColor(R.color.white));
                holder.item.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }else{
                holder.showTextName.setTextColor(activity.getResources().getColor(R.color.black));
                holder.item.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }

           /*if(PreferencesUtils.getData("SettingS",activity,"false").equals("true")){
                if(PreferencesUtils.getData(Constants.settings_duration,activity,"0").equals(
                        jsonObject.getString("session_time")
                )){
                    holder.showText.setTextColor(activity.getResources().getColor(R.color.white));
                    holder.showText.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                    selected_id.clear();
                    selected_id.add(view.getTag().toString());
                }
            }*/

            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_id.clear();
                    selected_id.add(view.getTag().toString());
                    try {
                        ChooseSpecification.sessionDuration = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getInt("session_time");
                        String name = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getString("session_name");
                        int time = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getInt("session_time");
                        listnerObject.onDurationChanged(name, time);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    notifyDataSetInvalidated();
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
            return view;
    }



    public interface onDurationSelectedListener{
        void onDurationChanged(String durationName, int duration);
    }

    public void onSetDurationChanged(onDurationSelectedListener listnerbject){
       listnerObject = listnerbject;
    }

    public class CustomViewHolder {
        TextView showTextName, showTextCost;
        RelativeLayout item;
    }
}
