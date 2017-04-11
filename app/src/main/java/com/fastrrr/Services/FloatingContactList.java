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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fastrrr.Adapter.ContactUserAdapter;
import com.fastrrr.R;
import com.fastrrr.Type.UserType;

import java.util.ArrayList;
import java.util.List;

public class FloatingContactList extends Service {

    WindowManager wm;
    ImageView buttonClose,buttonMenu;
    ProgressBar pbar;
    private ListView listViewContactList;
    Cursor phones;
    ArrayList<UserType> selectUsers;
    private ContactUserAdapter mAdapter;
    String lastnumber = "0";


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
        final View myView = inflater.inflate(R.layout.service_contact_list, null);
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
        //buttonMenu= (ImageView) myView.findViewById(R.id.buttonMenu);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(myView);
                stopSelf();
                //System.exit(0);
            }
        });
        /*buttonMenu.setOnClickListener(new View.OnClickListener() {
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
        });*/
        UIReference(myView);
        UICkickEvent();
        selectUsers = new ArrayList<UserType>();
        getContactList();

        wm.addView(myView, parameters);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
    public void getContactList()
    {
        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    public void UIReference(View view)
    {
        listViewContactList = (ListView) view.findViewById(R.id.listViewContactList);
    }

    public void UICkickEvent()
    {
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
    /*@Override
    public void onDialCallClickListenerCallback(UserType userType) {

        try {
            String PhoneNumber = userType.getPhone();
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(PhoneNumber));
            //callIntent.setData(Uri.parse(PhoneNumber));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            startActivity(callIntent);
        }
        catch (Exception e){

        }
    }*/

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
            mAdapter = new ContactUserAdapter( FloatingContactList.this,selectUsers);
            //mAdapter.setDialCallClickListener(FloatingContactList.this);
            listViewContactList.setAdapter(mAdapter);
        }
    }
}