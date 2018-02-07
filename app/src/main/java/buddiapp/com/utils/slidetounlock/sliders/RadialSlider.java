package buddiapp.com.utils.slidetounlock.sliders;


import buddiapp.com.utils.slidetounlock.ISlidingData;

public class RadialSlider extends RectangleSlider {

    @Override
    public float getPercentage(ISlidingData data, int x, int y) {
        float vertical = mVerticalSlider.getPercentage(data, x, y);
        float horizontal = mHorizontalSlider.getPercentage(data, x, y);
        return (float) Math.sqrt(vertical * vertical + horizontal * horizontal);
    }

}
