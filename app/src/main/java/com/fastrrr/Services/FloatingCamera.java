package com.fastrrr.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fastrrr.R;
import com.fastrrr.Singletone.Constants;

import java.io.File;
import java.util.ArrayList;

public class FloatingCamera extends Service {

    WindowManager wm;
    ImageView buttonClose;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        parameters.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View myView = inflater.inflate(R.layout.service_camera, null);
        myView.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        wm.updateViewLayout(myView, updatedParameters);

                    default:
                        break;
                }
                return false;
            }
        });
        Constants.showNotification(FloatingCamera.this,0);
        buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
                Constants.mNotificationManager.cancel(0);
            }
        });

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityFromChild(intent, 0);

        UIReference(myView);
        UIClickEvent();

        wm.addView(myView, parameters);
    }

    private void startActivityFromChild(Intent intent, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {

    }

    public void UIClickEvent()
    {

    }


}