package com.example.meld;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.models.SpotifyPlaylist;
import com.example.meld.models.User;
//import com.example.meld.services.SpotifyCallbacks;
import com.example.meld.models.YouTubePlaylist;
import com.example.meld.services.ApiCallTypes;
import com.example.meld.services.NetworkRequest;
import com.example.meld.services.YouTubeCallbacks;
import com.example.meld.services.YouTubeService;
import com.example.meld.ui.home.HomeFragment;
import com.example.meld.utils.JsonPlaylistParser;
import com.example.meld.utils.MapPlaylistParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.example.meld.services.SpotifyService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Handler textHandler = new Handler();

    ICallback fragmentSpotifyCallbacks;
    ICallback fragmentYouTubeCallbacks;

    private SpotifyService spotifyService;
    private YouTubeService youTubeService;
    RequestQueue requestQueue;
    IUser currentUser;

    private String spotifyAccessToken;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //                setup network request queue and services
        requestQueue = Volley.newRequestQueue(this);
        spotifyService = new SpotifyService(new SpotifyCallbacks());
        youTubeService = new YouTubeService(
                getApplicationContext(),
                this,
                new YouTubeCallbacks()
        );

        currentUser = User.getInstance();



        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_playlists, R.id.navigation_friends)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == SpotifyService.AUTH_TOKEN_REQUEST_CODE) {
                AuthorizationResponse response = spotifyService.getResponse(resultCode, requestCode, data);
                if (response != null) {
                    Log.v("gottoken", response.getAccessToken());

                    this.spotifyAccessToken = response.getAccessToken();
//                    getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    spotifyService.setCallType(ApiCallTypes.USER_DATA);

                    currentUser.setSpotifyAccessToken(this.spotifyAccessToken);

                    NetworkRequest.SpotifyApiCall runnableThread =
                            new NetworkRequest.SpotifyApiCall(
                                    spotifyService,
                                    requestQueue,
                                    currentUser
                            );
                    new Thread(runnableThread).start();


//                after token, make userdata request.
//                in callback (Which should prob be in this class), update UI (maybe a toast?),
//                with success method and store data in singleton user

//                getUserDataButton.setVisibility(View.VISIBLE);


                } else {
                    Log.v("TOKENULL", response.getAccessToken());

                }
            }

            if (requestCode == YouTubeService.REQUEST_CODE_SELECT_ACCOUNT) {
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("accountName", accountName);
                        editor.apply();
                        youTubeService.getCredential().setSelectedAccountName(accountName);
//                        youTubeService.fetchUserData();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void test() {
        Log.v("???", "Well");
    }

    public void registerFragmentSpotifyCallback(ICallback callbacks) {
        this.fragmentSpotifyCallbacks = callbacks;
    }

    public void registerFragmentYouTubeCallback(ICallback callbacks) {
        this.fragmentYouTubeCallbacks = callbacks;
    }


    public void authenticateSpotify() {
        spotifyService.authenticateWithSpotifyApp(this);
    }

    public void authenticateYouTube() {
        youTubeService.authenticate();
    }

    public void fetchSpotifyPlaylists() {
        spotifyService.fetchPlaylistList(
                requestQueue,
                spotifyAccessToken,
                currentUser.getSpotifyUsername()
        );
    }

    public void fetchYouTubePlaylists() {
        youTubeService.setCallType(ApiCallTypes.PLAYLISTS);

        NetworkRequest.YouTubeApiCall runnableThread =
                new NetworkRequest.YouTubeApiCall(
                        youTubeService,
                        currentUser
                );

        new Thread(runnableThread).start();





//        youTubeService.fetchPlaylistList();
    }


    public class SpotifyCallbacks implements ICallback {



        // make the playlist button visible here, after user data and user id is fetched
        public void userDataCallback(Object obj, IUser user) {
            JSONObject userDataObj = (JSONObject) obj;
            currentUser.setSpotifyUserDataObject(userDataObj);
            try {
                currentUser.setSpotifyUsername(userDataObj.getString("id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            fragmentSpotifyCallbacks.userDataCallback(obj, currentUser);
//            JSONObject userDataObj = (JSONObject) obj;
//            user.setSpotifyUserDataObject(userDataObj);
//
//            View spotifyButtonView = root.findViewById(R.id.spotify_button_view);
//            spotifyButtonView.setVisibility(View.INVISIBLE);
//            View foundSpotifyTextView = root.findViewById(R.id.found_spotify_view);
//            foundSpotifyTextView.setVisibility(View.VISIBLE);



        }


        public void playlistsCallback(Object obj) {
            JSONObject playlistObj = (JSONObject) obj;
            fragmentSpotifyCallbacks.playlistsCallback(playlistObj);


        }
    }





    public class YouTubeCallbacks implements ICallback {

        //gets a GoogleAccountCredential as obj from youtubeservice

        public void userDataCallback(Object obj, IUser user) {
            GoogleAccountCredential accountCredential = (GoogleAccountCredential) obj;
            currentUser.setGoogleUserObject(accountCredential);
            currentUser.setSelectedGoogleAccountName(accountCredential.getSelectedAccountName());
            fragmentYouTubeCallbacks.userDataCallback(obj, currentUser);
//            GoogleAccountCredential credential = (GoogleAccountCredential)obj;
//            try {
//                //dont get token in main thread, maybe give it back in callback?
//                Log.v("credential", credential.getToken());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            View spotifyButtonView = root.findViewById(R.id.youtube_button_view);
//            spotifyButtonView.setVisibility(View.INVISIBLE);
//            View foundYoutubeText = root.findViewById(R.id.found_youtube_view);
//            foundYoutubeText.setVisibility(View.VISIBLE);


        }

        public void playlistsCallback(Object obj) {
            //            playlistsObjectString = (JSONObject) obj;
//            Map<Playlist, List<PlaylistItem>>
//                    youTubePlaylistMap = (Map<Playlist, List<PlaylistItem>>) obj;

//            List<IPlaylist> res = new ArrayList<>();
//            for (Playlist playlist : youTubePlaylistMap.keySet()) {
//                YouTubePlaylist youTubePlaylist = new YouTubePlaylist();
//
//                youTubePlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
//                youTubePlaylist.setName(playlist.getSnippet().getTitle());
//                youTubePlaylist.setId(playlist.getId());
//                youTubePlaylist.setDescription(playlist.getSnippet().getDescription());
//
//                res.add(youTubePlaylist);

//            }
            //        return res;
            // need a youtube playlist impl
//            Log.v("FROM main", obj.toString());
            fragmentYouTubeCallbacks.playlistsCallback(obj);

        }


    }

    public IUser getCurrentUser() {
        return this.currentUser;
    }


}










