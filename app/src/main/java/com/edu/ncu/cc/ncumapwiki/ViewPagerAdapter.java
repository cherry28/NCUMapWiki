package com.edu.ncu.cc.ncumapwiki;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public static boolean fabVisible;
    Context context;
    FloatingActionButton fab;
    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs,FloatingActionButton fab, Context mContext) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.fab = fab;
        this.context = mContext;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DefaultCategory();
            case 1:
                return new CustomizedCategory();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title="0";
        switch(position){
            case 0: {
                title=context.getResources().getString(R.string.category_default);
                return title;
            }
            case 1: {
                title=context.getResources().getString(R.string.category_customized);
                return title;
            }
        }
        return title;
    }
}