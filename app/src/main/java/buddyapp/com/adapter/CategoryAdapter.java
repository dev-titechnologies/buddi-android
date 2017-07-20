package buddyapp.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import buddyapp.com.R;

/**
 * Created by root on 20/7/17.
 */

public class CategoryAdapter extends BaseAdapter {

JSONArray cat;
    Context context;
    public CategoryAdapter(Context context, JSONArray category){
this.context=context;
        this.cat=category;
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
                    .inflate(R.layout.feed_list_item, parent, false);
            holder = new CustomViewHolder();
            holder.  catImage = (ImageView) view.findViewById(R.id.cat_image);
            holder.   catName = (TextView) view.findViewById(R.id.cat_name);


//            holder.    hover = (SquareLayout) view.findViewById(R.id.hover);
            view.setTag(holder);

        }else{

            holder = (CustomViewHolder) view.getTag();

        }

        return null;
    }

    public class CustomViewHolder {
        public ImageView catImage;
       TextView catName;




    }
}
