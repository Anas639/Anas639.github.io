public class FPSLooper implements ISuspendable {


    private Behavior _onFrame = null;

    private double _deltaTime = 0;
    private int _fps = 60;//default is 60 frames per second
    private boolean _looping = false;

    public double DeltaTime()
    {
        return _deltaTime;
    }
    public void SetFPS(int fps)
    {
        _fps = fps;
    }
    public void SetOnFrame(Behavior e)
    {
        _onFrame = e;
    }
    public FPSLooper()
    {

    }
    public void Loop()
    {
        if(_looping)
            return;
        _looping = true;
        //start loop thread
        new Thread(new Runnable() {
            @Override
            public void run() {


                long last_time = System.currentTimeMillis();
                while(_looping)
                {
                    //delta time is the time spent executing last frame
                    _deltaTime = (double)(System.currentTimeMillis() - last_time)/1000;


                    last_time = System.currentTimeMillis();
                    //fire on frame event
                    if(_onFrame != null) {
                        _onFrame.Execute();
                        Suspend();
                    }
                    long millis_diff = System.currentTimeMillis() - last_time;
                    if(millis_diff < 1000/_fps)
                    {
                        //sleep until one exact frame time has past
                        try
                        {
                            long sleepTime = (1000/_fps)-millis_diff;
                            Thread.sleep(sleepTime);
                        }catch (InterruptedException e)
                        {

                        }
                    }
                }
            }
        }).start();
    }
    public void Break()
    {
        _looping = false;
    }

    @Override
    public void Suspend() {
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Resume() {
        synchronized (this) {
            notify();
        }
    }