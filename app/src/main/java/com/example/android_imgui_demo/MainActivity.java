package com.example.android_imgui_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Used to load the 'android_imgui_demo' library on application startup.
    static {
        System.loadLibrary("android_imgui_demo");
    }

    private static View g_touchView;    // 触屏层
    private static GIViewWrapper g_drawView;    // 透明绘制层
    private static WindowManager g_winMrg;

    private static WindowManager.LayoutParams GetLayoutParams(boolean isDrawLayout)
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 将window类型设置为覆盖层
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
        {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        }

        // FLAG_FULLSCREEN:表示显示此窗口时隐藏所有屏幕装饰（包括状态栏）.
        // FLAG_TRANSLUCENT_STATUS:设置状态栏为透明并且为全屏模式。
        // FLAG_TRANSLUCENT_NAVIGATION:设置NavigationBar为透明并且为全屏模式。
        // FLAG_NOT_FOCUSABLE:表示此窗口不会获得按键输入焦点。
        params.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (isDrawLayout)
        {
            // FLAG_NOT_TOUCH_MODAL:在此模式下，系统会将当前Window区域以外的单击事件传递给底层的Window，当前Window区域以内的单击事件则自己处理。不开启此标记，其它Window无法接收到单击事件。
            // FLAG_NOT_TOUCHABLE:表示不接受触摸屏事件。
            params.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

        params.gravity = Gravity.LEFT | Gravity.TOP; // 左上
        params.format = PixelFormat.RGBA_8888; // 设置为透明状态

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Android允许控制是否在刘海区域内显示内容
            // LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES:在竖屏模式和横屏模式下，内容都会呈现到刘海区域中。
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        params.x = params.y = 0;
        params.width = params.height = WindowManager.LayoutParams.MATCH_PARENT;

        return params;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 授权悬浮权限,需要在AndroidMainifest.xml里设置uses-permission
        if (!Settings.canDrawOverlays(this))
        {
            Toast.makeText(this, "请授权应用悬浮窗权限", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        }

        // 隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //双悬浮方案，解决触摸问题
        g_touchView = new View(this);
        g_drawView = new GIViewWrapper(this);

        g_winMrg = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // 后续要动态更新tounchView的pos,size.所以这里单独获取一下.
        WindowManager.LayoutParams touchParams = GetLayoutParams(false);
        g_winMrg.addView(g_touchView,touchParams);
        g_winMrg.addView(g_drawView,GetLayoutParams(true));

        // imgui输入Event
        g_touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        NativeMethods.MotionEventClick(action != MotionEvent.ACTION_UP, motionEvent.getRawX(), motionEvent.getRawY());
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // 1秒后执行刷新,因为要等imgui先刷新出来.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] rect = NativeMethods.GetWindowRect().split("\\|");
                    touchParams.x = Integer.parseInt(rect[0]);
                    touchParams.y = Integer.parseInt(rect[1]);
                    touchParams.width = Integer.parseInt(rect[2]);
                    touchParams.height = Integer.parseInt(rect[3]);
                    g_winMrg.updateViewLayout(g_touchView, touchParams);
                } catch (Exception e) {}

                // 递归跑
                handler.postDelayed(this, 20);
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 删除绘制层
        if(g_drawView!=null)
        {
            g_winMrg.removeView(g_drawView);
        }

        // 删除触摸层
        if(g_touchView!=null)
        {
            g_winMrg.removeView(g_touchView);
        }
    }

}