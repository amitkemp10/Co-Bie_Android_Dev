package com.example.co_bie.HomePage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.co_bie.Event.Physical.MapPhysicalEventFragment;
import com.example.co_bie.Event.Virtual.VirtualEventFragment;

public class ViewPagerAdapterEventType extends FragmentStateAdapter {


    public ViewPagerAdapterEventType(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new VirtualEventFragment();
            default:
                return new MapPhysicalEventFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
