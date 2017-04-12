package com.fastrrr.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fastrrr.Adapter.RecorderListAdapter;
import com.fastrrr.R;
import com.fastrrr.Type.RecordType;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class FloatingNewVoiceRecorder extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    Recorder recorder;
    Random random ;

    int recordStatus = 0;
    private TextView textViewTitle;
    private static final int SELECT_AUDIO = 2;
    String selectedPath = "";

    private RecorderListAdapter mAdapter;
    private Button buttonPlay,buttonRecord,buttonSave;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private ListView listViewRecordingListing;
    private RelativeLayout relativeLayoutListing,relativeLayoutRecording;

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
        final View myView = inflater.inflate(R.layout.service_new_voice_recorder, null);
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
        random = new Random();

        UIReference(myView);
        UIClickEvent();
        setupRecorder();


        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void UIReference(View view)
    {
        buttonPlay = (Button) view.findViewById(R.id.buttonPlay);
        buttonRecord = (Button) view.findViewById(R.id.buttonRecord);
        buttonSave = (Button) view.findViewById(R.id.buttonSave);

        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);

        relativeLayoutRecording = (RelativeLayout) view.findViewById(R.id.relativeLayoutRecording);
        relativeLayoutListing = (RelativeLayout) view.findViewById(R.id.relativeLayoutListing);
        relativeLayoutListing.setVisibility(View.GONE);

        listViewRecordingListing = (ListView) view.findViewById(R.id.listViewRecordingListing);
    }

    public void UIClickEvent()
    {
        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(recordStatus == 0) {
                    Toast.makeText(getApplicationContext(),"Start",Toast.LENGTH_LONG).show();
                    recordStatus = 1;
                    recorder.startRecording();
                }
                else if(recordStatus == 1)
                {
                    Toast.makeText(getApplicationContext(),"Pause",Toast.LENGTH_LONG).show();
                    recordStatus = 2;
                    recorder.pauseRecording();
                    buttonRecord.postDelayed(new Runnable() {
                        @Override public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                }
                else if(recordStatus == 2) {
                    Toast.makeText(getApplicationContext(),"Resume",Toast.LENGTH_LONG).show();
                    recordStatus = 1;
                    recorder.resumeRecording();
                }
            }
        });


        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recorder.play
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordStatus = 0;
                Toast.makeText(getApplicationContext(),"Save",Toast.LENGTH_LONG).show();
                recorder.stopRecording();
                buttonRecord.post(new Runnable() {
                    @Override public void run() {
                        animateVoice(0);
                    }
                });
            }
        });

    }

    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }

    private void animateVoice(final float maxPeak) {
        buttonRecord.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100);
    }

    @NonNull
    private File file() {

        File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
        if (!folder.exists()) {
            folder.mkdir();
        }
        return new File(Environment.getExternalStorageDirectory() + "/SoundRecorder",  CreateRandomAudioFileName(3) + "AudioRecording.3gp");
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

    public void openGalleryAudio(){

        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent,"Select Audio "), SELECT_AUDIO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_AUDIO)
            {
                System.out.println("SELECT_AUDIO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                System.out.println("SELECT_AUDIO Path : " + selectedPath);
                //doFileUpload();
            }

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }




    /*FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + "/SoundRecorder") {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){
                        // user deletes a recording file out of the app

                        String filePath = android.os.Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]";



                        // remove file from database and recyclerview
                        RecordType recordType = new RecordType();
                        recordType
                        mAdapter.removeOutOfApp(filePath);
                    }
                }
            };*/
}