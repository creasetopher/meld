package com.example.meld.models;


import java.util.List;


public interface IPlaylist {

    public enum PlaylistType {
        SPOTIFY("Spotify"), YOUTUBE("YouTube");

        private String string;

        PlaylistType(String string) {
            this.string =  string;
        }

        @Override
        public String toString() {
            return this.string;
        }
    }

    public Boolean isPublic();

    public String getId();

    public String getName();

    public String getDescription();

    public Object getTracks();

    public Object getMetadata();

    public PlaylistType getType();

    public List<Object> getImageUrls();






}
