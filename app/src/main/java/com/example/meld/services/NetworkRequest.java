package com.example.meld.services;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.example.meld.R;
import com.example.meld.models.IUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;

import java.util.Map;

public class NetworkRequest  {

    public static class SpotifyApiCall implements Runnable{
        SpotifyService spotifyService;
        RequestQueue requestQueue;
        IUser user;

        public SpotifyApiCall(SpotifyService spotifyService,
                              RequestQueue requestQueue,
                              IUser user) {
            this.spotifyService = spotifyService;
            this.requestQueue = requestQueue;
            this.user = user;
        }

        @Override
        public void run() {

            try {

                if (this.spotifyService.getCallType().equals(ApiCallTypes.USER_DATA)) {
                    spotifyService.fetchUserData(requestQueue, user.getSpotifyAccessToken());
                }

                else if(this.spotifyService.getCallType().equals(ApiCallTypes.PLAYLISTS)) {
                    spotifyService.fetchPlaylists(
                            this.requestQueue,
                            user.getSpotifyAccessToken(),
                            user.getId());
                }

                else if(this.spotifyService.getCallType().equals(ApiCallTypes.TRACKS)) {
                    spotifyService.fetchBatchTracks(
                            this.requestQueue,
                            user.getSpotifyAccessToken(),
                            spotifyService.getPlaylistsToFetchTracks());
                }


            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }


    public static class YouTubeApiCall implements Runnable {
        YouTubeService youTubeService;
        IUser user;

        public YouTubeApiCall(YouTubeService youTubeService,
                              IUser user) {

            this.youTubeService = youTubeService;
            this.user = user;

        }

        @Override
        public void run() {

            try {

                if(this.youTubeService.getCallType().equals(ApiCallTypes.PLAYLISTS)) {
                    youTubeService.fetchPlaylists();
                }


            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public static class FirestoreDBOperationCreate implements Runnable {
        FirebaseFirestore dbRef;
        String key;
        Object object;

        public FirestoreDBOperationCreate(FirebaseFirestore dbRef, String key, Object object) {
                this.dbRef = dbRef;
                this.key =  key;
                this.object = object;
        }

        @Override
        public void run() {

            if(this.key.equals("playlists")) {

                Map<String, Object> playlistMap = (Map<String, Object>)this.object;

                Query q = this.dbRef.collection("playlsits")
                        .whereEqualTo(
                                "name",
                                (String)playlistMap.get("name")).whereEqualTo("owner", (String)playlistMap.get("owner"));
                Log.v("the name", (String)playlistMap.get("name"));

                Log.v("the owner", (String)playlistMap.get("owner"));


                q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                      if (task.isSuccessful()) {

                                                          if (task.getResult().isEmpty()) {


                                                              dbRef.collection(key).document().set(object, SetOptions.merge()).addOnSuccessListener(
                                                                      new OnSuccessListener<Void>() {
                                                                          @Override
                                                                          public void onSuccess(Void aVoid) {
                                                                              Log.v("DBOps", String.format("successful write to DB collection %s!", key));
                                                                          }
                                                                      }
                                                              ).addOnFailureListener(new OnFailureListener() {
                                                                  @Override
                                                                  public void onFailure(@NonNull Exception e) {
                                                                      Log.v("DBOps", String.format("Error writing to %s DB collection! :(", key), e);
                                                                  }
                                                              });




                                                          }

                                                      }

                                                      else {
                                                          task.getException().printStackTrace();
                                                      }
                                                  }
                                              }

                );

            }
        }
    }

//    public static class FirestoreDBOperationRead implements Runnable {
//        FirebaseFirestore dbRef;
//        String collectionKey;
//        String param;
//        String paramValue;
//
//        public FirestoreDBOperationRead(FirebaseFirestore dbRef, String collectionKey, String param, String paramValue) {
//            this.dbRef = dbRef;
//            this.collectionKey =  collectionKey;
//            this.param = param;
//            this.paramValue = paramValue;
//        }
//
//        @Override
//        public void run() {
//
//
//            Query q = this.dbRef.collection(this.collectionKey)
//                    .whereEqualTo(
//                            param,
//                            paramValue);
//
//
//            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                              @Override
//                                              public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                  if (task.isSuccessful()) {
//
//                                                      if (!task.getResult().isEmpty()) {
//                                                          for (QueryDocumentSnapshot document : task.getResult()) {
//                                                              if (document.getData().containsKey("email")) {
//                                                                  String possibleUser = (String) document.getData()
//                                                                          .get("email");
//
//                                                                  if (possibleUser.equals(userEmail)) {
//                                                                      userFound = true;
//                                                                      addFriendToDatabase(userEmail);
//
//                                                                  }
//
//                                                              }
//                                                          }
//
//                                                      }
//
//                                                      else {
//                                                          String userNotFoundString =
//                                                                  String.format(
//                                                                          "Couldn't find user %s, please try again!", username
//                                                                  );
//
//                                                          Toast.makeText(theActivity.getApplicationContext(),
//                                                                  userNotFoundString,
//                                                                  Toast.LENGTH_SHORT).show();
//                                                      }
//
//                                                  }
//
//                                                  else {
//                                                      task.getException().printStackTrace();
//                                                  }
//                                              }
//                                          }
//
//            );
//
//
//
//
//
//        }
//    }


}
