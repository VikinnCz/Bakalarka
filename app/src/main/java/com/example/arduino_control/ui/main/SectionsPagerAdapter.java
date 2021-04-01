package com.example.arduino_control.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.arduino_control.BtScanFragment;
import com.example.arduino_control.QrScanFragment;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
            Fragment fragment = new Fragment();
            switch (position){
                case 0:

                    fragment = new BtScanFragment();
                    break;
                case 1:
                    fragment = new QrScanFragment();
                    break;
            }
            assert fragment !=null;
            return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Bt scanner";
            case 1:
                return "Qr scanner";
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }


}