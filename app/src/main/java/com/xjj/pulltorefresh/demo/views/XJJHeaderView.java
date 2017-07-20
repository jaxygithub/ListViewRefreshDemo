package com.xjj.pulltorefresh.demo.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xjj.pulltorefresh.demo.R;

/**
 * Created by Administrator on 2017/7/20.
 */

public class XJJHeaderView {

    View headerView;
    ImageView headerImg;  //箭头图标，固定必须是图片
    View headerPb;  //进度圈，设置成View，而不是ProgressBar,为了更加灵活，如果不愿意用自带的进度条就可以随便换
    TextView headerTv;  //文字，固定必须是TextView类型

    public XJJHeaderView(Context context, int layoutId, int imgId, int pbId, int tvId) {
        headerView = LayoutInflater.from(context).inflate(layoutId, null);
        headerImg = (ImageView) headerView.findViewById(imgId);
        headerPb = headerView.findViewById(pbId);
        headerTv = (TextView) headerView.findViewById(tvId);
    }

    public View getHeaderView() {
        return headerView;
    }

    public ImageView getHeaderImg() {
        return headerImg;
    }

    public View getHeaderPb() {
        return headerPb;
    }

    public TextView getHeaderTv() {
        return headerTv;
    }
}
