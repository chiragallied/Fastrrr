package com.fastrrr.Services;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fastrrr.Adapter.VideoPlayerAdapter;
import com.fastrrr.R;

public class FloatingMediaPlayer extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    private Cursor videoCursor;
    private int videColumnIndex;
    int count;
    String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID };
    private GridView gridViewVideoListing;

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
        final View myView = inflater.inflate(R.layout.service_media_player, null);
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

        UIReference(myView);
        UIClickEvent();
        init_phone_video_grid();
        buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);
        buttonMenu= (ImageView) myView.findViewById(R.id.buttonMenu);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
            }
        });
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getApplicationContext(), buttonMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });

        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {
        gridViewVideoListing = (GridView) view.findViewById(R.id.gridViewVideoListing);
    }

    public void UIClickEvent()
    {

    }

    private void init_phone_video_grid() {
        System.gc();
        String[] proj = { MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE };
        videoCursor =  getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,proj, null, null, null);
        count = videoCursor.getCount();
        gridViewVideoListing.setAdapter(new VideoPlayerAdapter(getApplicationContext()));
        //gridViewVideoListing.setOnItemClickListener(videogridlistener);
    }



    public class VideoPlayerAdapter extends BaseAdapter {

        String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID };
        private Cursor videocursor;
        private int video_column_index;
        int count;
        private Context mContext;
        public VideoPlayerAdapter(Context context)
        {

        }
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            View row = convertView;
            ViewHolder viewHolder = null;
            LayoutInflater inflater = null;

            if (row == null)
            {
                viewHolder = new ViewHolder();
                inflater = ((Activity) mContext).getLayoutInflater();
                row = inflater.inflate(R.layout.item_video_player, viewGroup, false);
                row.setTag(viewHolder);
                viewHolder.imageViewVideoPlayerIcon = (ImageView) row.findViewById(R.id.imageViewVideoPlayerIcon);
                viewHolder.textViewVideoTitle = (TextView) row.findViewById(R.id.textViewVideoTitle);
                viewHolder.textViewVideoTime = (TextView) row.findViewById(R.id.textViewVideoTime);


                video_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                videocursor.moveToPosition(position);

                String id = videocursor.getString(video_column_index);
                video_column_index = videocursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                videocursor.moveToPosition(position);

                viewHolder.textViewVideoTitle.setText(id);
                viewHolder.textViewVideoTime.setText(videocursor.getString(video_column_index));

                String[] proj = { MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATA };

                @SuppressWarnings("deprecation")
                Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj,
                        MediaStore.Video.Media.DISPLAY_NAME + "=?",
                        new String[] { id }, null);
                cursor.moveToFirst();
                long ids = cursor.getLong(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));

                ContentResolver crThumb = mContext.getContentResolver();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(
                        crThumb, ids, MediaStore.Video.Thumbnails.MICRO_KIND,
                        options);
                viewHolder.imageViewVideoPlayerIcon.setImageBitmap(curThumb);
                curThumb = null;

            }
            else
            {
                viewHolder = (ViewHolder) row.getTag();
            }
            return row;
        }
        public class ViewHolder
        {
            public TextView textViewVideoTitle,textViewVideoTime;
            public ImageView imageViewVideoPlayerIcon;
        }
    }

}