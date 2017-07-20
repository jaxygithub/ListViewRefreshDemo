package com.xjj.pulltorefresh.demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xjj.pulltorefresh.demo.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */

public class XMyAdapter extends BaseAdapter {
    private List<String> data;
    private Context context;

    public XMyAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(data != null && data.size() != 0){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_lv,null);
            holder.img = (ImageView) view.findViewById(R.id.item_img);
            holder.tv = (TextView) view.findViewById(R.id.item_tv);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        String text = data.get(i);
        holder.tv.setText(text);
        return view;
    }

    private class ViewHolder{
        ImageView img;
        TextView tv;
    }
}
