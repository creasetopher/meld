package com.example.meld.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.MainActivity;
import com.example.meld.R;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.services.SpotifyService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TracksFragment extends Fragment {
    MainActivity theActivity;
    public static ArrayAdapter<String> listAdapter;
    public static List<String> tracks = new ArrayList<>();
    ListView listView;
    FloatingActionButton sharePlaylistButton;
//    RequestQueue requestQueue;

    JSONObject spotifyTracksObject;

    IPlaylist thePlaylist;

    private Handler textHandler = new Handler();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tracks, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);

        listAdapter = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_list_item_1,
                tracks);

//        requestQueue = Volley.newRequestQueue(getContext());
        return root;
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        theActivity = (MainActivity)getActivity();

        firebaseUser = theActivity.getFirebaseUser();

        //this is how the main activity sends playlist data to playlist frag
        // after activty fetches playlists, it hands data back to this frag through
        // this.playlistsCallback
//        theActivity.registerFragmentSpotifyCallback(new PlaylistFragment.SpotifyCallbacks());
//        theActivity.registerFragmentYouTubeCallback(new PlaylistFragment.YouTubeCallbacks());

        listView = (ListView) view.findViewById(R.id.tracks_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                IPlaylist element = (IPlaylist) listView.getItemAtPosition(position);
            }
        });


        sharePlaylistButton = view.findViewById(R.id.share_playlist_button_from_tracks_fragment);
        thePlaylist = theActivity.getTappedPlaylist();

        tracks.clear();
        tracks.addAll(thePlaylist.getTracks());
        listAdapter.notifyDataSetChanged();

        sharePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(TracksFragment.this)
                        .navigate(R.id.action_navigation_tracks_to_navigation_share_playlist);

            }
        });




    }


    public class SpotifyCallbacks implements ICallback {

        // make the playlist button visible here, after user data and user id is fetched
        public void userDataCallback(Object obj, IUser updatedUser) {
        }

        public void playlistsCallback(Object obj) {

        }

        @Override
        public void tracksCallback(Object obj) {

        }

    }

    public class YouTubeCallbacks implements ICallback {


        public void userDataCallback(Object obj, IUser updatedUser) {
        }

        public void playlistsCallback(Object obj) {
        }

        @Override
        public void tracksCallback(Object obj) {

        }

    }
}
