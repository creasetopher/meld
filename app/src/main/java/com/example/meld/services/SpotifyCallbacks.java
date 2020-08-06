package com.example.meld.services;

import android.util.Log;
import android.view.View;

import com.example.meld.models.ICallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

//public class SpotifyCallbacks implements ICallback {
//
//    // make the playlist button visible here, after user data and user id is fetched
//    public void userDataCallback(Object obj) {
//
//        userDataObject = (JSONObject)obj;
////            SpotifyUser spotifyUser = gson.fromJson(userDataObject.toString(), SpotifyUser.class);
//        Log.v("USERDATA!", userDataObject.toString());
//
//        getPlaylistsButton.setVisibility(View.VISIBLE);
//    }
//
//    public void playlistsCallback(Object obj) {
//
//        playlistsObject = (JSONObject)obj;
//
//        try {
//            JSONArray playlists = playlistsObject.getJSONArray("items");
//
//            for (int i = 0; i < playlists.length(); i++) {
//                JSONObject rawPlaylist = (JSONObject) playlists.get(i);
//                rawPlaylist.put("playlistType", "spotify");
//                allPlaylistsAsJSONs.add(rawPlaylist);
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
////            try {
////                JSONObject spotifyPLaylistJSONobj = (JSONObject)obj;
////                spotifyPLaylistJSONobj.put("type", "spotify");
////                allPlaylists.addAll(JsonPlaylistParser.toArrayList(spotifyPLaylistJSONobj));
////            }
////
////            catch (Exception e) {
////                e.printStackTrace();
////            }
//
//    }
//
//}
