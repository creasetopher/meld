package com.example.meld.utils;


import android.util.Log;

import com.example.meld.models.IPlaylist;
import com.example.meld.models.SpotifyPlaylist;
import com.example.meld.models.YouTubePlaylist;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPlaylistParser {

    public static List<IPlaylist> toArrayList(JSONObject playlistsObjects) {
        List<IPlaylist> res = new ArrayList<>();
        //TODO: use gson to parse eventually
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();

        if (playlistsObjects != null) {

            try {
                JSONArray playlists = playlistsObjects.getJSONArray("items");

                for (int i = 0; i < playlists.length(); i++) {
                    JSONObject rawPlaylist = (JSONObject) playlists.get(i);

//                if (rawPlaylist.getString("type").equals("spotify")) {

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


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;

    }


    public static void addTracks(IPlaylist playlist, JSONObject trackResponseObject) {

        if(playlist.getName().equals("chris")){
            Log.v("atrackresp", trackResponseObject.toString());

        }

        List<String> trackTitles = new ArrayList<>();

        try {
            JSONArray tracksJsonArray = trackResponseObject.getJSONArray("items");

            for(int i = 0; i < tracksJsonArray.length(); i++) {
                if( i == 0) {
                    Log.v("abovetrack", tracksJsonArray.getJSONObject(i).toString());
                }
                JSONObject trackObject = tracksJsonArray.getJSONObject(i).getJSONObject("track");

                JSONObject jsonObjWithArtistInfo = (JSONObject)trackObject.getJSONObject("album").getJSONArray("artists").get(0);
//
                String artistName = jsonObjWithArtistInfo.getString("name");

                String nameFromJson = trackObject.getString("name");

//                Log.v("artist1234", artistName);
//                Log.v("name1234", nameFromJson);

                String trackTitle = String.format("%s - %s", artistName, nameFromJson);

                trackTitles.add(trackTitle);

            }

            if(trackTitles.size() > 0) {
                playlist.setTracks(trackTitles);
            }

//            Log.v("itemsasArray", tracksJsonArray.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    public static void addTracksAllPlaylists(List<IPlaylist> allPlaylists, List<JSONObject> trackResponseObjects) {
        Log.v("allplaylists", allPlaylists.toString());
        Log.v("playlistid", allPlaylists.get(0).getId());


        Map<String, IPlaylist> playlistMap = new HashMap<>();

//        allPlaylists.stream().map( p -> playlistMap.put(p.getId(), p));


        for(IPlaylist playlist : allPlaylists) {
            playlistMap.putIfAbsent(playlist.getId(), playlist);
        }


        Map<String, JSONObject> trackMap = new HashMap<>();


        for(JSONObject trackObj : trackResponseObjects) {


            try {
                String reqUrl = trackObj.getString("href");
                Log.v("requrl", reqUrl);

                String playlistId = reqUrl.substring(
                        reqUrl.indexOf("playlists") + 10,
                        reqUrl.indexOf('/',
                                reqUrl.indexOf("playlists") + 10));

                trackMap.put(playlistId, trackObj);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for(String playlistId : playlistMap.keySet()) {
            IPlaylist thePlaylist = playlistMap.get(playlistId);
            JSONObject trackObj = trackMap.getOrDefault(playlistId, null);

            if(trackObj != null) {
                Log.v("matchingtrack!", trackObj.toString());
                addTracks(thePlaylist, trackObj);
            }



        }

//
//
//        List<String> trackTitles = new ArrayList<>();
//
//        try {
//            JSONArray tracksJsonArray = trackResponseObject.getJSONArray("items");
//
//            for(int i = 0; i < tracksJsonArray.length(); i++) {
//                if( i == 0) {
//                    Log.v("abovetrack", tracksJsonArray.getJSONObject(i).toString());
//                }
//                JSONObject trackObject = tracksJsonArray.getJSONObject(i).getJSONObject("track");
//
//                JSONObject jsonObjWithArtistInfo = (JSONObject)trackObject.getJSONObject("album").getJSONArray("artists").get(0);
////
//                String artistName = jsonObjWithArtistInfo.getString("name");
//
//                String nameFromJson = trackObject.getString("name");
//
////                Log.v("artist1234", artistName);
////                Log.v("name1234", nameFromJson);
//
//                String trackTitle = String.format("%s - %s", artistName, nameFromJson);
//
//                trackTitles.add(trackTitle);
//
//            }
//
//            if(trackTitles.size() > 0) {
//                playlist.setTracks(trackTitles);
//            }
//
////            Log.v("itemsasArray", tracksJsonArray.toString());
//        } catch (JSONException e) {
//
//            e.printStackTrace();
//        }
    }



//    public static List<IPlaylist> toPlaylistArray(JSONObject playlistsObjects) {
//        List<IPlaylist> res = new ArrayList<>();
//        //TODO: use gson to parse eventually
////        GsonBuilder builder = new GsonBuilder();
////        Gson gson = builder.create();
//
//        try {
//            JSONArray playlists = playlistsObjects.getJSONArray("items");
//
//            for (int i = 0; i < playlists.length(); i++) {
//                JSONObject rawPlaylist = (JSONObject) playlists.get(i);
//
//                if (rawPlaylist.getString("type").equals("spotify")) {
//
//                    SpotifyPlaylist parsedPlaylist = new SpotifyPlaylist();
//
//                    Log.v("playlistact", ((JSONObject) playlists.get(i)).toString());
//
//                    parsedPlaylist.setName(rawPlaylist.getString("name"));
//                    parsedPlaylist.setId(rawPlaylist.getString("id"));
//
//                    parsedPlaylist.setMetadata(rawPlaylist.getJSONObject("owner"));
//                    Log.v("metadata", (rawPlaylist.getJSONObject("owner").toString()));
//
//
//                    parsedPlaylist.setDescription(rawPlaylist.getString("description"));
//                    parsedPlaylist.setVisibility(rawPlaylist.getBoolean("public"));
//                    parsedPlaylist.setType(IPlaylist.PlaylistType.SPOTIFY);
//
//                    JSONArray imagesJsonArray = rawPlaylist.getJSONArray("images");
//                    List<Object> images = new ArrayList<>();
//
//                    for (int j = 0; j < imagesJsonArray.length(); j++) {
//                        images.add(imagesJsonArray.get(j));
//                    }
//                    parsedPlaylist.setImageUrls(images);
//                    Log.v("dispplay2?", (new JSONObject(parsedPlaylist.getMetadata().toString())).getString("display_name"));
//                    res.add(parsedPlaylist);
//                }
//
//
//                if (rawPlaylist.getString("type").equals("youtube")) {
//                    YouTubePlaylist parsedPlaylist = new YouTubePlaylist();
//
//                    Log.v("playlistact", ((JSONObject) playlists.get(i)).toString());
//
//                    parsedPlaylist.setName(rawPlaylist.getString("name"));
//                    parsedPlaylist.setId(rawPlaylist.getString("id"));
//
//                    Log.v("metadata", (rawPlaylist.getJSONObject("owner").toString()));
//
//
//                    parsedPlaylist.setDescription(rawPlaylist.getString("description"));
//                    parsedPlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
//                    res.add(parsedPlaylist);
//
//                }
//            }
//
//
//        }
//
//
//    }
}

//{"collaborative":false,
//        "description":"",
//        "external_urls":{"spotify":"https:\/\/open.spotify.com\/playlist\/1BKpGZYuJgjjiT3m5dFakv"},
//        "href":"https:\/\/api.spotify.com\/v1\/playlists\/1BKpGZYuJgjjiT3m5dFakv",
//        "id":"1BKpGZYuJgjjiT3m5dFakv",
//        "images":[{"height":640,"url":"https:\/\/mosaic.scdn.co\/640\/ab67616d0000b2733f774ef4a81a1886f21a551cab67616d0000b273b9895f67935f0d041e13698aab67616d0000b273e3a46c36d91382bd86ad0c65ab67616d0000b273e6095c382c2853667c1623eb","width":640},{"height":300,"url":"https:\/\/mosaic.scdn.co\/300\/ab67616d0000b2733f774ef4a81a1886f21a551cab67616d0000b273b9895f67935f0d041e13698aab67616d0000b273e3a46c36d91382bd86ad0c65ab67616d0000b273e6095c382c2853667c1623eb","width":300},{"height":60,"url":"https:\/\/mosaic.scdn.co\/60\/ab67616d0000b2733f774ef4a81a1886f21a551cab67616d0000b273b9895f67935f0d041e13698aab67616d0000b273e3a46c36d91382bd86ad0c65ab67616d0000b273e6095c382c2853667c1623eb","width":60}],
//        "name":"",
//        "owner":{"display_name":"SoundtrackStunners","external_urls":{"spotify":"https:\/\/open.spotify.com\/user\/54rxacutbil4ztzkem79wn2au"},"href":"https:\/\/api.spotify.com\/v1\/users\/54rxacutbil4ztzkem79wn2au","id":"54rxacutbil4ztzkem79wn2au","type":"user","uri":"spotify:user:54rxacutbil4ztzkem79wn2au"},
//        "primary_color":null,
//        "public":false,
//        "snapshot_id":"OTUsZDk4NGJhMzFkNjI5OTJiMWM1MDNmOThkOWU5NWEyNDhiMzQ4ODg2Mg==",
//        "tracks":{"href":"https:\/\/api.spotify.com\/v1\/playlists\/1BKpGZYuJgjjiT3m5dFakv\/tracks","total":64},
//        "type":"playlist","uri":"spotify:playlist:1BKpGZYuJgjjiT3m5dFakv"}
