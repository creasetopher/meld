package com.example.meld.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.meld.models.YouTubePlaylist;
import com.example.meld.services.NetworkRequest;
import com.example.meld.services.SpotifyService;
import com.example.meld.ui.friends.FriendsFragment;
import com.example.meld.utils.JsonPlaylistParser;
import com.example.meld.utils.MapPlaylistParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlaylistFragment extends Fragment {

    public String spotifyUserDataObject;
    public JSONObject spotifyPlaylistsObject;

    public  Map<Playlist, List<PlaylistItem>> youTubePlaylistMap;



    MainActivity theActivity;
    String accessToken;
    IUser user;
    public static ArrayAdapter<String> listAdapter;
    public static List<IPlaylist> allPlaylists = new ArrayList<>();
    ListView listView;
    private SpotifyService spotifyService;
    RequestQueue requestQueue;

    private Handler textHandler = new Handler();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlists, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);

        listAdapter = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_selectable_list_item,
                allPlaylists);

        requestQueue = Volley.newRequestQueue(getContext());
        return root;
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        theActivity = (MainActivity)getActivity();

        firebaseUser = theActivity.getFirebaseUser();
        //this is how the main activity sends playlist data to playlist frag
        // after activty fetches playlists, it hands data back to this frag through
        // this.playlistsCallback
        theActivity.registerFragmentSpotifyCallback(new SpotifyCallbacks());
        theActivity.registerFragmentYouTubeCallback(new YouTubeCallbacks());

        listView = (ListView) view.findViewById(R.id.playlists_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaylist playlist = (IPlaylist) listView.getItemAtPosition(position);
                theActivity.setTappedPlaylist(playlist);
                if(playlist.getTracks() != null) {
                    NavHostFragment.findNavController(PlaylistFragment.this)
                            .navigate(R.id.action_navigation_playlists_to_navigation_tracks);
//                Log.v("thetracks", playlist.getTracks().toString());
                }
            }
        });

        if(theActivity.doesHaveSpotify()) {
            theActivity.fetchSpotifyPlaylists();


        }

        if(theActivity.doesHaveYouTube()) {
            theActivity.fetchYouTubePlaylists();
        }

    }

    private void addSpotifyPlaylists(){
        List<IPlaylist> spotifyPlaylists = JsonPlaylistParser.toArrayList(spotifyPlaylistsObject);
        populatePlaylistView(spotifyPlaylists);
    }

    private void addYouTubePlaylists(){
        List<IPlaylist> youTubePlaylists = MapPlaylistParser.toArrayList(youTubePlaylistMap);
        populatePlaylistView(youTubePlaylists);
    }

    private void populatePlaylistView(List<IPlaylist> playlists) {
//        allPlaylists.clear();
//        arrayIsLocked = true;

        this.theActivity.lockPlaylistArray();
        allPlaylists.addAll(playlists);
        listAdapter.notifyDataSetChanged();
        this.theActivity.unlockPlaylistArray();
//        arrayIsLocked = false;
    }


    public class SpotifyCallbacks implements ICallback {

        // make the playlist button visible here, after user data and user id is fetched
        public void userDataCallback(Object obj, IUser updatedUser) {



        }

        public void playlistsCallback(Object obj) {

            spotifyPlaylistsObject = (JSONObject) obj;

            List<IPlaylist> spotifyPlaylists = JsonPlaylistParser.toArrayList(spotifyPlaylistsObject);

            addSpotifyPlaylists();


            theActivity.fetchSpotifyPlaylistsTracks(spotifyPlaylists);



        }


        // this is called each time the user views the playlist frag
        // a necessary evil to ensure the tracks for each spotify playlists are fetched
        @Override
        public void tracksCallback(Object obj) {

            List<IPlaylist> spotifyPlaylists = JsonPlaylistParser.toArrayList(spotifyPlaylistsObject);


            List<JSONObject> tobjs = (List<JSONObject>)obj;
//            Log.v("tracks", tobjs.get(0).toString());

            for(int i = 0; i < tobjs.size(); i++){
                JsonPlaylistParser.addTracks(allPlaylists.get(i), tobjs.get(i));
            }

            persistAllPlaylists();


        }
    }

    public class YouTubeCallbacks implements ICallback {



        public void userDataCallback(Object obj, IUser updatedUser) {

        }

        public void playlistsCallback(Object obj) {

            youTubePlaylistMap = (Map<Playlist, List<PlaylistItem>>) obj;

//            Log.v("FROM PACT", youTubePlaylistMap.toString());


            textHandler.post(() -> addYouTubePlaylists());


//        return res;
            // need a youtube playlist impl


        }

        @Override
        public void tracksCallback(Object obj) {
//            JSONObject tracks = (JSONObject)obj;
//            Log.v("tracks", tracks.toString());
        }

    }


    private void addPlaylistToDatabase(IPlaylist playlist) {

        Map<String, Object> playlistMap = new HashMap<>();

        playlistMap.put("name", playlist.getName());
        playlistMap.put("owner", firebaseUser.getEmail());
        playlistMap.put("type", playlist.getType().toString());
        playlistMap.put("tracks", playlist.getTracks());

        NetworkRequest.FirestoreDBOperationCreate runnableThread =
                new NetworkRequest.FirestoreDBOperationCreate(this.db, "playlists", playlistMap);


        new Thread(runnableThread).start();




    }

    public void persistAllPlaylists() {
        if(!theActivity.isPlaylistArrayLocked()) {
            theActivity.lockPlaylistArray();
            for (IPlaylist playlist : allPlaylists) {
                addPlaylistToDatabase(playlist);
            }
            theActivity.unlockPlaylistArray();
        }
    }

//    private void addTracks(IPlaylist, )



    //fetch tracks!!!!!!!

}
