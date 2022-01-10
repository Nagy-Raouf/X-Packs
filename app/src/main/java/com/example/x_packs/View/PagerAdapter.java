package com.example.x_packs.View;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;

    public PagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragmentList = new ArrayList<Fragment>();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    public void finishFragments(){
        for(int i = 0;i<fragmentList.size();i++){
            fragmentList.get(i).onDestroy();
        }
    }
}