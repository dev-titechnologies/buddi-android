package buddyapp.com.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

import buddyapp.com.R;
import buddyapp.com.activity.questions.Question4;
import buddyapp.com.database.DatabaseHandler;
import buddyapp.com.utils.CommonCall;

/**
 * Created by root on 20/7/17.
 */

public class SubCategoryAdapter extends BaseAdapter {

    HashSet cat;
    Context context;

DatabaseHandler db;

    public SubCategoryAdapter(Context context, HashSet category, DatabaseHandler db) {
        this.context = context;
        this.cat = category;
        this.db = db;
    }


    @Override
    public int getCount() {
        return cat.size();
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
    public View getView(int i, View view, ViewGroup parent) {
        CustomViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sub_cat_item, parent, false);
            holder = new CustomViewHolder();

            holder.catName = (TextView) view.findViewById(R.id.sub_cat_name);
            holder.cat_card = (CardView) view.findViewById(R.id.sub_cat_card);

//            holder.    hover = (SquareLayout) view.findViewById(R.id.hover);
            view.setTag(holder);

        } else {

            holder = (CustomViewHolder) view.getTag();

        }
        try {
            JSONObject catItem = db.getSubCat(cat.toArray()[i].toString());

            holder.catName.setText(catItem.getString("subCat_name"));



            holder.cat_card.setTag(catItem.getString("subCat_id"));


            holder.cat_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
if (Question4.sub_cat_selectedID.contains(view.getTag().toString())){

    Question4.sub_cat_selectedID.remove(view.getTag().toString());
}else
    Question4.sub_cat_selectedID.add(view.getTag().toString());

                    CommonCall.PrintLog("data cat ",Question4.sub_cat_selectedID.toString());
                   notifyDataSetChanged();
                }
            });

            if (Question4.sub_cat_selectedID.contains(catItem.getString("subCat_id"))){
                ( holder.cat_card ) .setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

                holder.catName.setTextColor(context.getResources().getColor(R.color.white));

            }else{
                ( holder.cat_card ) .setCardBackgroundColor(context.getResources().getColor(R.color.white));
                holder.catName.setTextColor(context.getResources().getColor(R.color.black));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    public class CustomViewHolder {

        TextView catName;
        CardView cat_card;


    }
}
