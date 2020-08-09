package com.example.meld.ui.friends;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.meld.MainActivity;
import com.example.meld.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFriendFragment extends Fragment {

    Button addFriendButton;

    private Boolean userFound = false;

    FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private EditText usernameInput;

    MainActivity theActivity;


    ProgressBar loadingProgressBar;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        theActivity = (MainActivity)getActivity();
        currentUser = theActivity.getFirebaseUser();

        usernameInput = view.findViewById(R.id.username_input);


        addFriendButton = view.findViewById(R.id.add_friend_button);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendUsername = usernameInput.getText().toString();
                tryToAddFriend(friendUsername);
            }
        });





    }


    public void tryToAddFriend(String username){

        if(username == null || username.length() < 1 ) {
            Toast.makeText(theActivity.getApplicationContext(),
                    "Please enter a valid username",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        String userEmail = asEmail(username);

        //first query to see if user actually exists
        Query q = db.collection("users")
                .whereEqualTo(
                        "email",
                        userEmail);


        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                              if (task.isSuccessful()) {

                                                  if (!task.getResult().isEmpty()) {
                                                      for (QueryDocumentSnapshot document : task.getResult()) {
                                                          if (document.getData().containsKey("email")) {
                                                              String possibleUser = (String) document.getData()
                                                                              .get("email");

                                                              if (possibleUser.equals(userEmail)) {
                                                                  userFound = true;
                                                                  addFriendToDatabase(userEmail);

                                                              }

                                                          }
                                                      }

                                                  }

                                                  else {
                                                      String userNotFoundString =
                                                              String.format(
                                                                      "Couldn't find user %s, please try again!", username
                                                                      );

                                                      Toast.makeText(theActivity.getApplicationContext(),
                                                              userNotFoundString,
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


    private String asEmail(String username) {
        if (!username.contains("@")) {
            username = username.concat("@meld.io");
        }

        else {
            username = username.substring(0, username.indexOf('@'));
        }
        return username;
    }




    public void addFriendToDatabase(String userEmail) {

        Query thisUserQuery = db.collection("users").whereEqualTo("email", currentUser.getEmail());


        thisUserQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().containsKey("email")) {
                                String possibleUser = (String)document.getData()
                                        .get("email");

                                if (possibleUser.equals(currentUser.getEmail())) {

                                    DocumentReference documentReference = document.getReference();

                                    documentReference.update("friends", FieldValue.arrayUnion(userEmail));

                                    Toast.makeText(theActivity.getApplicationContext(),
                                            "Added a new friend!",
                                            Toast.LENGTH_SHORT).show();
                                    theActivity.updateFriends();

                                }

                            }
                        }

                    }
                    else {

                        Toast.makeText(theActivity.getApplicationContext(),
                                "Couldn't add user to friends-list, please try again!",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


    }

}
