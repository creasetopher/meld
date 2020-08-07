package com.example.meld.services;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.util.Log;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.meld.MainActivity;
import com.example.meld.models.ICallback;
import com.example.meld.models.IPlaylist;
import com.example.meld.models.SpotifyPlaylist;
import com.example.meld.models.YouTubePlaylist;
import com.google.android.gms.common.api.Api;
import com.spotify.sdk.android.auth.AuthorizationClient;
import  com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.RequestQueue;


public class SpotifyService {

    public static final String CLIENT_ID = "f76befcc65b1474fb7db808201c9593b";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "https://meld.page.link";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private static final String BASE_API_URL = "https://api.spotify.com";
    private static final String USERS_ENDPOINT = "/v1/me";

    private static String makePLAYLISTS_ENDPOINT(String userId){
        return String.format("/v1/users/%s/playlists", userId);
    }

    private static String makeTRACKS_ENDPOINT(String playlistId){
        return String.format("/v1/playlists/%s/tracks", playlistId);
    }

    private JSONObject userDataObject;
    private JSONObject playlistObject;
    private ICallback callbacks;
    private ApiCallTypes callType;



    public SpotifyService(ICallback callbacks) {
        this.callbacks = callbacks;
    }

    public AuthorizationRequest getRequest() {
        AuthorizationRequest request =  new AuthorizationRequest.Builder(
                CLIENT_ID,
                AuthorizationResponse.Type.TOKEN,
                SpotifyService.REDIRECT_URI)
                .setShowDialog(true)
                .setScopes(new String[]{
                        "playlist-read-collaborative",
                        "playlist-read-private",
                        "playlist-modify-public",
                        "playlist-modify-private"})
                .build();
        return request;
    }

    public void authenticateWithSpotifyApp(Activity onResultActivity) {
        final AuthorizationRequest request = getRequest();
        AuthorizationClient.openLoginActivity(
                onResultActivity,
                SpotifyService.AUTH_TOKEN_REQUEST_CODE,
                request);

    }

    public AuthorizationResponse getResponse(int resultCode, int requestCode, Intent data) throws IOException {

        if (requestCode == SpotifyService.AUTH_TOKEN_REQUEST_CODE) {
            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            if (response.getType().equals(AuthorizationResponse.Type.TOKEN)){
                return response;
            }
            else {
                throw new IOException("Could not authenticate user, please try again.");
            }
        }

        return null;
    }


    public void fetchUserData(final RequestQueue requestQueue, final String accessToken) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                String.format("%s%s", SpotifyService.BASE_API_URL, SpotifyService.USERS_ENDPOINT),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            userDataObject = new JSONObject(response);
                            callbacks.userDataCallback(userDataObject, null);
//                            Log.v("RESPONSE", userDataObject.toString());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.putIfAbsent("Authorization", String.format("Bearer %s", accessToken));
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void fetchPlaylists(
            final RequestQueue requestQueue,
            final String accessToken,
            final String userId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                String.format(
                        "%s%s",
                        SpotifyService.BASE_API_URL,
                        SpotifyService.makePLAYLISTS_ENDPOINT(userId)
                ),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            playlistObject = new JSONObject(response);
                            callbacks.playlistsCallback(playlistObject);
//                            Log.v("playlists", playlistObject.toString());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.putIfAbsent("Authorization", String.format("Bearer %s", accessToken));
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void fetchTracks(
            final RequestQueue requestQueue,
            final String accessToken,
            final String playlistId) {

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                String.format(
                        "%s%s",
                        SpotifyService.BASE_API_URL,
                        SpotifyService.makeTRACKS_ENDPOINT(playlistId)
                ),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //TODO: add tracks callback
                            playlistObject = new JSONObject(response);
                            callbacks.playlistsCallback(playlistObject);
//                            Log.v("playlists", playlistObject.toString());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.putIfAbsent("Authorization", String.format("Bearer %s", accessToken));
                return headers;
            }
        };
        requestQueue.add(stringRequest);

    }



    public void fetchPlaylistList(
            final RequestQueue requestQueue,
            final String accessToken,
            final String userId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                String.format(
                        "%s%s",
                        SpotifyService.BASE_API_URL,
                        SpotifyService.makePLAYLISTS_ENDPOINT(userId)
                ),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            playlistObject = new JSONObject(response);
                            callbacks.playlistsCallback(playlistObject);
//                            Log.v("playlists", playlistObject.toString());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.putIfAbsent("Authorization", String.format("Bearer %s", accessToken));
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void parsePlaylistResponse(JSONObject playlistsObjects) {
        List<IPlaylist> res = new ArrayList<>();
        //TODO: use gson to parse eventually
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();

        try {
            JSONArray playlists = playlistsObjects.getJSONArray("items");

            for (int i = 0; i < playlists.length(); i++) {
                JSONObject rawPlaylist = (JSONObject) playlists.get(i);

                if (rawPlaylist.getString("type").equals("spotify")) {

                    SpotifyPlaylist parsedPlaylist = new SpotifyPlaylist();

//                    Log.v("playlistact", ((JSONObject) playlists.get(i)).toString());

                    parsedPlaylist.setName(rawPlaylist.getString("name"));
                    parsedPlaylist.setId(rawPlaylist.getString("id"));

                    parsedPlaylist.setMetadata(rawPlaylist.getJSONObject("owner"));
//                    Log.v("metadata", (rawPlaylist.getJSONObject("owner").toString()));


                    parsedPlaylist.setDescription(rawPlaylist.getString("description"));
                    parsedPlaylist.setVisibility(rawPlaylist.getBoolean("public"));
                    parsedPlaylist.setType(IPlaylist.PlaylistType.SPOTIFY);

                    JSONArray imagesJsonArray = rawPlaylist.getJSONArray("images");
                    List<Object> images = new ArrayList<>();

                    for (int j = 0; j < imagesJsonArray.length(); j++) {
                        images.add(imagesJsonArray.get(j));
                    }
                    parsedPlaylist.setImageUrls(images);
//                    Log.v("dispplay2?", (new JSONObject(parsedPlaylist.getMetadata().toString())).getString("display_name"));
                    res.add(parsedPlaylist);
                }


                if (rawPlaylist.getString("type").equals("youtube")) {
                    YouTubePlaylist parsedPlaylist = new YouTubePlaylist();

//                    Log.v("playlistact", ((JSONObject) playlists.get(i)).toString());

                    parsedPlaylist.setName(rawPlaylist.getString("name"));
                    parsedPlaylist.setId(rawPlaylist.getString("id"));

//                    Log.v("metadata", (rawPlaylist.getJSONObject("owner").toString()));


                    parsedPlaylist.setDescription(rawPlaylist.getString("description"));
                    parsedPlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
                    res.add(parsedPlaylist);
                    this.callbacks.playlistsCallback(res);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    public Object getUserDataObject() {
        return userDataObject;
    }

    public Object getPlaylistObject() {
        return playlistObject;
    }

    public void setCallType(ApiCallTypes callType) {
        this.callType = callType;
    }


    public ApiCallTypes getCallType() {
        return this.callType;
    }


}
