package com.example.meld.models;

import com.example.meld.services.YouTubeService;

import java.util.List;

public class YouTubePlaylist implements IPlaylist {
    private String id;
    private String name;
    private String description;
    private PlaylistType type;

    public YouTubePlaylist() {

    }


    @Override
    public Boolean isPublic() {
        return null;
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
    public Object getTracks() {
        return null;
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
}
