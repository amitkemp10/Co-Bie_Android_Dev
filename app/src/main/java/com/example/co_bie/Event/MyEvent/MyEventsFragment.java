package com.example.co_bie.Event.MyEvent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.co_bie.Event.MyEvent.Manage.ManageFragment;
import com.example.co_bie.R;
import com.google.android.material.tabs.TabLayout;

public class MyEventsFragment extends Fragment {

    TabLayout tabLayout;
    View rootView;
    ViewPager2 viewPager2;
    ViewPagerAdapterMyEvent viewPagerAdapterMyEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = new ManageFragment();
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.frame_layout_my_events, fragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        tabLayout = rootView.findViewById(R.id.tab_my_event);
        viewPager2 = rootView.findViewById(R.id.view_pager_my_event);
        viewPagerAdapterMyEvent = new ViewPagerAdapterMyEvent(getChildFragmentManager(), getLifecycle());
        viewPager2.setAdapter(viewPagerAdapterMyEvent);
        viewPager2.setUserInputEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        return rootView;
    }
}