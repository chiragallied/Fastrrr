package com.fastrrr.Services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fastrrr.R;

public class FloatingTimer extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    private CountDownTimer countDownTimer;
    int hourint,minuteint,secondint;
    long totalTimeCountInMilliseconds,timeBlinkInMilliseconds;
    private boolean blink;
    private TextView textViewStart;

    private EditText editTextHours,editTextMinute,editTextSeconds;
    private RelativeLayout relativeLayoutReset,relativeLayoutStart;

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
        final View myView = inflater.inflate(R.layout.service_timer, null);
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
        editTextHours = (EditText) view.findViewById(R.id.editTextHours);
        editTextMinute = (EditText) view.findViewById(R.id.editTextMinute);
        editTextSeconds = (EditText) view.findViewById(R.id.editTextSeconds);
        textViewStart = (TextView) view.findViewById(R.id.textViewStart);

        relativeLayoutStart = (RelativeLayout) view.findViewById(R.id.relativeLayoutStart);
        relativeLayoutReset = (RelativeLayout) view.findViewById(R.id.relativeLayoutReset);
    }

    public void UIClickEvent()
    {
        relativeLayoutStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(textViewStart.getText().equals("Pause"))
                {
                    countDownTimer.cancel();
                    textViewStart.setText("Resume");
                }

                else if(textViewStart.getText().equals("Resume"))
                {
                    textViewStart.setText("Pause");
                    if (editTextHours.getText().toString().equals("00")) {
                        editTextHours.setText("00");
                        if (editTextMinute.getText().toString().equals("00")) {
                            editTextMinute.setText("00");
                            secondint = Integer.valueOf(editTextSeconds.getText().toString());

                            totalTimeCountInMilliseconds = ((secondint)) * 1000;
                            timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                            countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

                                @Override
                                public void onTick(long leftTimeInMilliseconds) {
                                    long seconds = leftTimeInMilliseconds / 1000;
                                    //  mSeekArc.setVisibility(View.INVISIBLE);


                                    if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                        if (blink) {
                                            editTextHours.setVisibility(View.VISIBLE);
                                            editTextMinute.setVisibility(View.VISIBLE);
                                            editTextSeconds.setVisibility(View.VISIBLE);
                                        } else {
                                            editTextHours.setVisibility(View.INVISIBLE);
                                            editTextMinute.setVisibility(View.INVISIBLE);
                                            editTextSeconds.setVisibility(View.INVISIBLE);


                                        }

                                        blink = !blink;         // toggle the value of blink
                                    }
                                    //textViewMinute.setText(String.format(" : %02d", (seconds % 3600) / 60));
                                    editTextSeconds.setText(String.format("%02d", (seconds % 60)));

                                    // format the textview to show the easily readable format
                                }


                                @Override
                                public void onFinish() {
                                    // this function will be called when the timecount is finished
                                    //textViewShowTime.setText("Time up!");
                                    editTextHours.setVisibility(View.VISIBLE);
                                    editTextMinute.setVisibility(View.VISIBLE);
                                    editTextSeconds.setVisibility(View.VISIBLE);
                                    //  mSeekArc.setVisibility(View.VISIBLE);
                                }

                            }.start();
                        } else {
                            minuteint = Integer.valueOf(editTextMinute.getText().toString());
                            if (editTextSeconds.getText().toString().equals("00")) {
                                secondint = Integer.parseInt("00");
                            } else {
                                secondint = Integer.valueOf(editTextSeconds.getText().toString());
                            }

                            totalTimeCountInMilliseconds = ((minuteint * 60) + (secondint)) * 1000;
                            timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                            countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500)
                            {

                                @Override
                                public void onTick(long leftTimeInMilliseconds) {
                                    long seconds = leftTimeInMilliseconds / 1000;
                                    //  mSeekArc.setVisibility(View.INVISIBLE);


                                    if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                        if (blink) {
                                            editTextHours.setVisibility(View.VISIBLE);
                                            editTextMinute.setVisibility(View.VISIBLE);
                                            editTextSeconds.setVisibility(View.VISIBLE);
                                        } else {
                                            editTextHours.setVisibility(View.INVISIBLE);
                                            editTextMinute.setVisibility(View.INVISIBLE);
                                            editTextSeconds.setVisibility(View.INVISIBLE);


                                        }

                                        blink = !blink;         // toggle the value of blink
                                    }
                                    editTextMinute.setText(String.format("%02d", (seconds % 3600) / 60));
                                    editTextSeconds.setText(String.format("%02d", (seconds % 60)));

                                    // format the textview to show the easily readable format
                                }


                                @Override
                                public void onFinish() {
                                    // this function will be called when the timecount is finished
                                    //textViewShowTime.setText("Time up!");
                                    editTextHours.setVisibility(View.VISIBLE);
                                    editTextMinute.setVisibility(View.VISIBLE);
                                    editTextSeconds.setVisibility(View.VISIBLE);
                                    //  mSeekArc.setVisibility(View.VISIBLE);
                                }

                            }.start();
                        }
                    } else {

                        hourint = Integer.valueOf(editTextHours.getText().toString());
                        minuteint = Integer.valueOf(editTextMinute.getText().toString());
                        secondint = Integer.valueOf(editTextSeconds.getText().toString());

                        totalTimeCountInMilliseconds = ((hourint * 60 * 60) + (minuteint * 60) + (secondint)) * 1000;
                        timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

                            @Override
                            public void onTick(long leftTimeInMilliseconds) {
                                long seconds = leftTimeInMilliseconds / 1000;
                                //  mSeekArc.setVisibility(View.INVISIBLE);


                                if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                    if (blink) {
                                        editTextHours.setVisibility(View.VISIBLE);
                                        editTextMinute.setVisibility(View.VISIBLE);
                                        editTextSeconds.setVisibility(View.VISIBLE);
                                    } else {
                                        editTextHours.setVisibility(View.INVISIBLE);
                                        editTextMinute.setVisibility(View.INVISIBLE);
                                        editTextSeconds.setVisibility(View.INVISIBLE);


                                    }

                                    blink = !blink;         // toggle the value of blink
                                }
                                editTextHours.setText(String.format("%02d ", seconds / 3600));
                                editTextMinute.setText(String.format("%02d", (seconds % 3600) / 60));
                                editTextSeconds.setText(String.format("%02d", (seconds % 60)));

                                // format the textview to show the easily readable format
                            }


                            @Override
                            public void onFinish() {
                                // this function will be called when the timecount is finished
                                //textViewShowTime.setText("Time up!");
                                editTextHours.setVisibility(View.VISIBLE);
                                editTextMinute.setVisibility(View.VISIBLE);
                                editTextSeconds.setVisibility(View.VISIBLE);
                                //  mSeekArc.setVisibility(View.VISIBLE);
                            }

                        }.start();
                    }
                }
                else {
                    editTextHours.setCursorVisible(false);
                    editTextMinute.setCursorVisible(false);
                    editTextSeconds.setCursorVisible(false);
                    textViewStart.setText("Pause");
                    if (editTextHours.getText().toString().equals("")) {
                        editTextHours.setText("00");
                        if (editTextMinute.getText().toString().equals("")) {
                            editTextMinute.setText("00");
                            secondint = Integer.valueOf(editTextSeconds.getText().toString());

                            totalTimeCountInMilliseconds = ((secondint)) * 1000;
                            timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                            countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

                                @Override
                                public void onTick(long leftTimeInMilliseconds) {
                                    long seconds = leftTimeInMilliseconds / 1000;
                                    //  mSeekArc.setVisibility(View.INVISIBLE);


                                    if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                        if (blink) {
                                            editTextHours.setVisibility(View.VISIBLE);
                                            editTextMinute.setVisibility(View.VISIBLE);
                                            editTextSeconds.setVisibility(View.VISIBLE);
                                        } else {
                                            editTextHours.setVisibility(View.INVISIBLE);
                                            editTextMinute.setVisibility(View.INVISIBLE);
                                            editTextSeconds.setVisibility(View.INVISIBLE);
                                        }
                                        blink = !blink;         // toggle the value of blink
                                    }
                                    editTextSeconds.setText(String.format("%02d", (seconds % 60)));
                                }


                                @Override
                                public void onFinish() {
                                    // this function will be called when the timecount is finished
                                    //textViewShowTime.setText("Time up!");
                                    editTextHours.setVisibility(View.VISIBLE);
                                    editTextMinute.setVisibility(View.VISIBLE);
                                    editTextSeconds.setVisibility(View.VISIBLE);
                                    //  mSeekArc.setVisibility(View.VISIBLE);
                                }

                            }.start();
                        } else {
                            minuteint = Integer.valueOf(editTextMinute.getText().toString());
                            if (editTextSeconds.getText().toString().equals("")) {
                                secondint = Integer.parseInt("00");
                            } else {
                                secondint = Integer.valueOf(editTextSeconds.getText().toString());
                            }

                            totalTimeCountInMilliseconds = ((minuteint * 60) + (secondint)) * 1000;
                            timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                            countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500)
                            {

                                @Override
                                public void onTick(long leftTimeInMilliseconds) {
                                    long seconds = leftTimeInMilliseconds / 1000;
                                    //  mSeekArc.setVisibility(View.INVISIBLE);


                                    if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                        if (blink) {
                                            editTextHours.setVisibility(View.VISIBLE);
                                            editTextMinute.setVisibility(View.VISIBLE);
                                            editTextSeconds.setVisibility(View.VISIBLE);
                                        } else {
                                            editTextHours.setVisibility(View.INVISIBLE);
                                            editTextMinute.setVisibility(View.INVISIBLE);
                                            editTextSeconds.setVisibility(View.INVISIBLE);


                                        }

                                        blink = !blink;         // toggle the value of blink
                                    }
                                    editTextMinute.setText(String.format("%02d", (seconds % 3600) / 60));
                                    editTextSeconds.setText(String.format("%02d", (seconds % 60)));

                                    // format the textview to show the easily readable format
                                }


                                @Override
                                public void onFinish() {
                                    // this function will be called when the timecount is finished
                                    //textViewShowTime.setText("Time up!");
                                    editTextHours.setVisibility(View.VISIBLE);
                                    editTextMinute.setVisibility(View.VISIBLE);
                                    editTextSeconds.setVisibility(View.VISIBLE);
                                    //  mSeekArc.setVisibility(View.VISIBLE);
                                }

                            }.start();
                        }
                    } else {

                        hourint = Integer.valueOf(editTextHours.getText().toString());
                        minuteint = Integer.valueOf(editTextMinute.getText().toString());
                        secondint = Integer.valueOf(editTextSeconds.getText().toString());

                        totalTimeCountInMilliseconds = ((hourint * 60 * 60) + (minuteint * 60) + (secondint)) * 1000;
                        timeBlinkInMilliseconds = totalTimeCountInMilliseconds / 1000;
                        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {

                            @Override
                            public void onTick(long leftTimeInMilliseconds) {
                                long seconds = leftTimeInMilliseconds / 1000;
                                //  mSeekArc.setVisibility(View.INVISIBLE);


                                if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                                    if (blink) {
                                        editTextHours.setVisibility(View.VISIBLE);
                                        editTextMinute.setVisibility(View.VISIBLE);
                                        editTextSeconds.setVisibility(View.VISIBLE);
                                    } else {
                                        editTextHours.setVisibility(View.INVISIBLE);
                                        editTextMinute.setVisibility(View.INVISIBLE);
                                        editTextSeconds.setVisibility(View.INVISIBLE);


                                    }

                                    blink = !blink;         // toggle the value of blink
                                }
                                editTextHours.setText(String.format("%02d ", seconds / 3600));
                                editTextMinute.setText(String.format("%02d", (seconds % 3600) / 60));
                                editTextSeconds.setText(String.format("%02d", (seconds % 60)));

                                // format the textview to show the easily readable format
                            }


                            @Override
                            public void onFinish() {
                                // this function will be called when the timecount is finished
                                //textViewShowTime.setText("Time up!");
                                editTextHours.setVisibility(View.VISIBLE);
                                editTextMinute.setVisibility(View.VISIBLE);
                                editTextSeconds.setVisibility(View.VISIBLE);
                                //  mSeekArc.setVisibility(View.VISIBLE);
                            }

                        }.start();
                    }
                }

            }
        });

        relativeLayoutReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                editTextHours.setText("HH");
                editTextMinute.setText("MM");
                editTextSeconds.setText("SS");
                textViewStart.setText("Start");

            }
        });
    }
}