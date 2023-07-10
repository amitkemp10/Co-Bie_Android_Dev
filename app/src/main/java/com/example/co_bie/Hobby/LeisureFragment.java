package com.example.co_bie.Hobby;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.co_bie.R;

import java.util.ArrayList;
import java.util.List;

public class LeisureFragment extends Fragment {

    Context mContext;
    List<Hobby> mLeisureHobbies;
    RecyclerView mRecyclerView;
    HobbiesAdapter mHobbiesAdapter;
    FragmentManager fm;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_leisure, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_leisure);
        mLeisureHobbies = new ArrayList<>();
        mHobbiesAdapter = new HobbiesAdapter(getContext(), mLeisureHobbies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mHobbiesAdapter);
        fm = getActivity().getSupportFragmentManager();
        addNewHobbies();

        return rootView;
    }

    private void addNewHobbies() {
        mLeisureHobbies.add(new Hobby("Cooking", R.drawable.ic_chef));
        mLeisureHobbies.add(new Hobby("Guitar", R.drawable.ic_guitar));
        mLeisureHobbies.add(new Hobby("Dance", R.drawable.ic_dance));
        mLeisureHobbies.add(new Hobby("Movie", R.drawable.ic_movie));
        mLeisureHobbies.add(new Hobby("Shopping", R.drawable.ic_shopping));
        mLeisureHobbies.add(new Hobby("Music", R.drawable.ic_music));
        mLeisureHobbies.add(new Hobby("Alcohol", R.drawable.ic_alcohol));
        mLeisureHobbies.add(new Hobby("Studies", R.drawable.ic_studies));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }
}