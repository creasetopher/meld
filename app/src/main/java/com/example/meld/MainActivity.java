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
import android.widget.Toast;

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
//import com.example.meld.services.YouTubeCallbacks;
import com.example.meld.services.YouTubeService;
import com.example.meld.ui.home.HomeFragment;
import com.example.meld.utils.JsonPlaylistParser;
import com.example.meld.utils.MapPlaylistParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.spotify.sdk.android.auth.AuthorizationResponse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private IPlaylist tappedPlaylist = null;
    private String tappedFriend;
    private Boolean playlistArrayIsLocked = false;



    private Handler textHandler = new Handler();

    ICallback fragmentSpotifyCallbacks;
    ICallback fragmentYouTubeCallbacks;

    private SpotifyService spotifyService;
    private YouTubeService youTubeService;
    RequestQueue requestQueue;
    IUser currentUser;

    private String spotifyAccessToken;

    private Boolean hasSpotify = false;
    private Boolean hasYouTube = false;
    private List<String> friends = new ArrayList<>();



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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



        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            firebaseUser = firebaseAuth.getCurrentUser();
        }


        //                setup network request queue and services
        requestQueue = Volley.newRequestQueue(this);
        spotifyService = new SpotifyService(new SpotifyCallbacks());
        youTubeService = new YouTubeService(
                getApplicationContext(),
                this,
                new YouTubeCallbacks()
        );

        currentUser = User.getInstance();
        Runnable runnableThread = new runnableThread();
        new Thread(runnableThread).start();

    }

    public FirebaseUser getFirebaseUser() {
        return this.firebaseUser;
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
                    Log.v("TOKENNULL", response.getAccessToken());

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

    public void fetchYouTubePlaylistTracks() {
        youTubeService.setCallType(ApiCallTypes.PLAYLISTS);

        NetworkRequest.YouTubeApiCall runnableThread =
                new NetworkRequest.YouTubeApiCall(
                        youTubeService,
                        currentUser
                );

        new Thread(runnableThread).start();

    }

    public void fetchSpotifyPlaylistsTracks(List<IPlaylist> playlists) {
        spotifyService.setCallType(ApiCallTypes.TRACKS);
        spotifyService.setPlaylistsToFetchTracks(playlists);

        NetworkRequest.SpotifyApiCall runnableThread =
                new NetworkRequest.SpotifyApiCall(spotifyService,
                        requestQueue,
                        currentUser);

        new Thread(runnableThread).start();

//        spotifyService.fetchTracks(
//                requestQueue,
//                spotifyAccessToken,
//                playlistId
//        );
    }






    public class SpotifyCallbacks implements ICallback {

        @Override
        public void tracksCallback(Object obj) {
            fragmentSpotifyCallbacks.tracksCallback(obj);
        }

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

        }

        public void playlistsCallback(Object obj) {

            fragmentYouTubeCallbacks.playlistsCallback(obj);

        }

        @Override
        public void tracksCallback(Object obj) {
            Map<Playlist, List<PlaylistItem>> youTubePlaylistMap = (Map<Playlist, List<PlaylistItem>>) obj;
            List<String> tracks = new ArrayList<>();
            for(Playlist playlist : youTubePlaylistMap.keySet()) {
                for(PlaylistItem track : youTubePlaylistMap.get(playlist)) {
                    tracks.add(track.getSnippet().getTitle());
                }
            }

            fragmentYouTubeCallbacks.tracksCallback(tracks);


        }


    }

    public IUser getCurrentUser() {
        return this.currentUser;
    }


    private void addPlaylistToDatabase(IPlaylist playlist) {

        Map<String, Object> playlistMap = new HashMap<>();

        playlistMap.put("owner", firebaseUser.getEmail());
        playlistMap.put("type", playlist.getType().toString());
//        playlistMap.put("tracks", Arrays.asList())

        db.collection("playlists").document().set(playlistMap).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DBOps", "playlist successfully written to DB!");
//                        fetchNewUserToken();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("DBOps", "Error writing playlist to DB! :(", e);
            }
        });

    }

    public void spotifyValidated() {
        this.hasSpotify = true;

    }

    public void youTubeValidated() {
        this.hasYouTube = true;
    }

    public Boolean doesHaveSpotify() {
        return this.hasSpotify;


    }

    public Boolean doesHaveYouTube() {
        return this.hasYouTube;
    }

    public IPlaylist getTappedPlaylist() {
        return this.tappedPlaylist;
    }

    public void setTappedPlaylist(IPlaylist tappedPlaylist) {
        this.tappedPlaylist = tappedPlaylist;
    }

    public String getTappedFriend() {
        return this.tappedFriend;
    }

    public void setTappedFriend(String tappedFriend) {
        this.tappedFriend = tappedFriend;
    }

    public Boolean isPlaylistArrayLocked() {
        return this.playlistArrayIsLocked;
    }

    public void lockPlaylistArray() {
        this.playlistArrayIsLocked = true;
    }

    public void unlockPlaylistArray() {
        this.playlistArrayIsLocked = false;
    }

    public List<String> getFriends() {
        Set<String> friendSet = new HashSet<>(this.friends);
        return new ArrayList<>(friendSet);
    }

    public void addFriends(List<String> newFriends) {
        this.friends.addAll(newFriends);
    }

    private void fetchFriends() {
        Query q = db.collection("users")
                .whereEqualTo(
                        "email",
                        firebaseUser.getEmail());
        Log.v("curremail", firebaseUser.getEmail());

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                              if (task.isSuccessful()) {

                                                  if (!task.getResult().isEmpty()) {

                                                      for (QueryDocumentSnapshot document : task.getResult()) {
                                                          if (document.getData().containsKey("friends")) {

                                                              List a = (List)document
                                                                      .getData()
                                                                      .get("friends");


                                                              friends.clear();
                                                              friends.addAll(a);

                                                          }
                                                      }
                                                  }

                                                  else {
                                                      Toast.makeText(getApplicationContext(),
                                                              "Could not fetch friends, please try again!",
                                                              Toast.LENGTH_SHORT).show();
                                                  }

                                              }

                                              else {
                                                  task.getException().printStackTrace();
                                              }
                                          }
                                      }

        );
    }


    class runnableThread implements Runnable {
        @Override
        public void run() {
            fetchFriends();
        }

    }

    public void updateFriends() {
        Runnable runnableThread = new runnableThread();
        new Thread(runnableThread).start();
    }




}












