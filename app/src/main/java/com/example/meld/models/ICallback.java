package com.example.meld.models;

import org.json.JSONObject;

public interface ICallback {

    public void userDataCallback(Object obj, IUser user);

    public void playlistsCallback(Object obj);

//    public void homeFragmentCallback(Object obj);
}
