package com.example.administrator.demo_tabstripviewpager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.demo_tabstripviewpager.R;
import com.jauker.widget.BadgeView;

import java.util.Locale;

/**
 *
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    /**
     * tab 的个数
     */
    private int tabCount;

    /**
     * tab 显示的提醒数
     */
    private int[] tabTips;

    /**
     * 指示条颜色
     */
    private int indicatorColor = 0xFF666666;

    /**
     * 分隔横线 颜色
     */
    private int underlineColor = 0x1A000000;

    /**
     * 分隔竖线颜色
     */
    private int dividerColor = 0x1A000000;

    /**
     * 是否使用权重
     */
    private boolean shouldExpand = false;

    /**
     * 文本是否为大写
     */
    private boolean textAllCaps = true;

    /**
     * 指示条 移动量
     */
    private int scrollOffset = 52;

    /**
     * 指示条 高度
     */
    private int indicatorHeight = 8;

    /**
     * 底部分隔横线 高度
     */
    private int underlineHeight = 2;

    /**
     * Item之间的分隔竖线
     */
    private int dividerWidth = 1;

    /**
     * 分隔竖线和Item之间的间距
     */
    private int dividerPadding = 12;

    /**
     * Item之间的间距
     */
    private int tabPadding = 24;

    /**
     * 文本 大小
     */
    private int tabTextSize = 12;

    /**
     * 文本字体样式
     * 默认0 正常
     * 1 是粗体
     */
    private int tabTextStyle = 0;

    /**
     * 文本 未选中颜色
     */
    private int tabTextColor = 0xFF666666;

    /**
     * 文本 选中时颜色
     */
    private int selectedTabTextColor = 0xFF666666;

    /**
     * Item 背景颜色
     */
    private int tabBackgroundResId = R.drawable.bg_tab_strip;

    /**
     * 图片和文本的间距
     */
    private int tabIconPadding = 0;

    /**
     * 图片的宽度
     */
    private int tabIconWidth = 0;

    /**
     * 图片的高度
     */
    private int tabIconHeight = 0;

    /**
     * 文字距离底部分隔横线的间距
     */
    private int tabTextPaddingButtom = 10;

    /**
     * tab样式
     * 1  仅显示文本
     * 2 仅显示图片
     * 3 图片在左 文本在右
     * 4 图片在上 文本在下
     */
    private int tabStyle = 1;

    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private Locale locale;


    private TabClickListener tabClickListener;
    private TabTextColorResInterface textSelector;

    // [+] 构造方法

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        tabIconPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabIconPadding, dm);
        tabIconWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabIconWidth, dm);
        tabIconHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabIconHeight, dm);
        tabTextPaddingButtom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabTextPaddingButtom, dm);

        //取得attrs属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        //Tab文本大小
        tabTextSize = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabTextSize, tabTextSize);
        //Tab文本颜色
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabTextColor, indicatorColor);
        //Tab文本选中时颜色
        selectedTabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabTextSelectedColor, indicatorColor);
        //Tab指示条颜色
        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabIndicatorColor, indicatorColor);
        //分隔线颜色
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabUnderlineColor, underlineColor);
        //Item之间的分隔线颜色
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabDividerColor, dividerColor);
        //指示条的高度
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabIndicatorHeight, indicatorHeight);
        //分隔线的高度
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabUnderlineHeight, underlineHeight);
        //Item之间的分隔线和Item间距
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabDividerPadding, dividerPadding);
        //Item之间的间距
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabItemPaddingLeftRight, tabPadding);
        //Item的背景颜色
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_tabItemBackground, tabBackgroundResId);
        //是否使用权重
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_tabShouldExpand, shouldExpand);
        //移动偏移量
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabIndicatorScrollOffset, scrollOffset);
        //是否文本是大写
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_tabTextAllCaps, textAllCaps);

        //tab 样式
        tabStyle = a.getInt(R.styleable.PagerSlidingTabStrip_tabStyle, tabStyle);
        //Icon宽
        tabIconWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabIconWidth, tabIconWidth);
        //Icon高
        tabIconHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabIconHeight, tabIconHeight);
        //Icon 和 文本间距
        tabIconPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabIconToTextPadding, tabIconPadding);
        //文本距离底下分隔线的间距
        tabTextPaddingButtom = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabTextPaddingButtom, tabTextPaddingButtom);
        //文本字体的样式 默认0正常； 1是粗体
        tabTextStyle= a.getInt(R.styleable.PagerSlidingTabStrip_tabTextStyle, tabTextStyle);

        a.recycle();

        //设置指示条的画笔
        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        //设置分隔线的画笔
        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    // [-] 构造方法

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            switch (tabStyle) {
                //tab 只有文本
                case 1:
                    addTextTabItem(i, pager.getAdapter().getPageTitle(i).toString());
                    break;

                //tab 只有Icon
                case 2:
                    addIconTabItem(i, ((TabIconResInterface) pager.getAdapter()).getPageSelectedIconResId(i));
                    break;

                //tab Icon在左 文本在右
                case 3:
                    addIconLeftToTextItem(i, pager.getAdapter().getPageTitle(i).toString(), ((TabIconResInterface) pager.getAdapter()).getPageSelectedIconResId(i));
                    break;

                //tab Icon在上 文本在下
                case 4:
                    addIconAboveTextItem(i, pager.getAdapter().getPageTitle(i).toString(), ((TabIconResInterface) pager.getAdapter()).getPageSelectedIconResId(i));
                    break;
                default:
                    break;
            }
        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    public void setTabTips(int[] tips) {
        this.tabTips = tips;
        notifyDataSetChanged();

    }

    /**
     * 添加仅是文字类型的子项
     *
     * @param position
     * @param title
     */
    private void addTextTabItem(final int position, String title) {

        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tv.setPadding(0, tabPadding, 0, tabPadding);

        addTabView(position, tv);
    }

    /**
     * 添加仅是图片类型的子项
     *
     * @param position
     * @param resId
     */
    private void addIconTabItem(final int position, int resId) {

        ImageButton image = new ImageButton(getContext());
        image.setImageResource(resId);

        addTabView(position, image);
    }

    /**
     * 添加图片在文字左边的类型的子项
     *
     * @param position
     * @param title
     * @param resId
     */
    private void addIconLeftToTextItem(final int position, String title, int resId) {
        LinearLayout layout = new LinearLayout(getContext());

        layout.setHorizontalGravity(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        ImageView image = new ImageView(getContext());
        image.setPadding(0, 0, px2dip(getContext(), tabIconPadding), 0);
        image.setImageResource(resId);
        layout.addView(image);

        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        layout.addView(tv);

        addTabView(position, layout);
    }


    /**
     * 添加图片在文字上方的类型的子项
     *
     * @param position
     * @param title
     * @param resId
     */
    private void addIconAboveTextItem(final int position, String title, int resId) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        ImageView image = new ImageView(getContext());
        image.setPadding(0, 0, 0, px2dip(getContext(), tabIconPadding));
        image.setImageResource(resId);
        layout.addView(image);

        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        tv.setPadding(0, 0, 0, tabTextPaddingButtom);
        layout.addView(tv);

        addTabView(position, layout);
    }


    /**
     * 把创建好的子项加入Tab布局中
     *
     * @param position
     * @param tab
     */
    private void addTabView(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabClickListener != null) {
                    tabClickListener.tabClick(position);
                }
                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    /**
     * 刷新tab显示（包括tips提醒数、图片和文字的选中效果）
     */
    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);
            v.setBackgroundResource(tabBackgroundResId);

            switch (tabStyle) {
                case 1:
                    setTabTextSelector(v, i);
                    break;

                case 2:
                    setTabIconSelector(v, i);
                    break;

                case 3:
                    setTabIconLeftOfTextSelector(v, i);
                    break;

                case 4:
                    setTabIconAboveOfTextSelector(v, i);
                    break;

                default:
                    break;
            }

        }
    }

    /**
     * 刷新 仅有文字类型tab
     *
     * @param v
     * @param i
     */
    private void setTabTextSelector(View v, int i) {
        if (v instanceof TextView) {
            TextView tv = (TextView) v;

            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
            tv.setTypeface(tabTypeface, tabTypefaceStyle);
            tv.setTextColor(tabTextColor);
            if (tabTextStyle == 1) {
                TextPaint tp = tv.getPaint();
                tp.setFakeBoldText(true);
            }

            if (tabTips == null) {
                tabTips = new int[tabCount];
            }

            if (i <= tabTips.length - 1) {
                //Sinya 计算宽度，标记提醒小圆点的最佳位置
                int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                tv.measure(width, height);

                BadgeView badgeView = new BadgeView(getContext());
                badgeView.setBadgeGravity(Gravity.CENTER);
                badgeView.measure(width, height);
                badgeView.setTargetView(tv);
                badgeView.setBadgeMargin(px2dip(getContext(), tv.getMeasuredWidth() / 2 + badgeView.getMeasuredWidth() / 3 * 2 - tabPadding), 0, 0, 0);

                if (tabTips[i] > 99) {
                    badgeView.setText("99+");
                } else if (tabTips[i] > 0) {
                    badgeView.setBadgeCount(tabTips[i]);
                }
            }

            // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
            // pre-ICS-build
            if (textAllCaps) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tv.setAllCaps(true);
                } else {
                    tv.setText(tv.getText().toString().toUpperCase(locale));
                }
            }

            if (i == selectedPosition) {
                tv.setTextColor(selectedTabTextColor);
            }

            //Sinya-更新tab文字选中颜色
        } else if (v instanceof FrameLayout) {
            TextView tv = (TextView) ((FrameLayout) v).getChildAt(0);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
            tv.setTypeface(tabTypeface, tabTypefaceStyle);
            tv.setTextColor(tabTextColor);
            if (i == selectedPosition) {
                tv.setTextColor(selectedTabTextColor);
            }
        }
    }

    /**
     * 刷新仅有图片类型的tab
     *
     * @param v
     * @param i
     */
    private void setTabIconSelector(View v, int i) {
        if (v instanceof ImageButton) {
            ImageButton image = (ImageButton) v;

            image.setFocusable(false);
            image.setSelected(false);
            image.setPressed(false);

            if (tabTips == null) {
                tabTips = new int[tabCount];
            }

            if (i <= tabTips.length - 1) {
                //Sinya 计算宽度，标记提醒小圆点的最佳位置
                int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                image.measure(width, height);

                BadgeView badgeView = new BadgeView(getContext());
                badgeView.setBadgeGravity(Gravity.CENTER);
                badgeView.measure(width, height);
                badgeView.setTargetView(image);
                badgeView.setBadgeMargin(px2dip(getContext(), image.getMeasuredWidth() / 2 + badgeView.getMeasuredWidth() / 6 - tabPadding), 0, 0, px2dip(getContext(), badgeView.getMeasuredHeight() / 2));

                if (tabTips[i] > 99) {
                    badgeView.setText("99+");
                } else if (tabTips[i] > 0) {
                    badgeView.setBadgeCount(tabTips[i]);
                }
            }

            if (i == selectedPosition) {
                image.setSelected(true);
            }

            //Sinya-更新tab文字选中颜色
        } else if (v instanceof FrameLayout) {
            ImageButton image = (ImageButton) ((FrameLayout) v).getChildAt(0);
            image.setFocusable(false);
            image.setSelected(false);
            image.setPressed(false);

            if (i == selectedPosition) {
                image.setSelected(true);
            }
        }
    }

    /**
     * 刷新图片在文字左边的类型的tab
     *
     * @param view
     * @param i
     */
    private void setTabIconLeftOfTextSelector(View view, int i) {
        if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;

            View child = layout.getChildAt(0);

            if (child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                imageView.setFocusable(false);
                imageView.setSelected(false);
                imageView.setPressed(false);

                if (i == selectedPosition) {
                    imageView.setSelected(true);
                }
            }

            View frame = layout.getChildAt(1);

            if (frame instanceof TextView) {
                TextView tv = (TextView) frame;

                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tv.setTypeface(tabTypeface, tabTypefaceStyle);
                tv.setTextColor(tabTextColor);
                if (tabTextStyle == 1) {
                    TextPaint tp = tv.getPaint();
                    tp.setFakeBoldText(true);
                }

                if (tabTips == null) {
                    tabTips = new int[tabCount];
                }

                if (i <= tabTips.length - 1) {
                    //Sinya 计算宽度，标记提醒小圆点的最佳位置
                    int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    tv.measure(width, height);

                    BadgeView badgeView = new BadgeView(getContext());
                    badgeView.setBadgeGravity(Gravity.CENTER);
                    badgeView.measure(width, height);
                    badgeView.setTargetView(tv);
                    badgeView.setBadgeMargin(px2dip(getContext(), (tv.getMeasuredWidth() / 2 + badgeView.getMeasuredWidth() / 3 * 2) - tabPadding - badgeView.getMeasuredWidth() / 2), 0, 0, 0);
                    tv.setPadding(0, 0, px2dip(getContext(), badgeView.getMeasuredWidth() * 4), 0);
                    if (tabTips[i] > 99) {
                        badgeView.setText("99+");
                    } else if (tabTips[i] > 0) {
                        badgeView.setBadgeCount(tabTips[i]);
                    }
                }

                if (i == selectedPosition) {
                    if (pager.getAdapter() instanceof TabTextColorResInterface) {
                        tv.setTextColor(((TabTextColorResInterface) pager.getAdapter()).getTextSelectColor(i));
                    } else {
                        tv.setTextColor(selectedTabTextColor);
                    }
                }

                //Sinya-更新tab文字选中颜色
            } else if (frame instanceof FrameLayout) {
                TextView tv = (TextView) ((FrameLayout) frame).getChildAt(0);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tv.setTypeface(tabTypeface, tabTypefaceStyle);
                tv.setTextColor(tabTextColor);
                if (i == selectedPosition) {
                    if (pager.getAdapter() instanceof TabTextColorResInterface) {
                        tv.setTextColor(((TabTextColorResInterface) pager.getAdapter()).getTextSelectColor(i));
                    } else {
                        tv.setTextColor(selectedTabTextColor);
                    }
                }
            }
        }
    }

    /**
     * 刷新图片在文字上面的类型的tab
     *
     * @param view
     * @param i
     */
    private void setTabIconAboveOfTextSelector(View view, int i) {
        if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;

            View child = layout.getChildAt(0);

            if (child instanceof ImageView) {
                ImageView imageView = (ImageView) child;
                imageView.setFocusable(false);
                imageView.setSelected(false);
                imageView.setPressed(false);

                if (i == selectedPosition) {
                    imageView.setSelected(true);
                }
            }

            View frame = layout.getChildAt(1);

            if (frame instanceof TextView) {
                TextView tv = (TextView) frame;

                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tv.setTypeface(tabTypeface, tabTypefaceStyle);
                tv.setTextColor(tabTextColor);
                if (tabTextStyle == 1) {
                    TextPaint tp = tv.getPaint();
                    tp.setFakeBoldText(true);
                }

                if (tabTips == null) {
                    tabTips = new int[tabCount];
                }

                if (i <= tabTips.length - 1) {
                    //Sinya 计算宽度，标记提醒小圆点的最佳位置
                    int width = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    tv.measure(width, height);

                    BadgeView badgeView = new BadgeView(getContext());
                    badgeView.setBadgeGravity(Gravity.CENTER);
                    badgeView.measure(width, height);
                    badgeView.setTargetView(tv);
                    badgeView.setBadgeMargin(px2dip(getContext(), tv.getMeasuredWidth() / 2 + badgeView.getMeasuredWidth() / 3 * 2/* - tabPadding*/), 0, 0, 0);

                    if (tabTips[i] > 99) {
                        badgeView.setText("99+");
                    } else if (tabTips[i] > 0) {
                        badgeView.setBadgeCount(tabTips[i]);
                    }
                }

                if (i == selectedPosition) {
                    if (pager.getAdapter() instanceof TabTextColorResInterface) {
                        tv.setTextColor(((TabTextColorResInterface) pager.getAdapter()).getTextSelectColor(i));
                    } else {
                        tv.setTextColor(selectedTabTextColor);
                    }
                }

                //Sinya-更新tab文字选中颜色
            } else if (frame instanceof FrameLayout) {
                TextView tv = (TextView) ((FrameLayout) frame).getChildAt(0);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tv.setTypeface(tabTypeface, tabTypefaceStyle);
                tv.setTextColor(tabTextColor);

                if (i == selectedPosition) {
                    if (pager.getAdapter() instanceof TabTextColorResInterface) {
                        tv.setTextColor(((TabTextColorResInterface) pager.getAdapter()).getTextSelectColor(i));
                    } else {
                        tv.setTextColor(selectedTabTextColor);
                    }
                }
            }
        }
    }

    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        //绘制分隔横线
        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // 绘制分隔竖线
        rectPaint.setColor(indicatorColor);

        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        //计算指示条的偏移量
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

        //绘制指示条
        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    // [+] get set Methods

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;

        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);

        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        notifyDataSetChanged();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        updateTabStyles();
    }

    public void setSelectedTextColorResource(int resId) {
        this.selectedTabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getSelectedTextColor() {
        return selectedTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    public int getTabIconPadding() {
        return tabIconPadding;
    }

    public void setTabIconPadding(int tabIconPadding) {
        this.tabIconPadding = tabIconPadding;
        updateTabStyles();
    }

    public int getTabIconWidth() {
        return tabIconWidth;
    }

    public void setTabIconWidth(int tabIconWidth) {
        this.tabIconWidth = tabIconWidth;
        updateTabStyles();
    }

    public int getTabIconHeight() {
        return tabIconHeight;
    }

    public void setTabIconHeight(int tabIconHeight) {
        this.tabIconHeight = tabIconHeight;
        updateTabStyles();
    }

    public int getTabTextPaddingButtom() {
        return tabTextPaddingButtom;
    }

    public void setTabTextPaddingButtom(int tabTextPaddingButtom) {
        this.tabTextPaddingButtom = tabTextPaddingButtom;
        updateTabStyles();
    }

    public int getTabStyle() {
        return tabStyle;
    }

    public void setTabStyle(int tabStyle) {
        this.tabStyle = tabStyle;
        invalidate();
        notifyDataSetChanged();
    }

//    public int getTabTextStyle() {
//        return tabTextStyle;
//    }
//
//    public void setTabTextStyle(int tabTextStyle) {
//        this.tabTextStyle = tabTextStyle;
//        updateTabStyles();
//    }

    // [-] get set Methods
    // [+] Override

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    // [-] Override
    // [+] 内部类

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            updateTabStyles();
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    // [-] 内部类
    // [+] 内部接口

    /**
     * 设置Icon图标资源的接口
     */
    public interface TabIconResInterface {
        public int getPageSelectedIconResId(int position);
    }

    /**
     * 设置TextColor资源的接口
     * 针对每个选项卡选中时候，有各自的颜色时候 使用
     */
    public interface TabTextColorResInterface {
        public int getTextSelectColor(int position);
    }

    public void setTextSelector(TabTextColorResInterface implement) {
        this.textSelector = implement;
    }

    /**
     * tabItem点击的回调接口
     */
    public interface TabClickListener {
        public void tabClick(int position);
    }

    public void setTabClickListener(TabClickListener listener) {
        this.tabClickListener = listener;
    }

    // [-] 内部接口


}
