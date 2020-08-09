package com.example.meld.ui.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meld.MainActivity;
import com.example.meld.R;
import com.example.meld.models.IPlaylist;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlaylistShareFragment extends Fragment {


    private FloatingActionButton addFriendButton;

    ArrayAdapter<String> userArrayAdapter;

    FirebaseAuth firebaseAuth;
    private Spinner userDropdown;
    private IPlaylist thePlaylist;

    FirebaseUser currentUser;
    MainActivity theActivity;
    List<String> friends = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist_share, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);

        theActivity = (MainActivity)getActivity();




        return root;

    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = theActivity.getFirebaseUser();

        userArrayAdapter = new ArrayAdapter(theActivity,
                android.R.layout.simple_spinner_dropdown_item,
                friends);

        userDropdown = view.findViewById(R.id.username_input);

        userDropdown.setAdapter(userArrayAdapter);

        userDropdown = view.findViewById(R.id.username_input);

        userDropdown.setGravity(Gravity.CENTER);

        thePlaylist = theActivity.getTappedPlaylist();


        getFriends();




    }




    public void getFriends() {
        friends.add("TEst");
        this.friends.addAll(theActivity.getFriends());
        userArrayAdapter.notifyDataSetChanged();

    }


}
