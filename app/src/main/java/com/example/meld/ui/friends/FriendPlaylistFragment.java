package com.example.meld.ui.friends;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.MainActivity;
import com.example.meld.R;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.services.SpotifyService;
import com.example.meld.ui.dashboard.PlaylistFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendPlaylistFragment extends Fragment {



    MainActivity theActivity;
    private String tappedFriend;
    public static ArrayAdapter<String> listAdapter;
    public static List<String> allPlaylists = new ArrayList<>();
    ListView listView;

    private Handler textHandler = new Handler();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser firebaseUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friend_playlists, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);

        listAdapter = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_selectable_list_item,
                allPlaylists);

        return root;
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        theActivity = (MainActivity)getActivity();

        firebaseUser = theActivity.getFirebaseUser();

        TextView header = view.findViewById(R.id.text_dashboard);


        tappedFriend = theActivity.getTappedFriend();
//        allPlaylists.add("TESTTESTTEST");

        //this is how the main activity sends playlist data to playlist frag
        // after activty fetches playlists, it hands data back to this frag through
        // this.playlistsCallback

        listView = (ListView) view.findViewById(R.id.playlists_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaylist playlist = (IPlaylist) listView.getItemAtPosition(position);
                theActivity.setTappedPlaylist(playlist);
//                NavHostFragment.findNavController(PlaylistFragment.this)
//                        .navigate(R.id.);
                Log.v("thetracks", playlist.getTracks().toString());

            }
        });

        header.setText(String.format("%s Playlists", tappedFriend.substring(0, tappedFriend.indexOf('@'))));

        getPlaylists();


    }

    public void getPlaylists() {

        Query query = db.collection("playlists").whereEqualTo("owner", tappedFriend.toLowerCase());


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            if (document.getData().containsKey("name")) {
                                String playlistName = (String)document.getData()
                                        .get("name");

                                allPlaylists.add(playlistName);

                            }
                        }
                        listAdapter.notifyDataSetChanged();

                    }
                    else {

                        Toast.makeText(theActivity.getApplicationContext(),
                                "Error fetching data, please try again!",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        });




    }



}
