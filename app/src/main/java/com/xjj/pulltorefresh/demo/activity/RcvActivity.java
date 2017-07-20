package com.xjj.pulltorefresh.demo.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.pulltorefresh.demo.R;
import com.xjj.pulltorefresh.demo.adapter.XJJBaseRvAdapter;
import com.xjj.pulltorefresh.demo.adapter.XJJBaseRvHolder;
import com.xjj.pulltorefresh.demo.views.XJJFooterView;
import com.xjj.pulltorefresh.demo.views.XJJHeaderView;
import com.xjj.pulltorefresh.demo.views.XJJRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RcvActivity extends AppCompatActivity implements XJJRecyclerView.XJJPullToRefreshListener {
    private XJJRecyclerView xjjRecyclerView;
    private XJJBaseRvAdapter adapter;
    private List<Integer> mdatas;

    XJJHeaderView xjjHeaderView;
    XJJFooterView xjjFooterView;
    View header;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcv);

        xjjRecyclerView = (XJJRecyclerView) findViewById(R.id.index_rcv);
        xjjHeaderView = new XJJHeaderView(this, R.layout.view_pull_to_refresh_header, R.id.view_header_img, R.id.view_header_pb, R.id.view_header_tv);
        xjjFooterView = new XJJFooterView(this,R.layout.view_load_more_footer,R.id.view_footer_pb,R.id.view_footer_tv);
        header = LayoutInflater.from(this).inflate(R.layout.view_header, null);

        mdatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mdatas.add(i);
        }
        adapter = new XJJBaseRvAdapter(this, R.layout.item_rcv, mdatas);
        adapter.setItemDataListener(new XJJBaseRvAdapter.ItemDataListener<Integer>() {
            @Override
            public void setItemData(final XJJBaseRvHolder holder, Integer integer) {
                TextView textView = holder.getView(R.id.item_index_tv);
                textView.setText(String.valueOf(integer));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "position" + adapter.getRealPosition(holder), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        xjjRecyclerView.setInit(xjjHeaderView, xjjFooterView,this);
        adapter.addPullToRefreshHeaderView(xjjRecyclerView.getPullToRefreshHeaderView());
        adapter.addHeaderView(header);
        adapter.addLoadMoreFooterView(xjjFooterView.getfooterView());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xjjRecyclerView.setLayoutManager(layoutManager);
        xjjRecyclerView.setAdapter(adapter);
    }

    @Override
    public void xjjLoadMore() {
        if (mdatas.size() > 35) {
            xjjRecyclerView.xjjLoadNoMoreData();
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    mdatas.add(mdatas.get(mdatas.size() - 1) + 1);
                }
                adapter.setDatas(mdatas);
                xjjRecyclerView.xjjLoadMoreComplete();
            }
        }, 3000);
    }

    @Override
    public void xjjRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                xjjRecyclerView.xjjRefreshComplete();
            }
        }, 3000);
    }
}
