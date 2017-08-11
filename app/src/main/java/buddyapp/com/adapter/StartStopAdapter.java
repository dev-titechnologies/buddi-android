package buddyapp.com.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import buddyapp.com.R;


/**
 * Created by yarolegovich on 08.03.2017.
 */

public class StartStopAdapter extends RecyclerView.Adapter<StartStopAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private ArrayList<String> data;

    public StartStopAdapter(ArrayList<String> data) {
        this.data = data;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.start_stop_item, parent, false);
        return new ViewHolder(v);
    }

  @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int iconTint = ContextCompat.getColor(holder.itemView.getContext(), R.color.grey);
        String forecast = null;
        try {
            forecast = data.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        holder.textView.setText(forecast.getCityName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.city_image);
            textView = (TextView) itemView.findViewById(R.id.city_name);

            itemView.findViewById(R.id.container).setOnClickListener(this);
        }

        public void showText() {
            int parentHeight = ((View) imageView.getParent()).getHeight();
            float scale = (parentHeight - textView.getHeight()) / (float) imageView.getHeight();
            imageView.setPivotX(imageView.getWidth() * 0.5f);
            imageView.setPivotY(0);
            imageView.animate().scaleX(scale)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            textView.setVisibility(View.VISIBLE);
                            imageView.setColorFilter(Color.BLACK);
                        }
                    })
                    .scaleY(scale).setDuration(200)
                    .start();
        }

        public void hideText() {
            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.grey));
            textView.setVisibility(View.INVISIBLE);
            imageView.animate().scaleX(1f).scaleY(1f)
                    .setDuration(200)
                    .start();
        }

        @Override
        public void onClick(View v) {
            parentRecycler.smoothScrollToPosition(getAdapterPosition());
        }
    }

//    private static class TintOnLoad implements RequestListener<Integer, GlideDrawable> {
//
//        private ImageView imageView;
//        private int tintColor;
//
//        public TintOnLoad(ImageView view, int tintColor) {
//            this.imageView = view;
//            this.tintColor = tintColor;
//        }
//
//        @Override
//        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
//            return false;
//        }
//
//        @Override
//        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//            imageView.setColorFilter(tintColor);
//            return false;
//        }
//    }
}