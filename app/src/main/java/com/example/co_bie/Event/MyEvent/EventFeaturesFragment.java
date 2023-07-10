package com.example.co_bie.Event.MyEvent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.co_bie.Event.MyEvent.Manage.ManageFragment;
import com.example.co_bie.R;
import com.google.android.material.tabs.TabLayout;

public class EventFeaturesFragment extends Fragment {

    TabLayout tabLayout;
    View rootView;
    ViewPager2 viewPager2;
    ViewPagerAdapterEventFeature viewPagerAdapterEventFeature;
    Bundle data;

    public EventFeaturesFragment(Bundle data) {
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = new ManageFragment();
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.frame_layout_event_feature, fragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_features, container, false);
        tabLayout = rootView.findViewById(R.id.tab_event_feature);
        viewPager2 = rootView.findViewById(R.id.view_pager_event_feature);
        viewPagerAdapterEventFeature = new ViewPagerAdapterEventFeature(getChildFragmentManager(), getLifecycle(), data, viewPager2);
        viewPager2.setAdapter(viewPagerAdapterEventFeature);
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