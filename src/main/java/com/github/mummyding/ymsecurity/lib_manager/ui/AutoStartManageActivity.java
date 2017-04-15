package com.github.mummyding.ymsecurity.lib_manager.ui;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.mummyding.ymbase.base.BaseSwipeBackActivity;
import com.github.mummyding.ymbase.util.SystemBarTintManager;
import com.github.mummyding.ymbase.util.UIElementsHelper;
import com.github.mummyding.ymbase.view.SlidingTab;
import com.github.mummyding.ymsecurity.lib_manager.R;
import com.github.mummyding.ymsecurity.lib_manager.fragment.AutoStartFragment;
import com.github.mummyding.ymsecurity.lib_manager.fragment.WeakFragmentPagerAdapter;


public class AutoStartManageActivity extends BaseSwipeBackActivity {

    private Resources mResourse;
    private SlidingTab mTabs;
    private ViewPager mViewPager;
    private AutoStartPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autostart_manage);
        initView();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setLogo(getResources().getDrawable(R.drawable.transparent));
//          applyKitKatTranslucency();

        mResourse = getResources();
        mPagerAdapter = new AutoStartPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPagerAdapter);

        int pageMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        mTabs.setViewPager(mViewPager);
        setTabsValue();
    }

    private void initView() {
        mTabs = (SlidingTab) bindView(R.id.tabs);
        mViewPager = (ViewPager) bindView(R.id.pagerFragmentTask);
    }

    private View bindView(int id) {
        return findViewById(id);
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 设置Tab是自动填充满屏幕的
        mTabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        mTabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3, dm));
        // 设置Tab标题文字的大小
        mTabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        mTabs.setTextColor(Color.parseColor("#ffffff"));
        mTabs.setIndicatorColor(Color.parseColor("#ffffff"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        mTabs.setSelectedTextColor(Color.parseColor("#ffffff"));
        // 取消点击Tab时的背景色
        mTabs.setTabBackground(0);

    }


    /**
     * Apply KitKat specific translucency.
     */

    private void applyKitKatTranslucency() {

        // KitKat translucent navigation/status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            // mTintManager.setTintColor(0xF00099CC);

            mTintManager.setTintDrawable(UIElementsHelper
                    .getGeneralActionBarBackground(this));

            getActionBar().setBackgroundDrawable(
                    UIElementsHelper.getGeneralActionBarBackground(this));

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class AutoStartPagerAdapter extends WeakFragmentPagerAdapter {

        private final String[] TITLES = {"普通应用", "系统核心应用"};

        public AutoStartPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            AutoStartFragment fragment = new AutoStartFragment();
            Bundle bundle = new Bundle();

            bundle.putInt("position", position);
            fragment.setArguments(bundle);
            saveFragment(fragment);

            return fragment;
        }
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
