package com.example.administrator.demo_tabstripviewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.demo_tabstripviewpager.utils.ToastUtil;
import com.example.administrator.demo_tabstripviewpager.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private Context context;
    private PagerSlidingTabStrip tabStrip;
    private ViewPager viewPager;

    private List<View> views;

    /**
     * 图片资源
     */
    private int icons[];

    /**
     * 文字颜色资源
     */
    private int textSelectors[];
    private String[] titles = {"QQ空间", "新浪微博", "微信"};

    //private String[] titles = {"热点", "国内", "国际", "社会", "理财网", "历史", "体育", "娱乐", "NBA", "音乐", "汽车之家"};
    //private List<Fragment> fragments;

    /**
     * 模拟数据
     */
    private int a = 100, b = 99, c = 9;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        icons = new int[]{R.drawable.tab_strip_icon1_selector, R.drawable.tab_strip_icon2_selector, R.drawable.tab_strip_icon3_selector};
        textSelectors = new int[]{getResources().getColor(R.color.qqzone), getResources().getColor(R.color.sina), getResources().getColor(R.color.wechat)};

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabstrip);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        views = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            TextView text = new TextView(context);
            switch (i) {
                case 0:
                    text.setText("第1页");
                    break;

                case 1:
                    text.setText("点击更换Tab样式");
                    text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (tabStrip.getTabStyle() == 4) {
                                tabStrip.setTabStyle(1);
                            } else {
                                tabStrip.setTabStyle(tabStrip.getTabStyle() + 1);
                            }
                        }
                    });
                    break;

                case 2:
                    text.setText("点击显示小圆点提醒数");
                    text.setOnClickListener(new View.OnClickListener() {
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

                    break;
                default:
                    break;
            }

            text.setGravity(Gravity.CENTER);
            text.setTextSize(18);
            views.add(text);
        }

        /*fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            fragments.add(new FragmentOne(tabStrip));
        }
        MyFragmentAdapter adaper = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adaper);
        tabStrip.setViewPager(viewPager);
        tabStrip.setTabTips(new int[]{2, 0, 5});*/

        MyPagerAdaper adaper = new MyPagerAdaper(views);
        viewPager.setAdapter(adaper);
        tabStrip.setViewPager(viewPager);
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ToastUtil.showShortToast(context, titles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // [+] 内部类

    class MyPagerAdaper extends PagerAdapter implements PagerSlidingTabStrip.TabIconResInterface, PagerSlidingTabStrip.TabTextColorResInterface {

        private List<View> views;

        public MyPagerAdaper(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position % titles.length];
        }

        @Override
        public int getPageSelectedIconResId(int position) {
            return icons[position % icons.length];
        }

        @Override
        public int getTextSelectColor(int position) {
            return textSelectors[position % textSelectors.length];
        }
    }


   /* class MyFragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> list;

        public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }


        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position % titles.length];
        }
    }*/

    // [-] 内部类
}
