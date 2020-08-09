//package com.example.meld.services;
//
//import android.util.Log;
//
//import com.example.meld.models.ICallback;
//import com.example.meld.models.IPlaylist;
//import com.example.meld.models.IUser;
//import com.example.meld.models.YouTubePlaylist;
//import com.example.meld.utils.MapPlaylistParser;
//import com.google.api.services.youtube.model.Playlist;
//import com.google.api.services.youtube.model.PlaylistItem;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class YouTubeCallbacks implements ICallback {
//
//    public void userDataCallback(Object obj, IUser user) {
//    }
//
//
//
//    public void playlistsCallback(Object obj) {
////            playlistsObjectString = (JSONObject) obj;
//        Map< Playlist, List< PlaylistItem >>
//                youTubePlaylistMap = (Map<Playlist, List<PlaylistItem>>)obj;
//
//        List<IPlaylist> res = new ArrayList<>();
//        for (Playlist playlist : youTubePlaylistMap.keySet()) {
//            YouTubePlaylist youTubePlaylist = new YouTubePlaylist();
//
//            youTubePlaylist.setType(IPlaylist.PlaylistType.YOUTUBE);
//            youTubePlaylist.setName(playlist.getSnippet().getTitle());
//            youTubePlaylist.setId(playlist.getId());
//            youTubePlaylist.setDescription(playlist.getSnippet().getDescription());
//
//            res.add(youTubePlaylist);
//
//        }
////        return res;
//        // need a youtube playlist impl
//        Log.v("FROM PACT", obj.toString());
//    }
//
//
//}
