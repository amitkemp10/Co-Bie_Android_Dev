package com.example.co_bie.Event.MyEvent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.co_bie.Event.MyEvent.EventReviewAdapter;
import com.example.co_bie.Event.MyEvent.Manage.ManageFragment;
import com.example.co_bie.Event.MyEvent.Participate.ParticipateFragment;
import com.example.co_bie.R;

public class ViewPagerAdapterEventFeature extends FragmentStateAdapter {

    private Bundle data;
    private ViewPager2 viewPager2;


    public ViewPagerAdapterEventFeature(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Bundle data, ViewPager2 viewPager2) {
        super(fragmentManager, lifecycle);
        this.data = data;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                if (viewPager2.getContext() instanceof EventActivity) {
                    EventActivity activity = (EventActivity) viewPager2.getContext();
                    activity.findViewById(R.id.lay_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_sec_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_third_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_forth_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_five_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.activity_event).setBackground(null);
                }
                EventGroupChatFeatureFragment groupChatFragment = new EventGroupChatFeatureFragment(data);
                return groupChatFragment;

            case 0:
                if (viewPager2.getContext() instanceof EventActivity) {
                    EventActivity activity = (EventActivity) viewPager2.getContext();
                    activity.findViewById(R.id.lay_title).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.lay_sec_title).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.lay_third_title).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.lay_forth_title).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.lay_five_title).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.activity_event).setBackground(activity.getResources().getDrawable(R.drawable.main_bg));
                }
                EventUsersFeatureFragment usersFragment = new EventUsersFeatureFragment(data);
                return usersFragment;

            default:
                if (viewPager2.getContext() instanceof EventActivity) {
                    EventActivity activity = (EventActivity) viewPager2.getContext();
                    activity.findViewById(R.id.lay_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_sec_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_third_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_forth_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.lay_five_title).setVisibility(View.GONE);
                    activity.findViewById(R.id.activity_event).setBackground(null);
                }
                EventReviewFragment eventReviewFragment = new EventReviewFragment(data);
                return eventReviewFragment;

        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
