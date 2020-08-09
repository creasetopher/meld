package com.example.meld.ui.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.meld.MainActivity;
import com.example.meld.R;
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


public class FriendsFragment extends Fragment {

    private FloatingActionButton addFriendButton;

    ListView friendsList;
    ArrayAdapter<String> listAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    MainActivity theActivity;
    List<String> friends = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_friends, container, false);
        theActivity = (MainActivity)getActivity();

        return root;

    }


    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addFriendButton = view.findViewById(R.id.addFriendFloatingButton);
        friendsList = view.findViewById(R.id.listViewForUsers);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = theActivity.getFirebaseUser();

        listAdapter = new ArrayAdapter(
                theActivity,
                android.R.layout.simple_list_item_1,
                friends);

        friendsList.setAdapter(listAdapter);
        getFriends();

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String tappedFriend = (String) friendsList.getItemAtPosition(position);
                theActivity.setTappedFriend(tappedFriend);
                NavHostFragment.findNavController(FriendsFragment.this)
                        .navigate(R.id.action_navigation_friends_to_navigation_friends_playlist);

            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NavHostFragment.findNavController(FriendsFragment.this)
                        .navigate(R.id.action_navigation_friends_to_navigation_add_friend);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getFriends();
    }


    public void getFriends() {
//        Query q = db.collection("users")
//                .whereEqualTo(
//                        "email",
//                        currentUser.getEmail());
//        Log.v("curremail", currentUser.getEmail());
//
//        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                          @Override
//                                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                              if (task.isSuccessful()) {
//
//                                                  if (!task.getResult().isEmpty()) {
//
//                                                      for (QueryDocumentSnapshot document : task.getResult()) {
//                                                          if (document.getData().containsKey("friends")) {
//
//                                                              List a = (List)document
//                                                                      .getData()
//                                                                      .get("friends");
//
//                                                              Log.v("frindsList", a.toString());
//
//                                                              friends.clear();
//                                                              friends.addAll(a);
//                                                              listAdapter.notifyDataSetChanged();
//                                                              theActivity.addFriends(a);
//
//                                                          }
//                                                      }
//                                                  }
//
//                                                  else {
//                                                      Toast.makeText(theActivity.getApplicationContext(),
//                                                              "Could not fetch friends, please try again!",
//                                                              Toast.LENGTH_SHORT).show();
//                                                  }
//
//                                              }
//
//                                              else {
//                                                  task.getException().printStackTrace();
//                                              }
//                                          }
//                                      }
//
//        );
//    }
        Log.v("getfreindscalled", "Called");
        friends.clear();
        friends.addAll(theActivity.getFriends());
        listAdapter.notifyDataSetChanged();
    }


}
