package com.example.meld.utils;

import com.example.meld.models.IPlaylist;
import com.example.meld.models.YouTubePlaylist;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapPlaylistParser {


//    method returning list of playlists as JSONObject

    public static List<IPlaylist> toArrayList(Map<Playlist, List<PlaylistItem>> playlistMap) {
        List<IPlaylist> res = new ArrayList<>();
        for (Playlist playlist : playlistMap.keySet()) {
            YouTubePlaylist youTubePlaylist = new YouTubePlaylist();

            youTubePlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
            youTubePlaylist.setName(playlist.getSnippet().getTitle());
            youTubePlaylist.setId(playlist.getId());
            youTubePlaylist.setDescription(playlist.getSnippet().getDescription());

            res.add(youTubePlaylist);

        }
    return res;
    }


    public static List<JSONObject> toJsonObjectArray(Map<Playlist, List<PlaylistItem>> playlistMap) {
        List<JSONObject> res = new ArrayList<>();

        for (Playlist playlist : playlistMap.keySet()) {
            JSONObject jsonObject = new JSONObject();

            try {

                jsonObject.put(
                        "name",
                        playlist.getSnippet().getTitle()
                );

                JSONArray tracks = new JSONArray();

                for (PlaylistItem playlistItem : playlistMap.get(playlist)) {
                    tracks.put(playlistItem.getSnippet().getTitle());
                }

                jsonObject.put("tracks", tracks);

                jsonObject.put("id", playlist.getId());
                jsonObject.put("description", playlist.getSnippet().getDescription());

                res.add(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

}
