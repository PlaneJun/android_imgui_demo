package com.example.android_imgui_demo;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GIViewWrapper extends GLSurfaceView implements GLSurfaceView.Renderer {

    public GIViewWrapper(Context ctx)
    {
        super(ctx);
        setEGLContextClientVersion(3);
        setEGLConfigChooser(8,8,8,8,16,0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setRenderer(this);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        NativeMethods.onDrawFrame();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        NativeMethods.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        NativeMethods.onSurfaceChanged(width,height);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NativeMethods.onDetachedFromWindow();
    }
}
