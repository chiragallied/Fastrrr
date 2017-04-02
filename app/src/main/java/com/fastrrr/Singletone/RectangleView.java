package com.fastrrr.Singletone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Chirag on 4/2/2017.
 */

public class RectangleView extends View {
    Paint paint = new Paint();
    double pointX;
    double pointY;
    double startX;
    double startY;

    public RectangleView(Context context, double x1, double y1, double x2, double y2)
    {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);

        startX=x1;
        startY=y1;
        pointX=x2;
        pointY=y2;    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        pointX = event.getX();
        pointY = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startX = pointX;
                startY = pointY;
                return true;
            case MotionEvent.ACTION_MOVE:
                pointX=event.getX();
                pointY=event.getY();
                break;
            default:
                return false;
        }
// Force a view to draw again
        postInvalidate();
        return true;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(Float.parseFloat(String.valueOf(startX)), Float.parseFloat(String.valueOf(startY)), Float.parseFloat(String.valueOf(pointX)), Float.parseFloat(String.valueOf(pointY)),paint);
    }
}
