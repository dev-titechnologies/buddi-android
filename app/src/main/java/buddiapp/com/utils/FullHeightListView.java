package buddiapp.com.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FullHeightListView extends ListView {

    public FullHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullHeightListView(Context context) {
        super(context);
    }

    public FullHeightListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}