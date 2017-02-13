package com.blogspot.unixnme.detectiontest;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, FrequencyCounter.CallBack {

    private static String TAG = MainActivity.class.getSimpleName();
    private Camera camera;
    private TextView tv;
    private FrequencyCounter counter;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);

        counter = new FrequencyCounter();
        counter.setFrequencyCallback(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (counter != null) {
            counter.stop();
        }
    }

    public void onFrequencyCalculated(double freq) {
        tv.setText(Double.toString(freq));
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surface created");
        camera = Camera.open();
        if (camera != null) {
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(this);
            try {
                camera.setPreviewDisplay(holder);
            } catch (Throwable t) {}
        }
        if (camera != null)
            camera.startPreview();
        if (counter != null)
            counter.start(1);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surface changed");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surface destroyed");
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        counter.increment();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
