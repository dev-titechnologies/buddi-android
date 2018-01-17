package buddiapp.com.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import buddiapp.com.R;
import buddiapp.com.Settings.Constants;
import buddiapp.com.Settings.PreferencesUtils;
import buddiapp.com.utils.CommonCall;
import buddiapp.com.utils.NetworkCalls;
import buddiapp.com.utils.Urls;

/**
 * Created by titech on 10/1/18.
 */

public class CardListAdapter extends BaseAdapter {
    JSONObject jsonData = new JSONObject();
    JSONObject sources = new JSONObject();
    JSONArray listArray = new JSONArray();
    Activity context;
    JSONArray cards = new JSONArray();
    String default_card = "";
    String sId = "", selected_card_id = "";
    ArrayList<String> selected_id = new ArrayList<>();
    String id = "";

    public CardListAdapter(JSONObject data, Activity applicationContext) {
        try{
            context = applicationContext;

            jsonData = data;
            if(PreferencesUtils.getData(Constants.user_type,context,"").equals(Constants.trainee)){
                sources = data.getJSONObject("sources");
                listArray = sources.getJSONArray("data");
//                if(!default_card.equals("null"))
                    default_card = jsonData.getString("default_source");

                if(default_card.length()>0 && !default_card.equals("null")){
                    selected_id.add(default_card);
                }
            }else{
                listArray = jsonData.getJSONArray("data");
                for (int i=0; i<listArray.length();i++){
                    if(listArray.getJSONObject(i).getBoolean("default_for_currency")){
                        default_card = listArray.getJSONObject(i).getString("id");
                        selected_id.add(default_card);
                    }
                }
            }


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return listArray.length();
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
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_list_item, viewGroup, false);
        view.setTag(holder);
        holder = new CustomViewHolder();
        holder.credit_card_text = view.findViewById(R.id.credit_card_text);
        holder.selectCard = view.findViewById(R.id.select_card);
        holder.selected_card_image = view.findViewById(R.id.selectCard_image);
        try{
            JSONObject jsonObject = listArray.getJSONObject(i);
            holder.credit_card_text.setText(
                    jsonObject.getString("brand")+" Ending with "+
                            jsonObject.getString("last4"));


        holder.selectCard.setTag(jsonObject.getString("id"));

//        holder.selectCard.setTag(i); //del

        holder.selectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                id = view.getTag().toString(); //del

                if(selected_id.contains(view.getTag().toString())){

                }else{
//                selected_id.clear();
//                selected_id.add(view.getTag().toString());
                    selected_card_id = view.getTag().toString();
                    new setDefaultCard().execute();
                }

            }
        });
/*
       //del
        if(id.length()>0 && selected_id.contains(i+"")){
            holder.selected_card_image.setImageResource(R.drawable.ic_tick);
        }else{
            holder.selected_card_image.setImageResource(R.drawable.ic_tick_grey);
        } //del
*/

        if(selected_id.size()>0 && selected_id.contains(jsonObject.getString("id"))){
            holder.selected_card_image.setImageResource(R.drawable.ic_tick);
        }else{
            holder.selected_card_image.setImageResource(R.drawable.ic_tick_grey);
        }

        holder.selectCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                try {
//                    JSONObject jsonObject1 = listArray.getJSONObject((Integer) view.getTag());
                    sId = String.valueOf(view.getTag());
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);

                builder.setCancelable(false);

                builder.setTitle("Warning!")
                        .setMessage("Do you want to remove this card?")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.KITKAT)
                            public void onClick(DialogInterface dialog, int which) {
                            new removeCard().execute();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        }catch (JSONException e){
            e.printStackTrace();
        }
        return view;
    }
    public class CustomViewHolder {
        TextView credit_card_text;
        LinearLayout selectCard;
        ImageView selected_card_image;
    }

    public class setDefaultCard extends AsyncTask<String, String, String> {
        String response1;
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                jsonObject.put("card_id",selected_card_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            response1 = NetworkCalls.POST(Urls.getDefaultStripeCardURL(), jsonObject.toString());
            return response1;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getInt("status")==1){
                selected_id.clear();
                selected_id.add(selected_card_id);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Your default card has been changed", Toast.LENGTH_SHORT).show();
                }else if(jsonObject.getInt("status")==2){
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }else if(jsonObject.getInt("status")==3){
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(context);
                }else{
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
            }

        }
    }

    public class removeCard extends AsyncTask<String, String, String> {
        String response;
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonCall.showLoader(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

//                jsonObject.put("custId", );
                jsonObject.put("card_token",sId);
                jsonObject.put(Constants.user_type, PreferencesUtils.getData(Constants.user_type,context,""));
             } catch (JSONException e) {
                e.printStackTrace();
            }
            response = NetworkCalls.POST(Urls.getRemoveStripeCardURL(), jsonObject.toString());
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getInt("status")==1){
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        listArray.remove(index);
                    }*/
                    new getCards().execute();


                }else if(jsonObject.getInt("status")==2){
                    CommonCall.hideLoader();
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }else if(jsonObject.getInt("status")==3){
                    CommonCall.hideLoader();
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    CommonCall.sessionout(context);
                }else{
                    CommonCall.hideLoader();
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                CommonCall.hideLoader();
                e.printStackTrace();
            }

        }
    }



    private class getCards extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            String res = NetworkCalls.POST(Urls.getFindDefaultCardURL(), "");


            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonCall.hideLoader();

            try {
                final JSONObject response = new JSONObject(s);

                if (response.getInt(Constants.status) == 1) {

                    if(PreferencesUtils.getData(Constants.user_type,context,"").equals(Constants.trainer)){
                        jsonData = response;
                        cards = response.getJSONArray("data");

                        for (int i=0; i<cards.length();i++){
                            if(cards.getJSONObject(i).getBoolean("default_for_currency")){
                                default_card = cards.getJSONObject(i).getString("id");
                                selected_id.add(default_card);
                            }
                        }


                    }else if(PreferencesUtils.getData(Constants.user_type,context,"").equals(Constants.trainee))
                    {
                        jsonData = response.getJSONObject("data");
                        sources = jsonData.getJSONObject("sources");
                        cards = sources.getJSONArray("data");

                        default_card = jsonData.getString("default_source");

                        if(default_card.length()>0 && !default_card.equals("null")){
                            selected_id.add(default_card);
                        }
                    }

                    /*JSONObject jsonData = response.getJSONObject("data");
                    JSONObject sources = jsonData.getJSONObject("sources");
                    JSONArray cards = sources.getJSONArray("data");*/
                    listArray = cards;
                    notifyDataSetChanged();

                    if (cards.length()==0){
                        PreferencesUtils.saveData(Constants.clientToken,"",context);
                    }else{

                        PreferencesUtils.saveData(Constants.clientToken,"cardsaved",context);

                    }
                }
            }
    catch (JSONException e){
            e.printStackTrace();
            }
        }
    }
}
