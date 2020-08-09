package com.example.meld.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.example.meld.PlaylistsActivity;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.YouTubePlaylist;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;

import org.json.JSONObject;

import java.time.chrono.IsoEra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.AEADBadTagException;

public class YouTubeService implements EasyPermissions.PermissionCallbacks {

    public static final int SELECT_YT_ACCOUNT_CODE = 1004;


    private static final String SAVED_ACCOUNT_KEY = "accountName";
    GoogleAccountCredential credential;
    YouTube youTubeInstance;
    Activity activityContext;
    private static final List<String> SCOPES = Arrays.asList(YouTubeScopes.YOUTUBE_READONLY);
    ICallback callbacks;
    Map<Playlist, List<PlaylistItem>> playlistMap = new HashMap();
    private ApiCallTypes callType;



    public YouTubeService(Context applicationContext,
                          Activity activityContext,
                          ICallback youTubeCallbacks) {
        this.activityContext = activityContext;
        this.callbacks = youTubeCallbacks;
        credential = GoogleAccountCredential.usingOAuth2(
                applicationContext, SCOPES)
                .setBackOff(new ExponentialBackOff());


        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        youTubeInstance = new YouTube.Builder(
                httpTransport,
                jsonFactory,
                credential).build();
    }

    public void authenticate() {

        if (EasyPermissions.hasPermissions(
                this.activityContext, Manifest.permission.GET_ACCOUNTS)) {

            String accountName = this.activityContext.getPreferences(Context.MODE_PRIVATE)
                    .getString(SAVED_ACCOUNT_KEY, null);

            if (accountName != null) {
                credential.setSelectedAccountName(accountName);
                fetchUserData();
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                fetchPlaylists();
            } else {

                // select a g account
                this.activityContext.startActivityForResult(
                        credential.newChooseAccountIntent(),
                        SELECT_YT_ACCOUNT_CODE);
//                fetchUserData();
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this.activityContext,
                    "Please authenticate using a Google Account on this device.",
                    SELECT_YT_ACCOUNT_CODE,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Toast.makeText(activityContext.getApplicationContext(), "Permissions granted! Yay!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        Toast.makeText(activityContext.getApplicationContext(), "permission denied - this app needs permission to work!", Toast.LENGTH_SHORT).show();
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        activityContext.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(
//                requestCode, permissions, grantResults, this);
    }



    public void fetchUserData() {
        Log.v("credential from ytservice", Boolean.toString(this.credential != null ));
        this.callbacks.userDataCallback(credential, null);
    }

    public void fetchPlaylists() {

        try {
            YouTube.Playlists.List playlistRequest =
                    youTubeInstance
                            .playlists()
                            .list("contentDetails,id,snippet,status");

            PlaylistListResponse response = playlistRequest.setMine(true).execute();

            Log.v("Playlist Resp", response.toPrettyString());

            List<Playlist> playlists = response.getItems();


            YouTube.PlaylistItems.List tracksRequest =
                    youTubeInstance
                            .playlistItems()
                            .list("contentDetails,id,snippet,status");

//            BatchRequest trackBatchRequest =  youTubeInstance.batch();
//            trackBatchRequest.queue()

            for (Playlist playlist : playlists) {

                List<PlaylistItem> playlistItems = new ArrayList<>();

                PlaylistItemListResponse playlistItemListResponse =
                        tracksRequest
                                .setPlaylistId(playlist.getId()).execute();

                Log.v("Playlist Items Resp", playlistItemListResponse.toPrettyString());

                playlistItems.addAll(playlistItemListResponse.getItems());

                playlistMap.put(playlist, playlistItems);


            }

            this.callbacks.playlistsCallback(playlistMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void fetchPlaylistList() {
        Log.v("youtubeservice", "go here");
        try {
            YouTube.Playlists.List playlistRequest =
                    youTubeInstance
                            .playlists()
                            .list("contentDetails,id,snippet,status");

            PlaylistListResponse response = playlistRequest.setMine(true).execute();

            Log.v("Playlist Resp", response.toPrettyString());

            List<Playlist> playlists = response.getItems();


            YouTube.PlaylistItems.List tracksRequest =
                    youTubeInstance
                            .playlistItems()
                            .list("contentDetails,id,snippet,status");

//            BatchRequest trackBatchRequest =  youTubeInstance.batch();
//            trackBatchRequest.queue()

            for (Playlist playlist : playlists) {

                List<PlaylistItem> playlistItems = new ArrayList<>();

                PlaylistItemListResponse playlistItemListResponse =
                        tracksRequest
                                .setPlaylistId(playlist.getId()).execute();

//                Log.v("Playlist Items Resp", playlistItemListResponse.toPrettyString());

                playlistItems.addAll(playlistItemListResponse.getItems());

                playlistMap.put(playlist, playlistItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.callbacks.playlistsCallback(playlistMap);

        Log.v("Playlist Map at end", playlistMap.toString());

    }


    public void setCallType(ApiCallTypes callType) {
        this.callType = callType;
    }


    public ApiCallTypes getCallType() {
        return this.callType;
    }

    public GoogleAccountCredential getCredential() {

        if (credential.getSelectedAccountName() == null)
        {


        }
        return this.credential;
    }


}