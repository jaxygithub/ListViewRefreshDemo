package com.xjj.pulltorefresh.demo.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/7/20.
 */

public class XJJFooterView {
    View footerView;
    View footerPb;  //进度圈，设置成View，而不是ProgressBar,为了更加灵活，如果不愿意用自带的进度条就可以随便换
    TextView footerTv;  //文字，固定必须是TextView类型

    public XJJFooterView(Context context, int layoutId,int pbId, int tvId) {
        footerView = LayoutInflater.from(context).inflate(layoutId, null);
        footerPb = footerView.findViewById(pbId);
        footerTv = (TextView) footerView.findViewById(tvId);
    }

    public View getfooterView() {
        return footerView;
    }


    public View getfooterPb() {
        return footerPb;
    }

    public TextView getfooterTv() {
        return footerTv;
    }
}
