package com.leessy.demo.floatwindow;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class FxService extends Service {

    //定义浮动窗口布局  
    LinearLayout mFloatLayout;
    LinearLayout mFloatLayout2;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;

//    Button mFloatView;

    private static final String TAG = "FxService";

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub  
        super.onCreate();
        Log.i(TAG, "oncreat");
        createFloatView();
        createFloatView2();//第二个窗口
        showNotification();
    }

    //显示同时 前台服务
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void showNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.face_notification);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.face_notification));//下拉下拉列表里面的图标（大图标）
        builder.setTicker("  弹窗服务启动.....");
        builder.setContentTitle("弹窗服务行中");
        builder.setContentText("---------不解释.----------");
        Notification notification = builder.build();
        startForeground(1, notification);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub  
        return null;
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper  
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type  
//        wmParams.type = LayoutParams.TYPE_PHONE;//4.4及以前添加悬浮窗需要设置成TYPE_PHONE
        wmParams.type = LayoutParams.TYPE_TOAST;//4.4之后，悬浮窗设置成TYPE_TOAST,不需要向系统申请权限
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶  
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
  
         /*// 设置悬浮窗口长宽数据 
        wmParams.width = 200; 
        wmParams.height = 80;*/

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局  
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout  
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮  
        ImageView imageView = mFloatLayout.findViewById(R.id.image);
        Button button = mFloatLayout.findViewById(R.id.button);
        Button close = mFloatLayout.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(mFloatLayout);
            }
        });

//        Glide.with(getApplication()).load(R.drawable.timg).asGif().into(imageView);
//        Glide.with(getApplication()).load("http://pic34.photophoto.cn/20150202/0005018384491898_b.jpg")
//                .into(imageView);
        Glide.with(getApplication()).load(R.drawable.timg).asGif().into(imageView);
//        Glide.with(getApplication()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527617344159&di=b942a121c9aa4e292fbc37080e4b19a5&imgtype=0&src=http%3A%2F%2Fimgs0.soufunimg.com%2Fnews%2F2018_01%2F15%2F1515987658049.gif")
//                .asGif().into(imageView);
//
//        Glide.with(getApplication()).load(R.drawable.f1).asGif()
//                .into(imageView);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
//        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动  
        mFloatLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {//移动时
                    //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                    wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                    Log.i(TAG, "RawX" + event.getRawX());
                    Log.i(TAG, "X" + event.getX());
                    //减25为状态栏的高度
                    wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2;
                    Log.i(TAG, "RawY" + event.getRawY());
                    Log.i(TAG, "Y" + event.getY());
                    //刷新
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub  
//                Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FxService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void createFloatView2() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
//        wmParams.type = LayoutParams.TYPE_PHONE;//4.4及以前添加悬浮窗需要设置成TYPE_PHONE
        wmParams.type = LayoutParams.TYPE_TOAST;//4.4之后，悬浮窗设置成TYPE_TOAST,不需要向系统申请权限
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout2 = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout2, wmParams);
        //浮动窗口按钮
        ImageView imageView = mFloatLayout2.findViewById(R.id.image);
        Button button = mFloatLayout2.findViewById(R.id.button);
        Button close = mFloatLayout2.findViewById(R.id.close);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindowManager.removeView(mFloatLayout2);
            }
        });

//        Glide.with(getApplication()).load(R.drawable.timg).asGif().into(imageView);
        Glide.with(getApplication()).load(R.drawable.f1).asGif().into(imageView);
//        Glide.with(getApplication()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527617344159&di=b942a121c9aa4e292fbc37080e4b19a5&imgtype=0&src=http%3A%2F%2Fimgs0.soufunimg.com%2Fnews%2F2018_01%2F15%2F1515987658049.gif")
//                .asGif().into(imageView);
//        Glide.with(getApplication()).load(R.drawable.f1).asGif()
//                .into(imageView);
        mFloatLayout2.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
//        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mFloatLayout2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {//移动时
                    // TODO Auto-generated method stub
                    //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                    wmParams.x = (int) event.getRawX() - mFloatLayout2.getMeasuredWidth() / 2;
                    Log.i(TAG, "RawX" + event.getRawX());
                    Log.i(TAG, "X" + event.getX());
                    //减25为状态栏的高度
                    wmParams.y = (int) event.getRawY() - mFloatLayout2.getMeasuredHeight() / 2;
                    Log.i(TAG, "RawY" + event.getRawY());
                    Log.i(TAG, "Y" + event.getY());
                    //刷新
                    mWindowManager.updateViewLayout(mFloatLayout2, wmParams);
                }
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FxService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub  
        super.onDestroy();
        if (mFloatLayout != null) {
            //移除悬浮窗口  
            mWindowManager.removeView(mFloatLayout);
            mWindowManager.removeView(mFloatLayout2);
        }
    }

}  