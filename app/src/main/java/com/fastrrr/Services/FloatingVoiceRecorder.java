package com.fastrrr.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fastrrr.R;
import com.fastrrr.Singletone.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.security.AccessController.getContext;

public class FloatingVoiceRecorder extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    int minute =0, seconds = 0, hour = 0;
    Button buttonStart, buttonPause, buttonPlay, buttonStopPlayingRecording ;
    //ImageView imageViewPlay,imageViewRecord;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;

    private TextView textViewTimer;
    Timer t;

    int recordStatus = 1 ,playStatus = 1;


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
        final View myView = inflater.inflate(R.layout.service_voice_recorder, null);
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
        Constants.showNotification(FloatingVoiceRecorder.this,0);
        buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);
        buttonMenu= (ImageView) myView.findViewById(R.id.buttonMenu);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
                Constants.mNotificationManager.cancel(0);
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

        UIReference(myView);
        UIClickEvent();


        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {
       /* imageViewRecord = (ImageView) view.findViewById(R.id.imageViewRecord);
        imageViewPlay = (ImageView) view.findViewById(R.id.imageViewPlay);*/

        buttonStart = (Button) view.findViewById(R.id.button);
        buttonPlay = (Button) view.findViewById(R.id.button2);


        textViewTimer = (TextView) view.findViewById(R.id.textViewTimer);
        buttonPlay.setEnabled(false);
        /*buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);*/

        random = new Random();


    }

    public void UIClickEvent()
    {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if(buttonStart.getText().toString().equalsIgnoreCase("Record"))
                if(recordStatus == 1)
                {
                    //imageViewPlay.setClickable(false);
                    buttonPlay.setEnabled(true);
                    recordStatus = 0;
                    textViewTimer.setText("00 : 00 : 00");
                    if (checkPermission()) {
                        buttonStart.setBackgroundResource(R.drawable.record2);
                        //buttonStart.setText("Stop");
                        t = new Timer("hello", true);
                        t.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                //imageViewRecord.setImageDrawable(getResources().getDrawable(R.drawable.record2));
                                textViewTimer.post(new Runnable() {

                                    public void run() {
                                        seconds++;
                                        if (seconds == 60) {
                                            seconds = 0;
                                            minute++;
                                        }
                                        if (minute == 60) {
                                            minute = 0;
                                            hour++;
                                        }
                                        textViewTimer.setText(""
                                                + (hour > 9 ? hour : ("0" + hour)) + " : "
                                                + (minute > 9 ? minute : ("0" + minute))
                                                + " : "
                                                + (seconds > 9 ? seconds : "0" + seconds));
                                    }
                                });
                            }
                        }, 1000, 1000);

                        File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        AudioSavePathInDevice = folder + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Toast.makeText(FloatingVoiceRecorder.this, "Recording started", Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                }
               // else if(buttonStart.getText().toString().equalsIgnoreCase("Stop")){
                else if(recordStatus == 0){
                    //imageViewPlay.setClickable(true);
                    //buttonStart.setText("Record");
                    buttonStart.setBackgroundResource(R.drawable.record1);
                    buttonPlay.setEnabled(true);
                    recordStatus = 1;
                    t.cancel();
                    t.purge();
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    mediaRecorder.release();
                    //mediaRecorder.stop();
                    Toast.makeText(FloatingVoiceRecorder.this, "Recording Completed", Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if(recordStatus == 0) {
                    recordStatus = 2;
                    mediaRecorder.pause();
                    // recording paused
                } else if(recordStatus == 2) {
                    mediaRecorder.resume();
                    // resume recording
                }

                /*//if(buttonPlay.getText().toString().equalsIgnoreCase("Play"))
                if(playStatus == 1)
                {
                    //imageViewPlay.setImageDrawable(getResources().getDrawable(R.d));
                    //imageViewRecord.setClickable(false);
                    buttonStart.setEnabled(false);
                    buttonPlay.setBackgroundResource(R.drawable.pause);
                   // buttonPlay.setText("Pause");
                    playStatus = 0;
                    mediaPlayer = new MediaPlayer();

                    try {
                        mediaPlayer.setDataSource(AudioSavePathInDevice);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();

                    Toast.makeText(FloatingVoiceRecorder.this, "Recording Playing", Toast.LENGTH_LONG).show();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            Log.i("Completion Listener","Song Complete");
                            Toast.makeText(getApplicationContext(), "Media Completed", Toast.LENGTH_SHORT).show();
                            buttonStart.setEnabled(true);
                            buttonPlay.setBackgroundResource(R.drawable.play);
                            playStatus = 1;
                        }
                    });

                }
                //else if(buttonPlay.getText().toString().equalsIgnoreCase("Pause")){
                else if(playStatus == 0){
                    playStatus = 1;
                    buttonStart.setEnabled(true);
                    buttonPlay.setBackgroundResource(R.drawable.play);
                    //buttonPlay.setText("Record");
                    //imageViewRecord.setClickable(true);
                    if(mediaPlayer != null){

                        mediaPlayer.stop();
                        mediaPlayer.release();

                        MediaRecorderReady();

                    }
                }*/
            }
        });

         /*buttonPause.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if(buttonPause.getText().toString().equalsIgnoreCase("Pause")) {
                     buttonPause.setText("Resume");
                     mediaRecorder.stop();
                     mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                     mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                     mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                     FileOutputStream paused_file = null;
                     try {
                         paused_file = new FileOutputStream(AudioSavePathInDevice);
                     } catch (FileNotFoundException e) {
                         e.printStackTrace();
                     }
                     try {
                         mediaRecorder.setOutputFile(paused_file.getFD());
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
                 else if(buttonPause.getText().toString().equalsIgnoreCase("Resume"))
                 {
                     try {
                         mediaRecorder.prepare();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                     mediaRecorder.start();
                 }
             }
         });*/



        /*buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.cancel();

                mediaRecorder.stop();

                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(FloatingVoiceRecorder.this, "Recording Completed", Toast.LENGTH_LONG).show();

            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException, SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                Toast.makeText(FloatingVoiceRecorder.this, "Recording Playing", Toast.LENGTH_LONG).show();

            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){

                    mediaPlayer.stop();
                    mediaPlayer.release();

                    MediaRecorderReady();

                }

            }
        });*/

    }

    public void MediaRecorderReady(){

        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){

        StringBuilder stringBuilder = new StringBuilder( string );

        int i = 0 ;
        while(i < string ) {

            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(FloatingVoiceRecorder.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(FloatingVoiceRecorder.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


}