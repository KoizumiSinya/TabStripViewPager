package com.example.administrator.demo_tabstripviewpager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.demo_tabstripviewpager.widget.PagerSlidingTabStrip;

/**
 * @Author: Sinya
 * @Editor:
 * @Date: 2015/11/18. 12:07
 * @Update:
 */
public class FragmentOne extends Fragment {

    private int a = 100, b = 99, c = 60;
    private boolean flag;
    private PagerSlidingTabStrip tabStrip;

    @SuppressLint("ValidFragment")
    public FragmentOne(PagerSlidingTabStrip tabStrip) {
        this.tabStrip = tabStrip;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView view = new TextView(getActivity());
        view.setGravity(Gravity.CENTER);
        view.setTextSize(18);
        view.setText("界面");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    tabStrip.setTabTips(new int[]{a--, b--, c--});
                } else {
                    tabStrip.setTabTips(new int[]{0, 0, 0});
                }
                flag = !flag;
            }
        });
        return view;
    }
}
