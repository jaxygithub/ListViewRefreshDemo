package com.xjj.pulltorefresh.demo.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by 晨兮_夏哥 on 2016/11/28.
 * CSDN：http://blog.csdn.net/gsw333
 */

public class XMyListView extends ListView implements AbsListView.OnScrollListener {
    Context context;

    OnItemClickListener onItemClickListener;
    LinearLayout headerLl;
    ImageView headImg;
    ProgressBar headPb;
    TextView headTv;

    final int NORMAL = 0;  //正常
    final int DOWN_PULL_TO_REFRESH = 1;  //下拉刷新
    final int REFRESH_TO_REFRESH = 2;  //松开刷新
    final int REFRESHING = 3;  //正在刷新
    int NOW_STATUS;  //当前状态
    int LAST_STATUS;  //上次的状态，只用于下拉刷新状态箭头动画的判断，如果是从正常状态转过来的则不需要显示动画；如果是从其他状态（松开刷新）转来的，则需要转换箭头方向的动画

    int firstVisibleItem;  //第一个可见的item
    boolean isOnTop;  //是否在listview最顶端
    int scollStatus;  //滚动状态
    int startY;  //手指按下的位置
    int header_height;  //header的高度

    int headerLayoutId, headerImgId, headerPbId, headerTvId;//header的布局，箭头图标，进度条，文本

    boolean isSetPullRefresh = false;//是否设置下拉刷新功能

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    public XMyListView(Context context) {
        this(context, null);
    }

    public XMyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XMyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setInit(int layoutId, int imgId, int pbId, int tvId) {
        this.headerLayoutId = layoutId;
        this.headerImgId = imgId;
        this.headerPbId = pbId;
        this.headerTvId = tvId;
        this.isSetPullRefresh = true;
        initView(context);
        setProperties();
    }

    private void initView(Context context) {
        headerLl = (LinearLayout) LayoutInflater.from(context).inflate(headerLayoutId, null);
        headImg = (ImageView) headerLl.findViewById(headerImgId);
        headPb = (ProgressBar) headerLl.findViewById(headerPbId);
        headTv = (TextView) headerLl.findViewById(headerTvId);
    }

    private void setProperties() {
        headerLl.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        header_height = headerLl.getMeasuredHeight();
        setMyPadding(-header_height);
        this.addHeaderView(headerLl);
        this.setOnScrollListener(this);
    }

    private void setMyPadding(int paddingTop) {
        headerLl.setPadding(0, paddingTop, 0, 0);
        headerLl.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        scollStatus = i;
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        this.firstVisibleItem = i;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSetPullRefresh) {  //如果不需要下拉刷新，直接返回
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    startY = (int) ev.getY();
                    isOnTop = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (NOW_STATUS == REFRESH_TO_REFRESH) {
                    NOW_STATUS = REFRESHING;
                    updateView();
                    //刷新
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isOnTop = false;
                            NOW_STATUS = NORMAL;
                            updateView();
                        }
                    }, 2000);
                } else if (NOW_STATUS == DOWN_PULL_TO_REFRESH) {
                    NOW_STATUS = NORMAL;
                    isOnTop = false;
                    updateView();
                }
                break;
            case MotionEvent.ACTION_MOVE:
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
                    setSelection(0);
                    LAST_STATUS = NORMAL;
                    NOW_STATUS = DOWN_PULL_TO_REFRESH;
                    updateView();
                }
                break;
            case DOWN_PULL_TO_REFRESH:
                setSelection(0);
                setMyPadding(space / 3 - header_height);
                if (space / 3 > header_height && scollStatus == SCROLL_STATE_TOUCH_SCROLL) {
                    LAST_STATUS = DOWN_PULL_TO_REFRESH;
                    NOW_STATUS = REFRESH_TO_REFRESH;
                    updateView();
                }
                break;
            case REFRESH_TO_REFRESH:
                setSelection(0);
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
                headImg.clearAnimation();
                setMyPadding(-header_height);
                headImg.setVisibility(VISIBLE);
                headPb.setVisibility(GONE);
                headTv.setText("下拉刷新");
                setSelection(0);
                break;
            case DOWN_PULL_TO_REFRESH:
                if (LAST_STATUS == NORMAL) {
                    return;
                }
                headImg.setVisibility(VISIBLE);
                headPb.setVisibility(GONE);
                headTv.setText("下拉刷新");
                headImg.clearAnimation();
                headImg.setAnimation(upToDownAnimation);
                break;
            case REFRESH_TO_REFRESH:
                headImg.setVisibility(VISIBLE);
                headPb.setVisibility(GONE);
                headTv.setText("释放刷新");
                headImg.clearAnimation();
                headImg.setAnimation(downToUpAnimation);
                break;
            case REFRESHING:
                setMyPadding(0);
                headImg.setVisibility(GONE);
                headPb.setVisibility(VISIBLE);
                headTv.setText("加载中");
                headImg.clearAnimation();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
        this.onItemClickListener = listener;
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        final boolean result;
        if (onItemClickListener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (position >= 1) {
                onItemClickListener.onItemClick(this, view, position - 1, id);
            }
            result = true;
        } else {
            result = false;
        }

        if (view != null) {
            view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
        }
        return result;
    }
}
