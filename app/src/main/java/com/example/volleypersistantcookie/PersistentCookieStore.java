package com.example.volleypersistantcookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class PersistentCookieStore implements CookieStore {

    private SharedPreferences authCookiePrefs;
    private SharedPreferences.Editor prefsEditor;

    public PersistentCookieStore(Context context){

        authCookiePrefs = context.getSharedPreferences("authCookiePrefs", context.MODE_PRIVATE);
        prefsEditor = authCookiePrefs.edit();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        Gson gson = new Gson();
        String json = gson.toJson(cookie);

        //getHost returns the domain
        //Log.d("host", uri.getHost());
        prefsEditor.putString(uri.getHost(), json);
        prefsEditor.apply();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        Gson gson = new Gson();
        //Log.d("host", uri.getHost());

        String json = authCookiePrefs.getString(uri.getHost(), "");
        HttpCookie authCookie = gson.fromJson(json, HttpCookie.class);

        List<HttpCookie> cookies = new ArrayList<>();

       //Read the docs. This method is expected to return an empty list if no cookie found
        if(authCookie != null){
            cookies.add(authCookie);
        }

        return cookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        return null;
    }

    @Override
    public List<URI> getURIs() {
        return null;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return false;
    }

    @Override
    public boolean removeAll() {
        return false;
    }
}
