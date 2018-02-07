package buddiapp.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.activity.WalletHistoryDetailActivity;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.Iso8601;

/**
 * Created by titech on 29/1/18.
 */

public class WalletTransactionTraineeAdapter extends BaseAdapter {
    JSONArray dataArray = new JSONArray();
    Activity activity;
    public WalletTransactionTraineeAdapter(JSONArray dataArray, Activity appContext){
        this.dataArray = dataArray;
        activity = appContext;
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
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallet_transaction_trainee_list, viewGroup, false);
        view.setTag(holder);
        holder = new CustomViewHolder();
        holder.amount = view.findViewById(R.id.amount);
        holder.date = view.findViewById(R.id.date);
        holder.trans_id = view.findViewById(R.id.transaction_id);
        holder.selectCard = view.findViewById(R.id.select_card);
        holder.imageView = view.findViewById(R.id.image_view);

        try {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            holder.selectCard.setTag(i);
            holder.date.setText(convertTime3(jsonObject.getString("date")));

    if(PreferencesUtils.getData(Constants.user_type, activity,"").equals(Constants.trainee)){

        if(jsonObject.getString("transaction_type").equals("Income")){
                if(jsonObject.getString("type").equals("refund")){
                    holder.trans_id.setText("Refund");
                    holder.imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    holder.trans_id.setText("Wallet Recharge");
                    holder.imageView.setImageResource(R.drawable.ic_wallet_black);
                }
            }else{
                holder.trans_id.setText(jsonObject.getString("session_name"));
            CommonCall.LoadImage(activity,jsonObject.getString("session_image"),holder.imageView,R.drawable.ic_broken_image,R.drawable.ic_broken_image);
            }

            if(jsonObject.getString("transaction_type").equals("Income")){
                String next = "<font color='#008000'>"+"+$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount")))+"</font>";
                holder.amount.setText(Html.fromHtml(  next));
            }else{
                String next = "<font color='#FF0000'>"+"-$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount")))+"</font>";
                holder.amount.setText(Html.fromHtml(  next));
            }
    }else{
        if(jsonObject.getString("type").equals("Income")){
            holder.trans_id.setText("Buddi payment");
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
            String next = "<font color='#008000'>"+"+$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount")))+"</font>";
            holder.amount.setText(Html.fromHtml(  next));
        }else{
            holder.trans_id.setText("Money withdrawal");
            holder.imageView.setImageResource(R.drawable.ic_bank_building_black);
            String next = "<font color='#FF0000'>"+"-$"+String.format("%.2f",Float.parseFloat(jsonObject.getString("amount")))+"</font>";
            holder.amount.setText(Html.fromHtml(  next));
        }
    }

        holder.selectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, WalletHistoryDetailActivity.class);
                try {
                    intent.putExtra("dataArray", String.valueOf(dataArray.getJSONObject(Integer.parseInt(String.valueOf(view.getTag())))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activity.startActivity(intent);
            }
        });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {
        TextView trans_id, date, amount;
        LinearLayout selectCard;
        ImageView imageView;
    }

    public String convertTime3(String date) {

        String formattedDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM ");
        try {
            formattedDate = sdf.format((Iso8601.toCalendar(date).getTime()));

//            formattedDate = Long.parseLong(getTimeAgo(Iso8601.toCalendar(date).getTimeInMillis()));
//            formattedDate = Iso8601.toCalendar(date).getTimeInMillis();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
