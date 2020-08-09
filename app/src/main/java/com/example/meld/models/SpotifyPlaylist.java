package com.example.meld.models;

import android.util.Log;
import android.widget.ListView;

import com.example.meld.models.IPlaylist;

import org.json.JSONObject;

import java.util.List;

public class SpotifyPlaylist implements IPlaylist{

    public SpotifyPlaylist() {
    }

    Boolean visibility;
    String name;
    String description;
    String id;
    List<String> tracks;
    Object metadata;
    List<Object> imageUrls;
    PlaylistType type;

    @Override
    public Boolean isPublic() {
        return this.visibility;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        if (this.name != null && this.name.length() > 0) {
            return this.name;
        }
        else {
            try {
                return new JSONObject(this.getMetadata().toString()).getString("display_name");
            }
            catch (Exception e) {
                return "Could not fetch playlist name";
            }
        }
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Object getMetadata() {
        return this.metadata;
    }

    @Override
    public List<String> getTracks() {
        return this.tracks;
    }

    @Override
    public List<Object> getImageUrls() {
        return this.imageUrls;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PlaylistType type) {
        this.type = type;
    }

    public PlaylistType getType() {
       return this.type;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    public void setMetadata(Object metadata) {
//        Log.v("meta???", metadata.toString());

        this.metadata = metadata;
//        Log.v("instancemeta???", this.metadata.toString());

    }

    public void setImageUrls(List<Object> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        String displayName = this.getName();
        return String.format("%s    |     %s", displayName, this.getType().toString());
    }
}
