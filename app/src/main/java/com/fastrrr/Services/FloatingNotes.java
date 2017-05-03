package com.fastrrr.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fastrrr.Activity.NotesActivity;
import com.fastrrr.Adapter.PDFFileAdapter;
import com.fastrrr.R;
import com.fastrrr.Singletone.DatabaseAccess;
import com.fastrrr.Type.Memo;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FloatingNotes extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonClose1;
    ProgressBar pbar;

    private ListView listView;
    private Button btnAdd;
    private Button btnShare;
    private Button btnList;
    private DatabaseAccess databaseAccess;
    private List<Memo> memos;
    Button btnClose;
    private Memo memo;
    MemoAdapter adapter;

    double x;
    double y;
    int pressedX;
    int pressedY;
    String UpdateMemo;
    TextView textViewNoNotes;

    private EditText editTextEnterNote,editTextEnterTitle;

    private ImageView imageViewAddNotes;

    private RelativeLayout relativeLayoutAddNotes,relativeLayoutMain;

    int  mCurrentX,mCurrentY;
    int updateMeno;

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
        final View myView = inflater.inflate(R.layout.service_notes, null);
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


        UIReference(myView);
        UIClick();
        this.databaseAccess = DatabaseAccess.getInstance(this);

        databaseAccess.open();
        FloatingNotes.this.memos = databaseAccess.getAllMemos();

        if(memos.size() > 0)
        {
            textViewNoNotes.setVisibility(View.GONE);
            databaseAccess.close();
            adapter = new MemoAdapter(FloatingNotes.this, memos);
            FloatingNotes.this.listView.setAdapter(adapter);
        }
        else {
            textViewNoNotes.setVisibility(View.VISIBLE);
        }


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
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
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnShare = (Button) view.findViewById(R.id.btnShare);
        btnList = (Button) view.findViewById(R.id.btnList);
        listView = (ListView) view.findViewById(R.id.listView);
        textViewNoNotes = (TextView) view.findViewById(R.id.textViewNoNotes);
        relativeLayoutAddNotes = (RelativeLayout) view.findViewById(R.id.relativeLayoutAddNotes);
        relativeLayoutMain = (RelativeLayout) view.findViewById(R.id.relativeLayoutMain);
        relativeLayoutAddNotes.setVisibility(View.GONE);
        imageViewAddNotes = (ImageView) view.findViewById(R.id.imageViewAddNotes);
        editTextEnterNote = (EditText) view.findViewById(R.id.editTextEnterNote);
        editTextEnterTitle = (EditText) view.findViewById(R.id.editTextEnterTitle);

        /*MemoAdapter adapter = new MemoAdapter(this, memos);
        listView.setAdapter(adapter);*/
    }
    public void UIClick()
    {
        imageViewAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getApplicationContext().startService(new Intent(getApplicationContext(),FloatingEditNotes.class));

                relativeLayoutMain.setVisibility(View.GONE);
                relativeLayoutAddNotes.setVisibility(View.VISIBLE);
                editTextEnterNote.setHint("Enter Note");
                editTextEnterTitle.setHint("Enter Title");
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.VISIBLE);
                relativeLayoutMain.setVisibility(View.VISIBLE);
                relativeLayoutAddNotes.setVisibility(View.GONE);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String sendText = editTextEnterNote.getText().toString();
                sendIntent.putExtra(Intent.EXTRA_TEXT,sendText);
                sendIntent.setType("text/plain");
                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);;
                startActivity(sendIntent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewNoNotes.setVisibility(View.GONE);
                String Title = editTextEnterTitle.getText().toString();
                String Notes = editTextEnterNote.getText().toString();
                if(Title.equalsIgnoreCase("")) {

                    Toast.makeText(getApplicationContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                /*databaseAccess.open();
                FloatingNotes.this.memos = databaseAccess.getAllMemos();
                databaseAccess.close();
                MemoAdapter adapter = new MemoAdapter(FloatingNotes.this, memos);
                FloatingNotes.this.listView.setAdapter(adapter);*/
                }
                else {
                    if(Notes.equalsIgnoreCase(""))
                    {
                        Toast.makeText(getApplicationContext(),"Please Enter Notes",Toast.LENGTH_LONG).show();
                    }
                    else {
                        onSaveClicked();
                        relativeLayoutMain.setVisibility(View.VISIBLE);
                        relativeLayoutAddNotes.setVisibility(View.GONE);
                        databaseAccess.open();
                        FloatingNotes.this.memos = databaseAccess.getAllMemos();
                        databaseAccess.close();
                        MemoAdapter adapter = new MemoAdapter(FloatingNotes.this, memos);
                        FloatingNotes.this.listView.setAdapter(adapter);
                    }

                }


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Memo memo = memos.get(i);
                UpdateMemo = memos.get(i).getText();
                TextView txtMemo = (TextView) view.findViewById(R.id.txtMemo);
                if (memo.isFullDisplayed()) {
                    txtMemo.setText(memo.getShortText());
                    memo.setFullDisplayed(false);
                } else {
                    txtMemo.setText(memo.getText());
                    memo.setFullDisplayed(true);
                }
            }
        });
    }

    public void onDeleteClicked(Memo memo) {
        databaseAccess.open();
        databaseAccess.delete(memo);
        databaseAccess.close();

        ArrayAdapter<Memo> adapter = (ArrayAdapter<Memo>) listView.getAdapter();
        adapter.remove(memo);
        adapter.notifyDataSetChanged();
    }

    public void onEditClicked(Memo memo) {

        relativeLayoutMain.setVisibility(View.GONE);
        relativeLayoutAddNotes.setVisibility(View.VISIBLE);
            UpdateMemo = memo.getText();
            String Title = memo.getNoteName();
            editTextEnterNote.setText(UpdateMemo);
            editTextEnterTitle.setText(Title);
    }

    private class MemoAdapter extends ArrayAdapter<Memo> {
        Context mContext;

        public MemoAdapter(Context context, List<Memo> objects) {
            super(context, 0, objects);
            this.mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                /*LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.item_notes_list, parent, false);*/
                LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = li.inflate(R.layout.item_notes_list, null);
            }

            ImageView btnEdit = (ImageView) convertView.findViewById(R.id.btnEdit);
            ImageView btnDelete = (ImageView) convertView.findViewById(R.id.btnDelete);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
            TextView txtMemo = (TextView) convertView.findViewById(R.id.txtMemo);

            final Memo memo = memos.get(position);
            memo.setFullDisplayed(false);
            txtDate.setText(memo.getDate());
            txtMemo.setText(memo.getShortText());
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMeno = 1;
                    onEditClicked(memo);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked(memo);
                }
            });
            return convertView;
        }
    }

    public void onSaveClicked() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        if(memo == null) {
            // Add new memo
            Memo temp = new Memo();
            temp.setText(editTextEnterNote.getText().toString());
            temp.setNoteName(editTextEnterTitle.getText().toString());
            databaseAccess.save(temp);
        } else {
            // Update the memo
            memo.setText(editTextEnterNote.getText().toString());
            memo.setNoteName(editTextEnterTitle.getText().toString());
            databaseAccess.update(memo);
        }
        databaseAccess.close();
    }


}