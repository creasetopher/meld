package com.example.meld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.models.SpotifyPlaylist;
//import com.example.meld.models.SpotifyUser;
import com.example.meld.models.User;
import com.example.meld.services.SpotifyService;
import com.example.meld.ui.playlists.PlaylistFragment;
import com.example.meld.utils.JsonPlaylistParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsActivity extends AppCompatActivity {

    public String userDataObject;
    public String playlistsObjectString;

    String accessToken;
    IUser user;
    public static ArrayAdapter<String> listAdapter;
    public static List<IPlaylist> allPlaylists = new ArrayList<>();
    ListView listView;
    private SpotifyService spotifyService;
    RequestQueue requestQueue;

    private Handler textHandler = new Handler();


    @Override
    protected void onStart() {
        final Activity theActivity = this;
        super.onStart();
        User user = User.getInstance();
//        user.setSpotifyAccessToken("!!!!");
        Log.v("user", user.getSpotifyAccessToken());
        String accountName = this.getPreferences(Context.MODE_PRIVATE)
                .getString("accountName", null);
        Log.v("user", accountName);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        listAdapter = new ArrayAdapter(
                PlaylistsActivity.this,
                android.R.layout.simple_list_item_1,
                allPlaylists);

        listView = (ListView) findViewById(R.id.playlists_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaylist element = (IPlaylist) listView.getItemAtPosition(position);
            }
        });


//        spotifyService = new SpotifyService(new SpotifyCallbacks());

        requestQueue = Volley.newRequestQueue(this);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
//        this.userDataObject = getIntent().getStringExtra("userDataObject");
//        this.accessToken = getIntent().getStringExtra("accessToken");
////        this.user = gson.fromJson(getIntent().getStringExtra("userDataObject"), SpotifyUser.class);
//        this.playlistsObjectString = getIntent().getStringExtra("playlists");


//        Log.v("fromplayact:99", this.playlistsObjectString);


        try {
            ArrayList<IPlaylist> temp = new ArrayList<>();
            JSONObject playlistsObjectJson = new JSONObject(playlistsObjectString);

            allPlaylists.clear();
//            allPlaylists.addAll(JsonPlaylistParser.toJsonObjectArray(playlistsObjectJson));
            listAdapter.notifyDataSetChanged();

        }

        catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed(){
        finish();
    }

//    class RunnableThread2 implements Runnable {
//        @Override
//        public void run() {
//
//            try {
//                textHandler.post(() -> Log.v("trying", "playlists"));
//
//                spotifyService.fetchPlaylists(requestQueue, accessToken, user.getId());
//
//            } catch (NullPointerException e) {
//                textHandler.post(() -> Log.v("error", e.getMessage()));
//
//            }
//
//        }
//    }


}

//2020-07-29 19:10:02.006 7275-7275/com.example.meld V/USERDATA!: {"display_name":"ittstoeknee","external_urls":{"spotify":"https:\/\/open.spotify.com\/user\/ittstoeknee"},"followers":{"href":null,"total":2},"href":"https:\/\/api.spotify.com\/v1\/users\/ittstoeknee","id":"ittstoeknee","images":[],"type":"user","uri":"spotify:user:ittstoeknee"}
