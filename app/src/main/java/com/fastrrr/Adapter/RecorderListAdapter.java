package com.fastrrr.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fastrrr.R;
import com.fastrrr.Type.RecordType;
import com.fastrrr.Type.UserType;

import java.util.List;

/**
 * Created by Allied on 11-Apr-17.
 */

public class RecorderListAdapter extends BaseAdapter {
    Context mContext;
    public List<RecordType> mData;
    ViewHolder viewHolder;

    public RecorderListAdapter(Context context, List<RecordType> selectUsers){
        mData = selectUsers;
        mContext = context;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public RecordType getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.item_recorder_list, null);
            Log.e("Inside", "here--------------------------- In view1");
        } else {
            view = convertView;
            Log.e("Inside", "here--------------------------- In view2");
        }

        viewHolder = new ViewHolder();

        viewHolder.textViewRecorderName = (TextView) view.findViewById(R.id.textViewRecorderName);
        viewHolder.textViewRecorderDate = (TextView) view.findViewById(R.id.textViewRecorderDate);

        final RecordType data =(RecordType) mData.get(i);
        String recordName = data.getRecordName();
        if(recordName.length() != 0) {
            viewHolder.textViewRecorderName.setText(recordName);
        }

        String recordDate = data.getRecordDate();
        if(recordName.length() != 0) {
            viewHolder.textViewRecorderDate.setText(recordDate);
        }

        return view;
    }

    static class ViewHolder {
        TextView textViewRecorderName,textViewRecorderDate;
    }
}
