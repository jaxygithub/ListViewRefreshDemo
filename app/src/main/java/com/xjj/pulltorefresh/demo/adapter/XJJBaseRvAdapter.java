package com.xjj.pulltorefresh.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Administrator on 2017/6/16.
 */

public class XJJBaseRvAdapter<T> extends RecyclerView.Adapter<XJJBaseRvHolder> {

    protected Context context;
    protected ItemDataListener itemDataListener;
    private View pullToRefreshHeaderView, headerView, loadMoreFooterView;

    protected int layoutId;
    protected List<T> datas;
    public static final int TYPE_HEADER = 0;  //正常头部
    public static final int TYPE_PULL_TO_REFRESH_HEADER = 1;  //下拉刷新头部
    public static final int TYPE_NORMAL = 2;  //正常数据
    public static final int TYPE_FOOTER = 3;  //上拉footer

    public XJJBaseRvAdapter(Context context, int layoutId, List<T> datas) {
        this.context = context;
        this.layoutId = layoutId;
        this.datas = datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    //添加下拉刷新头
    public void addPullToRefreshHeaderView(View addPullToRefreshHeaderView) {
        if (headerView != null) return;  //如果已经先添加了headerView，就不能增加下拉头了
        if (pullToRefreshHeaderView != null || addPullToRefreshHeaderView == null) {
            return;
        }
        this.pullToRefreshHeaderView = addPullToRefreshHeaderView;
        notifyItemInserted(0);
    }

    //添加头部布局（非下拉头），仅限一个
    public void addHeaderView(View addHeaderView) {
        if (addHeaderView == null || headerView != null) {
            return;
        }
        this.headerView = addHeaderView;
        notifyItemInserted(pullToRefreshHeaderView == null ? 0 : 1);
    }

    //添加footeer
    public void addLoadMoreFooterView(View addLoadMoreFooterView) {
        if (loadMoreFooterView != null || addLoadMoreFooterView == null) {
            return;
        }
        this.loadMoreFooterView = addLoadMoreFooterView;
        notifyItemInserted(getItemCount() - 1);
    }

    public View getPullToRefreshHeaderView(){
        return pullToRefreshHeaderView;
    }

    public View getLoadMoreFooterView(){
        return loadMoreFooterView;
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMoreFooterView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        if (pullToRefreshHeaderView == null && headerView == null) {
            return TYPE_NORMAL;
        }
        if (pullToRefreshHeaderView == null && headerView != null) {
            if (position == 0) {
                return TYPE_HEADER;
            }
        }
        if (pullToRefreshHeaderView != null && headerView == null) {
            if (position == 0) {
                return TYPE_PULL_TO_REFRESH_HEADER;
            }
        }
        if (pullToRefreshHeaderView != null && headerView != null) {
            if (position == 0) return TYPE_PULL_TO_REFRESH_HEADER;
            if (position == 1) return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    //获取真实的position（与datalist对应，因为添加了头部，会使得position和data对应不上）
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (pullToRefreshHeaderView == null) {
            return headerView == null ? position : position - 1;
        } else {
            return headerView == null ? position - 1 : position - 2;
        }
    }

    public void setItemDataListener(ItemDataListener listener) {
        itemDataListener = listener;
    }

    @Override
    public XJJBaseRvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (pullToRefreshHeaderView != null && viewType == TYPE_PULL_TO_REFRESH_HEADER) {//如果是下拉头
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
            pullToRefreshHeaderView.setLayoutParams(layoutParams);
            return new XJJBaseRvHolder(context, pullToRefreshHeaderView);
        }
        if (headerView != null && viewType == TYPE_HEADER) {//如果是正常头
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(layoutParams);
            return new XJJBaseRvHolder(context, headerView);
        }
        if (loadMoreFooterView != null && viewType == TYPE_FOOTER) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
            loadMoreFooterView.setLayoutParams(layoutParams);
            return new XJJBaseRvHolder(context, loadMoreFooterView);
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        XJJBaseRvHolder holder = new XJJBaseRvHolder(context, view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final XJJBaseRvHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_PULL_TO_REFRESH_HEADER) {//如果是头部，不做数据填充
            return;
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            return;
        } else {
            if (itemDataListener == null) {
                return;
            }
            itemDataListener.setItemData(holder, datas.get(getRealPosition(holder)));
        }
    }

    @Override
    public int getItemCount() {
        if (pullToRefreshHeaderView == null) {
            if (headerView == null) {
                return loadMoreFooterView == null ? datas.size() : datas.size() + 1;
            } else {
                return loadMoreFooterView == null ? datas.size() + 1 : datas.size() + 2;
            }
        } else {
            if (headerView == null) {
                return loadMoreFooterView == null ? datas.size() + 1 : datas.size() + 2;
            } else {
                return loadMoreFooterView == null ? datas.size() + 2 : datas.size() + 3;
            }
        }
    }

    public interface ItemDataListener<T> {
        void setItemData(XJJBaseRvHolder holder, T t);
    }

}
