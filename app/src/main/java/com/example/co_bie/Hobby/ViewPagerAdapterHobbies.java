package com.example.co_bie.Hobby;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterHobbies extends FragmentStateAdapter {

    public ViewPagerAdapterHobbies(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new LeisureFragment();
            case 2:
                return new GamesFragment();
            default:
                return new SportFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
