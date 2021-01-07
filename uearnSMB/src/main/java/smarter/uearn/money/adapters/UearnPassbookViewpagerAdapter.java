package smarter.uearn.money.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import smarter.uearn.money.fragments.PassbookFragment;
import smarter.uearn.money.models.UearnPassbookInfo;

public class UearnPassbookViewpagerAdapter extends FragmentStatePagerAdapter {

    private List<UearnPassbookInfo> uearnPassbookInfoList;


    public UearnPassbookViewpagerAdapter(FragmentManager fm, List<UearnPassbookInfo> uearnPassbookInfoList) {
        super(fm);
        this.uearnPassbookInfoList = uearnPassbookInfoList;
    }

    @Override
    public Fragment getItem(int position) {
        PassbookFragment passbookFragment = new PassbookFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("pass_info", uearnPassbookInfoList.get(position));
        passbookFragment.setArguments(bundle);
        return passbookFragment;
    }

    @Override
    public int getCount() {
        return uearnPassbookInfoList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String monthEarned = uearnPassbookInfoList.get(position).month + "\n" + uearnPassbookInfoList.get(position).year;
        return monthEarned;
    }
}