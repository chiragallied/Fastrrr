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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import com.fastrrr.R;

import java.io.File;
import java.util.ArrayList;

public class FloatingGallery extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;

    private GridView gridViewImageList;

    File[] mediaFiles;
    File imageDir;
    ImageButton btncam;
    String name = null;
    ArrayList<Bitmap> bmpArray = new ArrayList<Bitmap>();
    ArrayList<String> fileName = new ArrayList<String>();
    public static final String TAG = "Album3Activity";

    static Cursor imageCursor;
    static int columnIndex;



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
        final View myView = inflater.inflate(R.layout.service_gallery, null);
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

        buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
            }
        });

        UIReference(myView);
        UIClickEvent();
        getImageList();

        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {
        gridViewImageList = (GridView) view.findViewById(R.id.gridViewImageList);
    }

    public void UIClickEvent()
    {

    }

    public void getImageList()
    {
        String[] imgColumnID = {MediaStore.Images.Thumbnails._ID};
        Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        imageCursor = getContentResolver().query(uri, imgColumnID, null, null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
        columnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        //gridView = (GridView)findViewById(R.id.gridview);
        gridViewImageList.setAdapter(new ImageAdapter(getApplicationContext()));
        /*gridView.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                String[] dataLocation = {MediaStore.Images.Media.DATA};
                //String []dataLocation = {Environment.getExternalStorageDirectory().getAbsolutePath()};
                imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        dataLocation, null, null, null);
                columnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                imageCursor.moveToPosition(position);
                String imgPath = imageCursor.getString(columnIndex);
                in = new Intent(getApplicationContext(), FullScreen.class);
                in.putExtra("id", imgPath);
                startActivity(in);
            }//onItemClickListener
        });*/

    }

    public static class ImageAdapter extends BaseAdapter {
        private Context context;
        //Album1Activity act = new Album1Activity();
        public ImageAdapter(Context context){
            this.context = context;
        }//ImageAdapter
        public int getCount() {
            // TODO Auto-generated method stub
            return imageCursor.getCount();
        }//getCount

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }//getItem

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }//getItemId

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;
            if(convertView == null){
                iv = new ImageView(context);
                imageCursor.moveToPosition(position);
                int imageID = imageCursor.getInt(columnIndex);
                iv.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        "" +imageID));
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setPadding(5, 5, 5, 5);
                iv.setLayoutParams(new GridView.LayoutParams(100, 100));
            }//if
            else{
                iv = (ImageView)convertView;
            }//else
            return iv;
        }//getView

    }

}