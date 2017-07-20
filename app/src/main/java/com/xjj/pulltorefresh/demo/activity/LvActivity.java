package com.xjj.pulltorefresh.demo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xjj.pulltorefresh.demo.R;
import com.xjj.pulltorefresh.demo.adapter.XMyAdapter;
import com.xjj.pulltorefresh.demo.views.XMyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 晨兮_夏哥 on 2016/11/28.
 * CSDN：http://blog.csdn.net/gsw333
 */
public class LvActivity extends AppCompatActivity {

    private XMyListView xMyListView;

    private XMyAdapter adapter;

    private TextView title;
    private EditText edt;
    private boolean isCanEdit = true;
    private List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lv);

        adapter = new XMyAdapter(this);
        xMyListView = (XMyListView) findViewById(R.id.main_listview);
        xMyListView.setInit(R.layout.view_pull_to_refresh_header,R.id.view_header_img,R.id.view_header_pb,R.id.view_header_tv);
        xMyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "position" + position, Toast.LENGTH_SHORT).show();
            }
        });
        xMyListView.setAdapter(adapter);

        for (int i = 0; i < 15; i++) {
            data.add("item" + i);
        }
        adapter.setData(data);
    }

}
