package com.xjj.pulltorefresh.demo.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;

import com.xjj.pulltorefresh.demo.adapter.XJJBaseRvAdapter;

/**
 * Created by Administrator on 2017/7/19.
 */

public class XJJRecyclerView extends RecyclerView {
    Context context;
    XJJPullToRefreshListener pullToRefreshListener;
    XJJHeaderView headerView;
    XJJFooterView footerView;

    final int NORMAL = 0;  //正常
    final int DOWN_PULL_TO_REFRESH = 1;  //下拉刷新
    final int REFRESH_TO_REFRESH = 2;  //松开刷新
    final int REFRESHING = 3;  //正在刷新
    int NOW_STATUS;  //当前状态
    int LAST_STATUS;  //上次的状态，只用于下拉刷新状态箭头动画的判断，如果是从正常状态转过来的则不需要显示动画；如果是从其他状态（松开刷新）转来的，则需要转换箭头方向的动画

    int firstVisibleItem;  //第一个可见的item
    boolean isOnTop;  //是否在listview最顶端
    boolean isLoadMoreFinish = true;
    int startY;  //手指按下的位置
    int header_height;  //header的高度

    boolean isSetPullRefresh = false;//是否设置下拉刷新功能

    public XJJRecyclerView(Context context) {
        this(context, null);
    }

    public XJJRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XJJRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    //header的布局，箭头图标，进度条，文本
    public void setInit(XJJHeaderView xjjHeaderView, XJJFooterView xjjFooterView, XJJPullToRefreshListener listener) {
        this.pullToRefreshListener = listener;
        this.isSetPullRefresh = true;
        this.headerView = xjjHeaderView;
        this.footerView = xjjFooterView;
        setProperties();
    }

    private void setProperties() {
        headerView.getHeaderView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        header_height = headerView.getHeaderView().getMeasuredHeight();
        setMyPadding(-header_height);
        this.addOnScrollListener(new OnScrollListener() {//滚动监听，为了知道是不是在顶部
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    //获取第一个可见的item的position，如果没有数据，返回-1；如果有数据，由于添加了header，所以firstVisibleItem为1说明是在顶部
                    firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                    if (!ViewCompat.canScrollVertically(recyclerView, 1) && pullToRefreshListener != null
                            && ((XJJBaseRvAdapter) getAdapter()).getLoadMoreFooterView() != null && isLoadMoreFinish) {
                        //显示footer并加载下一页
                        isLoadMoreFinish = false;
                        footerView.getfooterPb().setVisibility(VISIBLE);
                        footerView.getfooterTv().setText("正在加载更多数据");
                        pullToRefreshListener.xjjLoadMore();
                    }
                }
            }
        });
    }

    public View getPullToRefreshHeaderView() {
        if (!isSetPullRefresh) {  //如果不需要下拉刷新，直接返回
            return null;
        }
        return headerView.getHeaderView();
    }

    private void setMyPadding(int paddingTop) {
        headerView.getHeaderView().setPadding(0, paddingTop, 0, 0);
        headerView.getHeaderView().invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isSetPullRefresh || ((XJJBaseRvAdapter) getAdapter()).getPullToRefreshHeaderView() == null) {  //如果不需要下拉刷新，直接返回
            return super.dispatchTouchEvent(ev);
        }
        if (firstVisibleItem == 1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchEvent(ev);  //item或者其中的view如果设置了事件，那么改rcv不会分发down事件，所以不会执行下拉事件的处理，这里手动分发down事件
        }
        if (firstVisibleItem == -1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchEvent(ev);  //item或者其中的view如果设置了事件，那么改rcv不会分发down事件，所以不会执行下拉事件的处理，这里手动分发down事件
        }
        if (firstVisibleItem != -1 && firstVisibleItem != 1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            return super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSetPullRefresh || ((XJJBaseRvAdapter) getAdapter()).getPullToRefreshHeaderView() == null) {  //如果不需要下拉刷新，直接返回
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 1 || firstVisibleItem == -1) {  //因为添加了header，所以第一个item是1的时候才是top,如果没有数据，那么就是-1
                    startY = (int) ev.getY();
                    isOnTop = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (NOW_STATUS == REFRESH_TO_REFRESH) {
                    NOW_STATUS = REFRESHING;
                    updateView();
                    //刷新
                    if (pullToRefreshListener != null) {
                        pullToRefreshListener.xjjRefresh();
                    }
                } else if (NOW_STATUS == DOWN_PULL_TO_REFRESH) {
                    NOW_STATUS = NORMAL;
                    isOnTop = false;
                    updateView();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (firstVisibleItem > 1) {  //排除在底部的时候下拉导致执行了touch事件
                    isOnTop = false;
                } else {
                    isOnTop = true;
                }
                //比较繁杂，单独写在moveEvent方法中
                moveEvent(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }

    //手指移动处理
    private void moveEvent(MotionEvent ev) {
        if (!isOnTop) return;
        int moveY = (int) ev.getY();
        int space = moveY - startY;
        switch (NOW_STATUS) {
            case NORMAL:
                if (space > 0) {
                    scrollToPosition(0);
                    LAST_STATUS = NORMAL;
                    NOW_STATUS = DOWN_PULL_TO_REFRESH;
                    updateView();
                }
                break;
            case DOWN_PULL_TO_REFRESH:
                scrollToPosition(0);
                setMyPadding(space / 3 - header_height);
                if (space / 3 > header_height) {
                    LAST_STATUS = DOWN_PULL_TO_REFRESH;
                    NOW_STATUS = REFRESH_TO_REFRESH;
                    updateView();
                }
                break;
            case REFRESH_TO_REFRESH:
                scrollToPosition(0);
                setMyPadding(space / 3 - header_height);
                if (space / 3 < header_height && space > 0) {
                    LAST_STATUS = REFRESH_TO_REFRESH;
                    NOW_STATUS = DOWN_PULL_TO_REFRESH;
                    updateView();
                } else if (space <= 0) {
                    LAST_STATUS = REFRESH_TO_REFRESH;
                    NOW_STATUS = NORMAL;
                    isOnTop = false;
                    updateView();
                }
                break;
        }
    }

    //更改header布局样式
    private void updateView() {
        RotateAnimation downToUpAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        downToUpAnimation.setDuration(300);
        downToUpAnimation.setFillAfter(true);
        RotateAnimation upToDownAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        upToDownAnimation.setDuration(300);
        upToDownAnimation.setFillAfter(true);
        switch (NOW_STATUS) {
            case NORMAL:
                headerView.getHeaderImg().clearAnimation();
                setMyPadding(-header_height);
                headerView.getHeaderImg().setVisibility(VISIBLE);
                headerView.getHeaderPb().setVisibility(GONE);
                headerView.getHeaderTv().setText("下拉刷新");
                scrollToPosition(0);
                break;
            case DOWN_PULL_TO_REFRESH:
                if (LAST_STATUS == NORMAL) {
                    return;
                }
                headerView.getHeaderImg().setVisibility(VISIBLE);
                headerView.getHeaderPb().setVisibility(GONE);
                headerView.getHeaderTv().setText("下拉刷新");
                headerView.getHeaderImg().clearAnimation();
                headerView.getHeaderImg().setAnimation(upToDownAnimation);
                break;
            case REFRESH_TO_REFRESH:
                headerView.getHeaderImg().setVisibility(VISIBLE);
                headerView.getHeaderPb().setVisibility(GONE);
                headerView.getHeaderTv().setText("松开刷新");
                headerView.getHeaderImg().clearAnimation();
                headerView.getHeaderImg().setAnimation(downToUpAnimation);
                break;
            case REFRESHING:
                setMyPadding(0);
                headerView.getHeaderImg().setVisibility(GONE);
                headerView.getHeaderPb().setVisibility(VISIBLE);
                headerView.getHeaderTv().setText("正在刷新");
                headerView.getHeaderImg().clearAnimation();
                break;
        }
    }

    //单页加载完成
    public void xjjLoadMoreComplete() {
        isLoadMoreFinish = true;
        footerView.getfooterPb().setVisibility(GONE);
        footerView.getfooterTv().setText("加载更多");
    }

    public void xjjLoadNoMoreData() {
        isLoadMoreFinish = true;
        footerView.getfooterPb().setVisibility(GONE);
        footerView.getfooterTv().setText("亲，没有更多数据了~(>_<)~");
    }

    //单次刷新完成
    public void xjjRefreshComplete() {
        isOnTop = false;
        NOW_STATUS = NORMAL;
        updateView();
    }

    public interface XJJPullToRefreshListener {
        void xjjLoadMore();

        void xjjRefresh();
    }
}
