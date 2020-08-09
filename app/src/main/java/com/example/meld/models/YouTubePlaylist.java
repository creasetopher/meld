package com.example.meld.models;

import com.example.meld.services.YouTubeService;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class YouTubePlaylist implements IPlaylist {
    private String id;
    private String name;
    private String description;
    private PlaylistType type;
    Boolean visibility;
    List<String> tracks;

    public YouTubePlaylist() {

    }

    public void setVisibility(String visibility) {
        this.visibility = Boolean.parseBoolean(visibility);
    }



    @Override
    public Boolean isPublic() {
        return this.visibility;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<String> getTracks() {
        return this.tracks;
    }

    @Override
    public void setTracks(List<String> tracks) {
        this.tracks = tracks;
    }

    @Override
    public Object getMetadata() {
        return null;
    }

    @Override
    public PlaylistType getType() {
        return this.type;
    }

    public void setType(PlaylistType type) {
        this.type = type;
    }

    @Override
    public List<Object> getImageUrls() {
        return null;
    }

    @Override
    public String toString() {
        String displayName = this.getName();
        return String.format("%s    |     %s", displayName, this.getType().toString());
    }
}
