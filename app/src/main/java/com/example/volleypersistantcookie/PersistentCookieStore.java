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

        //getHost returns the domain so in this case = izak10-testing.eastus.cloudapp.azure.com
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

        /*
        apparently there's a bug in the CookieManager class so that if a cookie is not found
        and I return null it causes a NullPointerException. If I initialize and return an empty
        list that also causes an exception which is why I'm returning a dummy cookie
        so that it doesn't crash.
         */
        if(authCookie == null)
            authCookie = new HttpCookie("notFound", "notFound");

        cookies.add(authCookie);
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
