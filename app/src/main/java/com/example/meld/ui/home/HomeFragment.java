package com.example.meld.ui.home;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.MainActivity;
import com.example.meld.PlaylistsActivity;
import com.example.meld.R;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.User;
import com.example.meld.models.YouTubePlaylist;
import com.example.meld.services.ApiCallTypes;
import com.example.meld.services.NetworkRequest;
import com.example.meld.services.SpotifyService;
import com.example.meld.services.YouTubeService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends FragmentActivity {


    private static final String CLIENT_ID = "f76befcc65b1474fb7db808201c9593b";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";
    private SpotifyService spotifyService;
    private YouTubeService youTubeService;




    Button spotifyLoginButton;
    Button youtubeLoginButton;
    private String spotifyAccessToken;
    User user;
    ProgressBar progressBar;
    FragmentManager fragmentManager;
    View root;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        FragmentActivity theActivity = this;

//                setup network request queue and services
        requestQueue = Volley.newRequestQueue(this);
        spotifyService = new SpotifyService(new HomeFragment.SpotifyCallbacks());
        youTubeService = new YouTubeService(
                this.getApplicationContext(),
                this,
                new HomeFragment.YouTubeCallbacks()
        );



         this.fragmentManager = getSupportFragmentManager();

        this.spotifyLoginButton = findViewById(R.id.spotify_login_button);
        this.youtubeLoginButton = findViewById(R.id.youtube_login_button);
        this.progressBar = findViewById(R.id.progress_circular);

        this.spotifyService = new SpotifyService(new SpotifyCallbacks());

        this.user = User.getInstance();



        this.fragmentManager.setFragmentResultListener("test", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                        // We use a String here, but any type that can be put in a Bundle is supported
                        String result = bundle.getString("bundleKey");
                        Log.v("fragsent!@", "!?!?");
                    }
                });







//        setup login buttons



        spotifyLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//             goToPlaylistActivity();
                spotifyService.authenticateWithSpotifyApp(theActivity);
            }
        });

        youtubeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubeService.authenticate();
            }
        });

    }




    public void fetchSpotifyUserData() {
        spotifyService.fetchUserData(requestQueue, this.spotifyAccessToken);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if(requestCode == SpotifyService.AUTH_TOKEN_REQUEST_CODE) {
                AuthorizationResponse response = spotifyService.getResponse(resultCode, requestCode, data);
                if (response != null) {
                    Log.v("gottoken", response.getAccessToken());

                    this.spotifyAccessToken = response.getAccessToken();
//                    getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    spotifyService.setCallType(ApiCallTypes.USER_DATA);

                    user.setSpotifyAccessToken(this.spotifyAccessToken);


                    NetworkRequest.SpotifyApiCall runnableThread =
                            new NetworkRequest.SpotifyApiCall(
                                    spotifyService,
                                    requestQueue,
                                    user
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

            if(requestCode == YouTubeService.REQUEST_CODE_SELECT_ACCOUNT) {
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
                        youTubeService.fetchUserData();

                    }
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

//    API Response CALLBACKS *********************

   public class SpotifyCallbacks implements ICallback {


       // make the playlist button visible here, after user data and user id is fetched
       public void userDataCallback(Object obj) {
            JSONObject userDataObj = (JSONObject) obj;
            user.setSpotifyUserDataObject(userDataObj);

           View spotifyButtonView = findViewById(R.id.spotify_button_view);
           spotifyButtonView.setVisibility(View.INVISIBLE);
           View foundSpotifyTextView = findViewById(R.id.found_spotify_view);
           foundSpotifyTextView.setVisibility(View.VISIBLE);



       }

       public void playlistsCallback(Object obj) {


       }
   }

    public class YouTubeCallbacks implements ICallback {

        public void userDataCallback(Object obj) {
            GoogleAccountCredential credential = (GoogleAccountCredential)obj;
            try {
                //dont get token in main thread, maybe give it back in callback?
                Log.v("credential", credential.getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }

            View spotifyButtonView = findViewById(R.id.youtube_button_view);
            spotifyButtonView.setVisibility(View.INVISIBLE);
            View foundYoutubeText = findViewById(R.id.found_youtube_view);
            foundYoutubeText.setVisibility(View.VISIBLE);

            


        }

        public void playlistsCallback(Object obj) {
//            playlistsObjectString = (JSONObject) obj;
            Map< Playlist, List< PlaylistItem >>
                    youTubePlaylistMap = (Map<Playlist, List<PlaylistItem>>)obj;

            List<IPlaylist> res = new ArrayList<>();
            for (Playlist playlist : youTubePlaylistMap.keySet()) {
                YouTubePlaylist youTubePlaylist = new YouTubePlaylist();

                youTubePlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
                youTubePlaylist.setName(playlist.getSnippet().getTitle());
                youTubePlaylist.setId(playlist.getId());
                youTubePlaylist.setDescription(playlist.getSnippet().getDescription());

                res.add(youTubePlaylist);

            }
//        return res;
            // need a youtube playlist impl
            Log.v("FROM PACT", obj.toString());


        }


    }



}
