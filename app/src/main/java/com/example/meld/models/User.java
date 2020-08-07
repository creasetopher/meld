package com.example.meld.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class User implements IUser, Parcelable {

    private String displayName;

    private JSONObject sptifyExternalUrls;
    private JSONObject spotifyFollowers;



    private String id;
    private JSONArray spotifyImages;
    private String type;
    private String username;
    private String spotifyAccessToken;
    private String googleAccessToken;
    private String selectedGoogleAccountName;

    private GoogleAccountCredential googleAccountCredential;
    private JSONObject spotifyUserDataObject;
    private String spotifyUsername;

    private static User instance = null;


    // make singleton????
    private User() {
    }


    @Override
    public String getSpotifyUsername() {
        return spotifyUsername;
    }

    @Override
    public void setSpotifyUsername(String username) {
        this.spotifyUsername = username;
    }


    @Override
    public String getId() {
        return this.id;
    }


    public String getDisplayName() {
        return this.getUsername();
    }

    @Override
    public String getUsername() {
        return this.displayName;
    }


    public String getType() {
        return this.type;
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getName() {
        return null;
    }

    @Override
    public String getSpotifyAccessToken() {
        return this.spotifyAccessToken;
    }

    @Override
    public void setSpotifyAccessToken(String accessToken) {
        this.spotifyAccessToken = accessToken;
    }

    @Override
    public JSONObject getSpotifyUserDataObject() {
        return this.spotifyUserDataObject;
    }

    @Override
    public void setSpotifyUserDataObject(JSONObject userDataObject) {
        this.spotifyUserDataObject = userDataObject;
    }

    @Override
    public GoogleAccountCredential getGoogleUserObject() {
        return this.googleAccountCredential;
    }

    @Override
    public void setGoogleUserObject(GoogleAccountCredential googleUserObject) {
        this.googleAccountCredential = googleUserObject;
    }

    @Override
    public void setSelectedGoogleAccountName(String accountName) {
        this.selectedGoogleAccountName = accountName;
    }

    @Override
    public String getSelectedGoogleAccountName() {
        return this.selectedGoogleAccountName;
    }

    public static User getInstance() {
        if (instance  == null) {
            instance = new User();
            return instance;
        }

        else {
            return instance;
        }
    }


    public User(Parcel in){

        displayName = in.readString();
        id = in.readString();
        username = in.readString();
        spotifyAccessToken = in.readString();
        selectedGoogleAccountName = in.readString();

    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(spotifyAccessToken);
        dest.writeString(selectedGoogleAccountName);
    }




}



