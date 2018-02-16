package buddiapp.com.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddiapp.com.R;
import buddiapp.com.utils.CommonCall;

import static buddiapp.com.utils.CommonCall.extendSessionTask;

/**
 * Created by titech on 15/2/18.
 */

public class ExtendedListAdapters extends BaseAdapter {
    JSONArray dataArray = new JSONArray();
    Activity activity;

    ArrayList<String> selected_id = new ArrayList<>();
    public ExtendedListAdapters(JSONArray jsonArray, Activity activity){
        dataArray = jsonArray;
        this.activity = activity;
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
        holder.showText = view.findViewById(R.id.text_view);
        try {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            holder.showText.setTag(i);

            holder.showText.setText(jsonObject.getString("session_name")+"\t \t \t \t"+
                    "$"+jsonObject.getString("session_cost"));

            if(selected_id.size()>0 && selected_id.contains(i+"")){
                holder.showText.setTextColor(activity.getResources().getColor(R.color.white));
                holder.showText.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }else{
                holder.showText.setTextColor(activity.getResources().getColor(R.color.black));
                holder.showText.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }

            holder.showText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_id.clear();
                    selected_id.add(view.getTag().toString());
                    try {

                        String name = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getString("session_name");
                        int time = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getInt("session_time");
                        int amount = dataArray.getJSONObject(Integer.parseInt(view.getTag().toString()))
                                .getInt("session_cost");
                        CommonCall.durationTime = String.valueOf(time);
                        extendSessionTask(activity, time,amount);
                        CommonCall.dialog.dismiss();
//                        listnerObject.onDurationChanged(name, time, amount, activity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {
        TextView showText;
    }
}
