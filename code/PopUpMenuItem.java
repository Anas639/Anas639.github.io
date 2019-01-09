import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class PopUpMenuItem implements IStateChangable{
    private Context _context;
    private Bitmap _icon;
    private String _text;
    private Behavior _onClickListener;
    private boolean _selected;

    public PopUpMenuItem SetIcon(Bitmap bmp)
    {
        _icon = bmp;
        return this;
    }
    public PopUpMenuItem SetIcon(Drawable d)
    {
        SetIcon(((BitmapDrawable)d).getBitmap());
        return this;
    }
    public PopUpMenuItem SetIcon(int res)
    {
        SetIcon(_context.getResources().getDrawable(res));
        return this;
    }
    public Bitmap GetIcon()
    {
        return _icon;
    }
    public PopUpMenuItem SetText(String txt)
    {
        _text = txt;
        return this;
    }
    public String GetText()
    {
        return _text;
    }
    public PopUpMenuItem SetOnclickListener(Behavior listener)
    {
        _onClickListener = listener;
        return this;
    }
    public void PerformClick()
    {
        if(_onClickListener != null)
            _onClickListener.Execute();
    }
    public PopUpMenuItem(Context c)
    {
        _context = c;
    }

    @Override
    public void ChangeState() {
        _selected = !_selected;
    }
}
