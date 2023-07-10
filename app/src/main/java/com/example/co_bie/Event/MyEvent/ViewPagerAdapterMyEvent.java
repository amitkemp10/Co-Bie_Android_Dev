package com.example.co_bie.Event.MyEvent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.co_bie.Event.MyEvent.Manage.ManageFragment;
import com.example.co_bie.Event.MyEvent.Participate.ParticipateFragment;

public class ViewPagerAdapterMyEvent extends FragmentStateAdapter {


    public ViewPagerAdapterMyEvent(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ParticipateFragment();
            default:
                return new ManageFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
