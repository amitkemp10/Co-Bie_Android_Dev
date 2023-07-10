package com.example.co_bie.Notification;

import android.app.DownloadManager;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.co_bie.Event.Event;
import com.example.co_bie.LoginAndRegistration.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSend {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "AAAARTPCCHs:APA91bFwRHyLxpndXX8hZ5xtW1SZXOGBcuZwU_IhsWasQ689i0FHgB5HTTWwoA-ux9a3IG6-c63fufAlTvYqMzKtWGEHXyngGpXS779soeE59MqsYrKra_19VX5Qg-QbbIH6tn5TszTs";

    public static void pushNotifications(Context context, String token, String title, String message, String type, Event event, String virtual_physical, User user) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("message", message);
            data.put("type", type);
            data.put("virtual_physical", virtual_physical);
            Gson gsonEvent = new Gson();
            Gson gsonUser = new Gson();
            String eventJson = gsonEvent.toJson(event);
            data.put("event", eventJson);
            String userJson = gsonUser.toJson(user);
            data.put("user", userJson);
            json.put("data", data);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "key=" + SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
