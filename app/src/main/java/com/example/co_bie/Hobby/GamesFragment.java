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

public class GamesFragment extends Fragment {

    Context mContext;
    List<Hobby> mGamesHobbies;
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
        rootView = inflater.inflate(R.layout.fragment_games, container, false);

        mRecyclerView = rootView.findViewById(R.id.rv_games);
        mGamesHobbies = new ArrayList<>();
        mHobbiesAdapter = new HobbiesAdapter(getContext(),mGamesHobbies);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(mHobbiesAdapter);
        fm = getActivity().getSupportFragmentManager();
        addNewHobbies();

        return rootView;
    }

    private void addNewHobbies() {
        mGamesHobbies.add(new Hobby("Chess",R.drawable.ic_chess));
        mGamesHobbies.add(new Hobby("Cards",R.drawable.ic_cards));
        mGamesHobbies.add(new Hobby("Xbox",R.drawable.ic_xbox));
        mGamesHobbies.add(new Hobby("Puzzle",R.drawable.ic_puzzle));
        mGamesHobbies.add(new Hobby("Pokemon",R.drawable.ic_pokemon));
        mGamesHobbies.add(new Hobby("Domino",R.drawable.ic_domino));
        mGamesHobbies.add(new Hobby("Gaming",R.drawable.ic_gaming));
        mGamesHobbies.add(new Hobby("Checkers",R.drawable.ic_checkers));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }
}