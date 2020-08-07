package com.example.meld.models;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.json.JSONObject;

public interface IUser {
    public String getId();

    public String getName();

    public String getUsername();

    public String getSpotifyAccessToken();

    public void setSpotifyAccessToken(String accessToken);

    public JSONObject getSpotifyUserDataObject();

    public String getSpotifyUsername();

    public void setSpotifyUsername(String username);

    public void setSpotifyUserDataObject(JSONObject userDataObject);

    public GoogleAccountCredential getGoogleUserObject();

    public void setGoogleUserObject(GoogleAccountCredential googleUserObject);

    public void setSelectedGoogleAccountName(String accountName);

    public String getSelectedGoogleAccountName();

}
