package com.fastrrr.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastrrr.R;
import com.fastrrr.Type.UserType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Allied on 05-Apr-17.
 */

public class ContactUserAdapter extends BaseAdapter {

    public List<UserType> mData;
    private ArrayList<UserType> userTypeArrayList;
    Context mContext;
    ViewHolder viewHolder;


    //RoundImage roundedImage;

    public ContactUserAdapter(Context context, List<UserType> selectUsers) {
        mData = selectUsers;
        mContext = context;
        this.userTypeArrayList = new ArrayList<UserType>();
        this.userTypeArrayList.addAll(mData);
    }


    public interface OnDialCallClickListener {
        void onDialCallClickListenerCallback(UserType restaurantType);

    }

    public OnDialCallClickListener onDialCallClickListener;

    public void setDialCallClickListener(OnDialCallClickListener l) {
        onDialCallClickListener = l;
    }

    public OnDialCallClickListener getDialCallClickListener() {
        return onDialCallClickListener;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public UserType getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.item_contact_info, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        viewHolder = new ViewHolder();

        viewHolder.textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        viewHolder.imageViewUserImage = (ImageView) view.findViewById(R.id.imageViewUserImage);
        viewHolder.imageViewCall = (ImageView) view.findViewById(R.id.imageViewCall);

        final UserType data = (UserType) mData.get(i);
        String phoneNumber = data.getPhone();

        String Name = data.getName();
        if(Name.length() != 0) {
            viewHolder.textViewUserName.setText(Name);
        }

        String Image = data.getUserImage();
        Glide.with(mContext)
                .load(Image)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(viewHolder.imageViewUserImage);

        /*viewHolder.imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDialCallClickListener != null) {
                    onDialCallClickListener.onDialCallClickListenerCallback(data);
                }
            }
        });*/



        return view;
    }


    static class ViewHolder {
        ImageView imageViewUserImage, imageViewCall;
        TextView textViewUserName;
    }
}
