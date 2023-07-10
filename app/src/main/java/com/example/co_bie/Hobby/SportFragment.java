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

public class SportFragment extends Fragment {

    Context mContext;
    List<Hobby> mSportHobbies;
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
        rootView = inflater.inflate(R.layout.fragment_sport, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_sport);
        mSportHobbies = new ArrayList<>();
        mHobbiesAdapter = new HobbiesAdapter(getContext(),mSportHobbies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(mHobbiesAdapter);
        fm = getActivity().getSupportFragmentManager();
        addNewHobbies();

        return rootView;
    }

    private void addNewHobbies() {
        mSportHobbies.add(new Hobby("Basketball",R.drawable.ic_basketball));
        mSportHobbies.add(new Hobby("Ping-Pong",R.drawable.ic_ping_pong));
        mSportHobbies.add(new Hobby("Tennis",R.drawable.ic_tennis));
        mSportHobbies.add(new Hobby("Soccer",R.drawable.ic_soccer));
        mSportHobbies.add(new Hobby("Bowling",R.drawable.ic_bowling));
        mSportHobbies.add(new Hobby("Football",R.drawable.ic_football));
        mSportHobbies.add(new Hobby("Volleyball",R.drawable.ic_volleyball));
        mSportHobbies.add(new Hobby("Baseball",R.drawable.ic_baseball));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }
}