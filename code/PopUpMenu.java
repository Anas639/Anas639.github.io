
    import android.content.Context;
    import android.graphics.Bitmap;
    import android.graphics.Canvas;
    import android.graphics.Paint;
    import android.graphics.Path;
    import android.graphics.Rect;
    import android.graphics.RectF;
    import android.graphics.Region;
    import android.os.Handler;
    import android.util.AttributeSet;
    import android.view.MotionEvent;
    import android.view.View;

    import java.util.ArrayList;
    import java.util.List;

    /**/
    public class PopUpMenu extends View implements IUIThreadFriendly{


    private static final int MARG = 5;
    private static final float ANIM_SPEED = 0.3f; //How much of a second it takes to complete the animation

    private Handler _handler;
    private Behavior _MenuDisposed;
    private Behavior _outboundclick;
    private boolean _clickable = false;

    private List<PopUpMenuItem> _items = new ArrayList<>();
    private RectF _bounds;
    private Paint _wedgePaint = null;
    private Paint _backgroundPaint = null;
    private ViewMeasureProperty _cx =new ViewMeasureProperty().SetValue(100),
            _cy =new ViewMeasureProperty().SetValue(100),
            radius = new ViewMeasureProperty().SetValue(80);
    private FPSLooper _fpsLooper;

    private float _jumpAngle = 0;
    private float _sweepAngle = 0;
    private Path _middleClipPath;

    private double GetSpeed()
    {
        return (_sweepAngle*_fpsLooper.DeltaTime())/ANIM_SPEED;
    }

    public void SetOnMenuDisposed(Behavior e)
    {
        _MenuDisposed = e;
    }
    public void SetOnOutBoundClick(Behavior e)
    {
        _outboundclick = e;
    }
    public PopUpMenu AddItem(PopUpMenuItem item)
    {
        _items.add(item);
        _sweepAngle = 360/_items.size();
        return this;
    }
    public PopUpMenu SetRadius(int r)
    {
        radius.SetValue(r);
        return this;
    }
    public PopUpMenu SetCirclePoint(float x,float y)
    {
        //check if the circle will fit inside screen
        float rpx = radius.GetValueDP(getContext());//this will return radius dp in pixels
        float W = getResources().getDisplayMetrics().widthPixels;
        float H = getResources().getDisplayMetrics().heightPixels;
        if(x<rpx || x > W-rpx)
        {
            float distR = W-x;//ditance between x and right edge
            if(x/*x itself is the distance from left*/ > distR)
            {
                //then translate to the left
                x = W-rpx;
            }else
            {
                //translate to the right
                x = rpx;
            }
        }
        //same thing with y
        if(y<rpx || y > H-rpx)
        {
            float distB = W-x;//ditance between x and bottom edge
            if(y/*y itself is the distance from UP*/ > distB)
            {
                //then translate to the bottom
                y = H-rpx;
            }else
            {
                //translate to the upper edge
                y = rpx;
            }
        }
        _cx.SetValue(x);
        _cy.SetValue(y);
        return this;
    }
    public PopUpMenu(Context context)
    {
        super(context);
        InitializeMenu();
    }
    public PopUpMenu(Context context, AttributeSet attr){
        super(context,attr);
        InitializeMenu();
    }
    public PopUpMenu(Context context,AttributeSet attr,int defStyle)
    {
        super(context,attr,defStyle);
        InitializeMenu();
    }
    private void InitializeMenu()
    {
        _handler = new Handler();
        _fpsLooper = new FPSLooper();

        _bounds = new RectF();

        _wedgePaint = new Paint();
        _wedgePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        _wedgePaint.setColor(getResources().getColor(R.color.colorPrimaryBright));
        _wedgePaint.setAntiAlias(true);

        _backgroundPaint = new Paint();
        _backgroundPaint.setColor(0xa0000000);

        _middleClipPath = new Path();


    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        _bounds.left = _cx.GetValue()-radius.GetValueDP(getContext());
        _bounds.right = _cx.GetValue()+radius.GetValueDP(getContext());
        _bounds.top = _cy.GetValue()-radius.GetValueDP(getContext());
        _bounds.bottom = _cy.GetValue()+radius.GetValueDP(getContext());

        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        canvas.drawRect(0,0,w,h,_backgroundPaint);


        float radiusDP = radius.GetValueDP(getContext());
        float clipRadius = radiusDP/2f;
        _middleClipPath.reset();
        _middleClipPath.addCircle(_cx.GetValue(),_cy.GetValue(),clipRadius,Path.Direction.CW);
        canvas.clipPath(_middleClipPath,Region.Op.DIFFERENCE);

        if(_items.size()>0) {
            int count = _items.size();
            float startAngle = -MARG / 2, _sweepAngle = (360 / count)-MARG;
            for (int i = count-1; i >=0; i--) {
                PopUpMenuItem item = _items.get(i);
                canvas.drawArc(_bounds, startAngle + MARG, _sweepAngle, true, _wedgePaint);

                Bitmap bmp = item.GetIcon();
                if(bmp != null)
                {
                    float scale = 0.12f;
                bmp = Bitmap.createScaledBitmap(bmp,(int)(bmp.getWidth()*scale),(int)(bmp.getHeight()*scale),true);

                    //draw item icon
                    //test lets try to get the center of current wedge
                    float centerAngle = ((_sweepAngle+2*MARG/*- startAngle*/)/2)+(startAngle);
                    //convert angle to radians
                    centerAngle =(float)(centerAngle * Math.PI/180);
                    //let's get x,y of the center angle
                    //we know that x of angle is cos(angle)*radius and y of angle is sin(angle)*radius

                    double x=Math.cos(centerAngle),y=Math.sin(centerAngle);

                    //adjust x y
                    float iconRadius = (radiusDP+clipRadius) /2;
                    x*=iconRadius;
                    y*=iconRadius;
                    //to match global position of circle add cx to x and xy to y
                    x+=_cx.GetValue();
                    y+=_cy.GetValue();
                    //now draw the circle
                    Paint p = new Paint();
                    p.setColor(getResources().getColor(R.color.colorPrimary));
                    RectF bmpbounds = new RectF();
                    bmpbounds.left =(float)x-bmp.getWidth()/2;
                    bmpbounds.right =(float)x+bmp.getWidth()/2;
                    bmpbounds.top = (float)y-bmp.getHeight()/2;
                    bmpbounds.bottom = (float)y+bmp.getHeight()/2;
                   // canvas.drawCircle((float)x,(float)y,radius.GetValueDP(getContext())/4,p);
                    canvas.drawBitmap(bmp,new Rect(0,0,bmp.getWidth(),bmp.getHeight()),bmpbounds,null);

                }

                startAngle += _jumpAngle;
            }
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(!_clickable)
            return false;
        //Toast.makeText(getContext(),"ontouch "+event.getX()+","+event.getY()+" "+event.getAction(),Toast.LENGTH_SHORT).show();
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //let's test if the click is inside our circle
            if(_bounds.contains(event.getRawX(),event.getRawY())) {
                //now make coords relative to the circle center
                float x = event.getRawX() - _cx.GetValue();
                float y = -(event.getRawY() - _cy.GetValue());// * -1 because it's inverted on screen
                double theta = Math.atan2(y,x)*180/Math.PI;
                theta = (theta+360)%360;
                int gic = 360/_items.size();//group item count
                //f(x) = x/i
                int si = (int)Math.floor(theta/gic);
                _items.get(si).PerformClick();
            }else
            {

                Hide();
            }
            performClick();
            return true;
        }
        return true;
    }

    @Override
    public void RunOnUiThread(Runnable r) {
        _handler.post(r);
    }
    public void Show()
    {
        _fpsLooper.Loop();
        _fpsLooper.SetOnFrame(new Behavior() {
            @Override
            void Execute() {
                RunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //we should change the jumps angle until it reaches SweepAngle
                        _jumpAngle += GetSpeed();
                        if(_jumpAngle>=_sweepAngle) {
                            _jumpAngle = _sweepAngle;
                            _fpsLooper.Break();
                            _clickable = true;
                        }
                        invalidate();
                        _fpsLooper.Resume();
                    }
                });
            }
        });
    }
    public void Hide()
    {
        _clickable = false;
        _fpsLooper.Loop();
        _fpsLooper.SetOnFrame(new Behavior() {
            @Override
            void Execute() {
                RunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //we should change the jumps angle until it reaches 0
                        _jumpAngle -= GetSpeed();
                        if(_jumpAngle<=0) {
                            _jumpAngle = 0;
                            _fpsLooper.Break();


                            if(_MenuDisposed != null)
                                _MenuDisposed.Execute();
                        }
                        invalidate();
                        _fpsLooper.Resume();
                    }
                });
            }
        });
    }
    }