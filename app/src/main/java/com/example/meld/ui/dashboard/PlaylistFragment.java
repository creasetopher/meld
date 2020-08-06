package com.example.meld.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.meld.PlaylistsActivity;
import com.example.meld.R;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.IUser;
import com.example.meld.services.SpotifyService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("user", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                user = (IUser)result.get("user");
            }
        });

//        f.set

//        setTargetFragment();
//        getParentFragmentManager().s
//                setFragmentResultListener("key", this, new FragmentResultListener() {
//            @Override
//            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
//                // We use a String here, but any type that can be put in a Bundle is supported
//                String result = bundle.getString("bundleKey");
//                // Do something with the result...
//            }
//        });
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlists, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);


        listAdapter = new ArrayAdapter(
                getContext(),
                android.R.layout.simple_list_item_1,
                allPlaylists);

        requestQueue = Volley.newRequestQueue(getContext());




        return root;
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.playlists_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaylist element = (IPlaylist) listView.getItemAtPosition(position);
            }
        });



        try {
            ArrayList<IPlaylist> temp = new ArrayList<>();
//            JSONObject playlistsObjectJson = new JSONObject(playlistsObjectString);

            allPlaylists.clear();
//            allPlaylists.addAll(JsonPlaylistParser.toJsonObjectArray(playlistsObjectJson));
            listAdapter.notifyDataSetChanged();

        }

        catch (Exception e) {
            e.printStackTrace();
        }


    }

}
