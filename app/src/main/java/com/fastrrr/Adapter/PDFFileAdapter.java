package com.fastrrr.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fastrrr.R;
import com.fastrrr.Type.UserType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allied on 05-Apr-17.
 */

public class PDFFileAdapter extends BaseAdapter {

    public List<File> mData;
    private ArrayList<String> userTypeArrayList;
    Context mContext;
    ViewHolder viewHolder;


    //RoundImage roundedImage;

    public PDFFileAdapter(Context context, List<File> selectUsers) {
        mData = selectUsers;
        mContext = context;

    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public File getItem(int i) {
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
            view = li.inflate(R.layout.item_pdf_file, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        viewHolder = new ViewHolder();

        viewHolder.textViewPDFFileName = (TextView) view.findViewById(R.id.textViewPDFFileName);

        File FileName = (File) getItem(i);
        //final UserType data = (UserType) mData.get(i);
        //String phoneNumber = data.getPhone();

        String Name = String.valueOf(FileName);
        Name = Name.substring(Name.lastIndexOf("/")+1);

        if(FileName.length() != 0) {
            viewHolder.textViewPDFFileName.setText(Name);
        }

        return view;
    }


    static class ViewHolder {
        TextView textViewPDFFileName;
    }
}
