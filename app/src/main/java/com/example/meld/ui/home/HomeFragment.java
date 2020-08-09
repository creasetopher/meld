package com.example.meld.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.android.volley.RequestQueue;
import com.example.meld.MainActivity;
import com.example.meld.R;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.models.YouTubePlaylist;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {


    private static final String CLIENT_ID = "f76befcc65b1474fb7db808201c9593b";
    private static final String REDIRECT_URI = "http://com.yourdomain.yourapp/callback";




    MainActivity theActivity;
    Button spotifyLoginButton;
    Button youtubeLoginButton;
    private String spotifyAccessToken;
    IUser user;
    ProgressBar progressBar;
    FragmentManager fragmentManager;
    View root;
    RequestQueue requestQueue;


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);


         this.fragmentManager = getParentFragmentManager();

        this.spotifyLoginButton = root.findViewById(R.id.spotify_login_button);
        this.youtubeLoginButton = root.findViewById(R.id.youtube_login_button);
        this.progressBar = root.findViewById(R.id.progress_circular);

        return root;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        theActivity = (MainActivity)getActivity();
        theActivity.registerFragmentSpotifyCallback(new SpotifyCallbacks());
        theActivity.registerFragmentYouTubeCallback(new YouTubeCallbacks());


        spotifyLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//             goToPlaylistActivity();
                theActivity.authenticateSpotify();

            }
        });

        youtubeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theActivity.authenticateYouTube();
            }
        });

        if(theActivity.doesHaveSpotify()) {
            disableSpotifyLoginButton();
        }

        if(theActivity.doesHaveYouTube()) {
            disableYouTubeLoginButton();
        }


    }

    private void disableSpotifyLoginButton() {
        View spotifyButtonView = root.findViewById(R.id.spotify_button_view);
        spotifyButtonView.setVisibility(View.INVISIBLE);
        View foundSpotifyTextView = root.findViewById(R.id.found_spotify_view);
        foundSpotifyTextView.setVisibility(View.VISIBLE);
    }

    private void disableYouTubeLoginButton() {
        View youTubeButtonView = root.findViewById(R.id.youtube_button_view);
        youTubeButtonView.setVisibility(View.INVISIBLE);
        View foundYoutubeText = root.findViewById(R.id.found_youtube_view);
        foundYoutubeText.setVisibility(View.VISIBLE);
    }





//    API Response CALLBACKS *********************

   public class SpotifyCallbacks implements ICallback {


       // make the playlist button visible here, after user data and user id is fetched
       public void userDataCallback(Object obj, IUser updatedUser) {
           user = updatedUser;
           theActivity.spotifyValidated();
           disableSpotifyLoginButton();

       }

       public void playlistsCallback(Object obj) {


       }

       @Override
       public void tracksCallback(Object obj) {

       }
   }

    public class YouTubeCallbacks implements ICallback {



        public void userDataCallback(Object obj, IUser updatedUser) {
            user = updatedUser;
            theActivity.youTubeValidated();
            try {
                //dont get token in main thread, maybe give it back in callback?
//                Log.v("credential from home from user", Boolean.toString(user.getGoogleUserObject() != null));
//                Log.v("credential from home", Boolean.toString(obj != null));

            } catch (Exception e) {
                e.printStackTrace();
            }

            disableYouTubeLoginButton();


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

        @Override
        public void tracksCallback(Object obj) {

        }


    }

//    private class



}
