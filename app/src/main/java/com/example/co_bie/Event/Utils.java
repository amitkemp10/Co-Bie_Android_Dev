package com.example.co_bie.Event;

import com.example.co_bie.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {

    public enum EventType {
        VIRTUAL,
        PHYSICAL
    }

    public enum Platform { //notice that the platforms are also hard-coded in the CreateEventFragment
        ZOOM,
        TEAMS,
        SKYPE
    }

    public static int appropriateImage(String HobbieName) {
        switch (HobbieName) {
            case "Basketball":
                return R.drawable.ic_basketball;
            case "Ping-Pong":
                return R.drawable.ic_ping_pong;
            case "Tennis":
                return R.drawable.ic_tennis;
            case "Soccer":
                return R.drawable.ic_soccer;
            case "Bowling":
                return R.drawable.ic_bowling;
            case "Football":
                return R.drawable.ic_football;
            case "Volleyball":
                return R.drawable.ic_volleyball;
            case "Baseball":
                return R.drawable.ic_baseball;
            case "Cooking":
                return R.drawable.ic_chef;
            case "Guitar":
                return R.drawable.ic_guitar;
            case "Dance":
                return R.drawable.ic_dance;
            case "Movie":
                return R.drawable.ic_movie;
            case "Shopping":
                return R.drawable.ic_shopping;
            case "Music":
                return R.drawable.ic_music;
            case "Alcohol":
                return R.drawable.ic_alcohol;
            case "Studies":
                return R.drawable.ic_studies;
            case "Chess":
                return R.drawable.ic_chess;
            case "Cards":
                return R.drawable.ic_cards;
            case "Xbox":
                return R.drawable.ic_xbox;
            case "Puzzle":
                return R.drawable.ic_puzzle;
            case "Pokemon":
                return R.drawable.ic_pokemon;
            case "Domino":
                return R.drawable.ic_domino;
            case "Gaming":
                return R.drawable.ic_gaming;
            case "Checkers":
                return R.drawable.ic_checkers;
            default:
                return 0;
        }
    }

    public static int appropriatePlatformImage(String platformType) {
        switch (platformType) {
            case "TEAMS":
                return R.drawable.ic_teams;
            case "SKYPE":
                return R.drawable.ic_skype;
            case "ZOOM":
                return R.drawable.ic_zoom;
            default:
                return R.drawable.ic_gps;
        }
    }
}
