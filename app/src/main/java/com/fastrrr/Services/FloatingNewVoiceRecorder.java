package com.fastrrr.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fastrrr.Adapter.RecorderListAdapter;
import com.fastrrr.R;
import com.fastrrr.Type.RecordType;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

public class FloatingNewVoiceRecorder extends Service{

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    Recorder recorder;
    Random random ;
    MediaPlayer mediaPlayer;
    int recordStatus = 0;
    int minute =0, seconds = 0, hour = 0;
    private TextView textViewTitle,file_name_text_view;
    private static final int SELECT_AUDIO = 2;
    String selectedPath = "";
    private SeekBar mSeekBar;
    private FloatingActionButton fab_play;
    Timer t;

    private RecorderListAdapter mAdapter;
    private ArrayList<RecordType> mData;
    private Button buttonPlay,buttonRecord,buttonSave;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private ListView listViewRecordingListing;
    private MediaPlayer mMediaPlayer = null;
    private RelativeLayout relativeLayoutListing,relativeLayoutRecording;

    private CardView mediaplayer_view;
    int saveRecordStatus = 0;
    private Handler mHandler = new Handler();
    String file,fileName;
    private boolean isPlaying = false;
    private TextView textViewTimer;

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
        mData = new ArrayList<RecordType>();
        mediaPlayer=new MediaPlayer();


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
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);

        textViewTimer = (TextView) view.findViewById(R.id.textViewTimer);

        mediaplayer_view = (CardView) view.findViewById(R.id.mediaplayer_view);

        textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        file_name_text_view = (TextView) view.findViewById(R.id.file_name_text_view);
        fab_play = (FloatingActionButton) view.findViewById(R.id.fab_play);

        relativeLayoutRecording = (RelativeLayout) view.findViewById(R.id.relativeLayoutRecording);
        relativeLayoutListing = (RelativeLayout) view.findViewById(R.id.relativeLayoutListing);
        relativeLayoutListing.setVisibility(View.GONE);

        listViewRecordingListing = (ListView) view.findViewById(R.id.listViewRecordingListing);
        buttonSave.setBackground(getResources().getDrawable(R.drawable.img_btn_forward));
        mediaplayer_view.setVisibility(View.GONE);

        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.primary), getResources().getColor(R.color.primary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);
    }

    public void UIClickEvent()
    {
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    //mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes,seconds));

                    updateSeekBar();

                } else if (mMediaPlayer == null && fromUser) {
                    //prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    mHandler.removeCallbacks(mRunnable);
                    mMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    //mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes,seconds));
                    updateSeekBar();
                }
            }
        });

        listViewRecordingListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                fileName = mAdapter.getItem(i).getRecordName();
                file = mAdapter.getItem(i).getRecordPath();
                mediaplayer_view.setVisibility(View.VISIBLE);
                file_name_text_view.setText(fileName);

            }
        });
        textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutRecording.setVisibility(View.VISIBLE);
                relativeLayoutListing.setVisibility(View.GONE);
            }
        });

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(recordStatus == 0) {
                    Toast.makeText(getApplicationContext(),"Start",Toast.LENGTH_LONG).show();
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

                    recordStatus = 1;
                    recorder.startRecording();
                    buttonSave.setBackground(getResources().getDrawable(R.drawable.save_button));
                    saveRecordStatus = 1;
                }
                else if(recordStatus == 1)
                {
                    t.cancel();
                    t.purge();

                    buttonSave.setBackground(getResources().getDrawable(R.drawable.img_btn_forward));
                    saveRecordStatus = 0;
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
                    saveRecordStatus = 1;
                    buttonSave.setBackground(getResources().getDrawable(R.drawable.save_button));
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
                if(saveRecordStatus == 1)
                {
                    recordStatus = 0;
                    Toast.makeText(getApplicationContext(),"Save",Toast.LENGTH_LONG).show();
                    recorder.stopRecording();
                    buttonRecord.post(new Runnable() {
                        @Override public void run() {
                            animateVoice(0);
                        }
                    });
                }
                else if(saveRecordStatus == 0)
                {
                    mediaplayer_view.setVisibility(View.GONE);
                    relativeLayoutRecording.setVisibility(View.GONE);
                    relativeLayoutListing.setVisibility(View.VISIBLE);
                    mData.clear();
                    getFile();
                    mAdapter = new RecorderListAdapter(getApplicationContext(),mData);
                    listViewRecordingListing.setAdapter(mAdapter);
                }

            }
        });

    }

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayer != null){

                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(mCurrentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
               // mCurrentProgressTextView.setText(String.format("%02d:%02d", minutes, seconds));

                updateSeekBar();
            }
        }
    };

    private void onPlay(boolean isPlaying){
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if(mMediaPlayer == null) {
                startPlaying(file); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            //pause the MediaPlayer
            pausePlaying();
        }
    }

    private void pausePlaying() {
        fab_play.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();
    }

    private void resumePlaying() {
        fab_play.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

    private void stopPlaying() {
        fab_play.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        isPlaying = !isPlaying;

        //mCurrentProgressTextView.setText(mFileLengthTextView.getText());
        mSeekBar.setProgress(mSeekBar.getMax());

        //allow the screen to turn off again once audio is finished playing
        //getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void startPlaying(String file) {
        fab_play.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(file);
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stopPlaying();
            }
        });

        updateSeekBar();

        //keep screen on while playing audio
        //((Activity) getApplicationContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

   public void getFile()
   {
       String path = Environment.getExternalStorageDirectory().toString()+"/SoundRecorder";
       Log.d("Files", "Path: " + path);
       File directory = new File(path);

       //String orderBy = MediaStore.Images.Thumbnails._ID + " DESC LIMIT 10";

       File[] files = directory.listFiles();
       Log.d("Files", "Size: "+ files.length);

       for (int i = 0; i < files.length; i++)
       {
           Log.d("Files", "FileName:" + files[i].getName());
           RecordType recordType = new RecordType();
           long createDate = files[i].lastModified();
           //Date d = new Date(createDate);
           SimpleDateFormat sdf5 = new SimpleDateFormat("dd/MM/yyyy");
           String strdate5 = sdf5.format(createDate);
           String fullPath = files[i].getPath();
           recordType.setRecordName(files[i].getName());
           recordType.setRecordPath(fullPath);
           recordType.setRecordDate(strdate5);

           mData.add(recordType);
       }
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