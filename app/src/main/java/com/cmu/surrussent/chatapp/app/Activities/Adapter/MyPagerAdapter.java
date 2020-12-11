package com.cmu.surrussent.chatapp.app.Activities.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.cmu.surrussent.chatapp.app.Activities.Fragments.AnotherLovesFragment;
import com.cmu.surrussent.chatapp.app.Activities.Fragments.AnthorPostsFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public int getCount() {
        return 2;
    }

    public Fragment getItem(int position) {
        if(position == 0)
            return new AnthorPostsFragment();
        else if(position == 1)
            return new AnotherLovesFragment();
        return null;
    }
}
