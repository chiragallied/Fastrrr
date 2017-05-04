package com.fastrrr.Services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fastrrr.Adapter.ContactUserAdapter;
import com.fastrrr.R;
import com.fastrrr.Singletone.Constants;
import com.fastrrr.Type.UserType;

import java.util.ArrayList;
import java.util.List;

public class FloatingDialer extends Service {

    WindowManager wm;
    Cursor phones;
    String lastnumber = "0";

    WebView web1;
    EditText ed1;
    Button buttonContact,buttonMenu;
    ImageView buttonClose;
    ProgressBar pbar;

    EditText edtPhoneNo;
    //TextView lblinfo;

    ArrayList<UserType> selectUsers;
    private ContactUserAdapter mAdapter;

    Button buttonZiro;
    String phoneNo;
    RelativeLayout relativeLayoutListing,relativeLayoutMain;
    ListView listViewContactList;

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
        final View myView = inflater.inflate(R.layout.service_contact_dialor1, null);
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
        Constants.showNotification(FloatingDialer.this,0);

        UIReference(myView);
        UIClickEvent();


      /*  buttonClose= (ImageView)myView.findViewById(R.id.buttonClose);
        web1 = (WebView)myView.findViewById(R.id.webView1);
        ed1 = (EditText)myView.findViewById(R.id.editText1);
        bt1 = (Button)myView.findViewById(R.id.button1);
        pbar = (ProgressBar)myView.findViewById(R.id.progressBar1);
        pbar.setVisibility(View.GONE);
        bt1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WebSettings webSetting = web1.getSettings();
                webSetting.setBuiltInZoomControls(true);
                webSetting.setJavaScriptEnabled(true);

                web1.setWebViewClient(new WebViewClient());

                web1.loadUrl("http://" + ed1.getText().toString());

            }
        });

*/
        selectUsers = new ArrayList<UserType>();
        getContactList();


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
                Constants.mNotificationManager.cancel(0);
            }
        });
        buttonClickEvent(myView);

        wm.addView(myView, parameters);
    }

    public void UIReference(View view)
    {
        relativeLayoutListing = (RelativeLayout) view.findViewById(R.id.relativeLayoutListing);
        relativeLayoutMain = (RelativeLayout) view.findViewById(R.id.relativeLayoutMain);

        buttonContact = (Button) view.findViewById(R.id.buttonContact);
        buttonMenu = (Button) view.findViewById(R.id.buttonMenu);
        edtPhoneNo = (EditText)view.findViewById(R.id.edtPhoneNumber);
        //lblinfo = (TextView)myView.findViewById(R.id.lblinfo);
        buttonClose= (ImageView)view.findViewById(R.id.buttonClose);
        buttonZiro= (Button) view.findViewById(R.id.buttonZiro);
        listViewContactList = (ListView) view.findViewById(R.id.listViewContactList);
        relativeLayoutListing.setVisibility(View.GONE);
    }

    public void UIClickEvent()
    {
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhoneNo.setVisibility(View.VISIBLE);
                relativeLayoutListing.setVisibility(View.GONE);
                relativeLayoutMain.setVisibility(View.VISIBLE);
            }
        });

        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhoneNo.setVisibility(View.GONE);
                relativeLayoutListing.setVisibility(View.VISIBLE);
                relativeLayoutMain.setVisibility(View.GONE);
            }
        });

        buttonZiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNo += "0";
                edtPhoneNo.setText(phoneNo);
            }
        });
        buttonZiro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                phoneNo += "+";
                edtPhoneNo.setText(phoneNo);
                return true;
            }
        });

        listViewContactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String PhoneNumber = mAdapter.getItem(i).getPhone();
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", PhoneNumber, null));
                //callIntent.setData(Uri.parse(PhoneNumber));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });
    }

    public void getContactList()
    {
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }
    public class WebViewClient extends android.webkit.WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            pbar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {

            // TODO Auto-generated method stub

            super.onPageFinished(view, url);
            pbar.setVisibility(View.GONE);
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
    public void buttonClickEvent(View v) {
        phoneNo = edtPhoneNo.getText().toString();
        try {

            switch (v.getId()) {
                case R.id.buttonStar:
                    //lblinfo.setText("");
                    phoneNo += "*";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonHash:
                    //lblinfo.setText("");
                    phoneNo += "#";
                    edtPhoneNo.setText(phoneNo);
                    break;
                /*case R.id.buttonZiro:
                    //lblinfo.setText("");
                    phoneNo += "0";
                    edtPhoneNo.setText(phoneNo);
                    break;*/
                case R.id.buttonOne:
                    //lblinfo.setText("");
                    phoneNo += "1";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonTwo:
                    //lblinfo.setText("");
                    phoneNo += "2";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonThree:
                    //lblinfo.setText("");
                    phoneNo += "3";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonFour:
                    //lblinfo.setText("");
                    phoneNo += "4";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonFive:
                    //lblinfo.setText("");
                    phoneNo += "5";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonSix:
                    //lblinfo.setText("");
                    phoneNo += "6";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonSeven:
                    //lblinfo.setText("");
                    phoneNo += "7";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonEight:
                    //lblinfo.setText("");
                    phoneNo += "8";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonNine:
                    //lblinfo.setText("");
                    phoneNo += "9";
                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.buttonBack:
                    //lblinfo.setText("");
                    if (phoneNo != null && phoneNo.length() > 0) {
                        phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                    }

                    edtPhoneNo.setText(phoneNo);
                    break;
                case R.id.btnClearAll:
                    //lblinfo.setText("");
                    edtPhoneNo.setText("");
                    break;
                case R.id.relativeLayoutDial:
                    if (phoneNo.trim().equals("")) {
                        //lblinfo.setText("Please enter a number to call on!");
                        Toast.makeText(getApplicationContext(),"Please enter a number to call on!",Toast.LENGTH_LONG).show();
                    } else {
                        Boolean isHash = false;
                        //phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                        //startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + phoneNo)));

                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", phoneNo, null));
                        //callIntent.setData(Uri.parse(PhoneNumber));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);

                        /*String callInfo = "tel:" + phoneNo + Uri.encode("#");
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(callInfo));
                        startActivity(callIntent);*/
                       /* if (phoneNo.subSequence(phoneNo.length() - 1, phoneNo.length()).equals("#")) {
                            phoneNo = phoneNo.substring(0, phoneNo.length() - 1);
                            String callInfo = "tel:" + phoneNo + Uri.encode("#");
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));
                            startActivity(callIntent);
                        } else {
                            String callInfo = "tel:" + phoneNo;
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(callInfo));
                            startActivity(callIntent);
                        }*/
                    }
                    break;
            }

        } catch (Exception ex) {

        }
    }

    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            List<UserType> res = new ArrayList<UserType>();


            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                }

                while (phones.moveToNext()) {
                    UserType selectUser = new UserType();
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String lookUp = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                    if (phoneNumber.equals(lastnumber))
                    {

                    }
                    else {
                        lastnumber = phoneNumber;
                        selectUser.setUserImage(image_thumb);
                        selectUser.setName(name);
                        selectUser.setPhone(phoneNumber);
                        selectUsers.add(selectUser);
                    }
                }
                phones.close();
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new ContactUserAdapter( FloatingDialer.this,selectUsers);
            //mAdapter.setDialCallClickListener(FloatingContactList.this);
            listViewContactList.setAdapter(mAdapter);
        }
    }
}