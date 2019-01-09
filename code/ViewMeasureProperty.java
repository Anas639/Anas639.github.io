import android.content.Context;

public class ViewMeasureProperty {
    private float value;
    public float GetValue()
    {
        return value;
    }
    public float GetValueDP(Context context)
    {
        return context.getResources().getDisplayMetrics().density*value;
    }
    public float GetValuePixel(Context context)
    {
        return value/context.getResources().getDisplayMetrics().density;
    }
    public ViewMeasureProperty SetValue(float val)
    {
        value = val;
        return this;
    }
}
