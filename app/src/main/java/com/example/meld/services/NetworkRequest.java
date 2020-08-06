package com.example.meld.services;


import android.util.Log;

import com.android.volley.RequestQueue;
import com.example.meld.models.IUser;

import org.json.JSONException;

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


}
