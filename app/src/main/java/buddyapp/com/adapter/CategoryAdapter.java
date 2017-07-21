package buddyapp.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import buddyapp.com.R;
import buddyapp.com.activity.ChooseCategory;
import buddyapp.com.utils.CircleImageView;
import buddyapp.com.utils.CommonCall;

/**
 * Created by root on 20/7/17.
 */

public class CategoryAdapter extends BaseAdapter {

    JSONArray cat;
    Context context;



    public CategoryAdapter(Context context, JSONArray category) {
        this.context = context;
        this.cat = category;
    }


    @Override
    public int getCount() {
        return cat.length();
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
                    .inflate(R.layout.category_item, parent, false);
            holder = new CustomViewHolder();
            holder.catImage = (CircleImageView) view.findViewById(R.id.cat_image);
            holder.catName = (TextView) view.findViewById(R.id.cat_name);
            holder.cat_card = (CardView) view.findViewById(R.id.cat_card);

//            holder.    hover = (SquareLayout) view.findViewById(R.id.hover);
            view.setTag(holder);

        } else {

            holder = (CustomViewHolder) view.getTag();

        }
        try {
            JSONObject catItem = cat.getJSONObject(i);

            holder.catName.setText(catItem.getString("category_name"));


            CommonCall.LoadImage(context, catItem.getString("category_image"), holder.catImage
                    , R.drawable.ic_no_image, R.drawable.ic_broken_image


            );
            holder.cat_card.setTag(catItem.getString("category_id"));


            holder.cat_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
if (ChooseCategory.selectedID.contains(view.getTag().toString())){

    ChooseCategory.selectedID.remove(view.getTag().toString());
}else
    ChooseCategory.selectedID.add(view.getTag().toString());

                    CommonCall.PrintLog("data cat ",ChooseCategory.selectedID.toString());
                   notifyDataSetChanged();
                }
            });

            if (ChooseCategory.selectedID.contains(catItem.getString("category_id"))){
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
        public CircleImageView catImage;
        TextView catName;
        CardView cat_card;


    }
}
