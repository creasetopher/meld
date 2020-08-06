package com.example.meld.ui.playlists;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.meld.R;
import com.example.meld.models.IPlaylist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    public String userDataObject;
    public static ArrayAdapter<String> listAdapter;
    public static List<IPlaylist> playlists = new ArrayList<>();
    ListView listView;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        userDataObject = getArguments().getString("userDataObject");

//        Log.v("userDataObjecttaObjectFrom frag", userDataObject);
        Log.v("userDataObjecttaObjectFrom frag", "!!!!!!!!");
//        Log.v("args frag", getArguments().toString());



        listAdapter = new ArrayAdapter(
                this.getContext(),
                android.R.layout.simple_list_item_1,
                playlists);

        listView = (ListView)view.findViewById(R.id.playlists_listview);

        listView.setAdapter(listAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaylist element = (IPlaylist)listView.getItemAtPosition(position);
            }
        });
    }
}
