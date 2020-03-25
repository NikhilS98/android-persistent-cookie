package com.example.volleypersistantcookie;

import androidx.appcompat.app.AppCompatActivity;
;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    TextView loginResultTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieHandler.setDefault(new CookieManager(new PersistentCookieStore(this), CookiePolicy.ACCEPT_ALL));

        loginResultTxtView = findViewById(R.id.login_result_txtView);
        loginBtn = findViewById(R.id.login_btn);

        /*
        Doing this on app start up so that I can check I'm already logged in or not.
        If request returns OK then that means I have the cookie so proceed to home page.
        If it returns Unauthorized (401) then that means I don't have the cookie
        and must open get the login page. Checking the existence of cookie in sharedPreferences
        for this purpose is another way but it might cause some inconsistencies with the way
        session has been implemented at backend so it's better to do it this way.
        */
        getMyProfile();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login(){

        String url ="https://izak10-testing.eastus.cloudapp.azure.com/api/userlogin/login";

        /*
        If "remember" is not sent or "false" even then the cookie is persisted which is not the
        intended behaviour. So will have to put a check in the add method of CookieStore for the expiry
        of Cookie. But in mobile devices I think it's not a huge issue so let it persist.
        When someone clicks on Logout, the session is automatically deleted at server so not an issue.
         */
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "nikhil");
        params.put("password","nikhil");
        params.put("remember", "true");

        makeStringRequest(url, Request.Method.POST, params);

        /*JSONObject params = new JSONObject();
        try {
            params.put("username", "nikhil");
            params.put("password","nikhil");
            params.put("Content-Type", "application/json");
        } catch (JSONException e) {
            params = null;
            e.printStackTrace();
        }*/

    }

    public void getMyProfile() {
        String url = "https://izak10-testing.eastus.cloudapp.azure.com/api/userlogin/getmyprofile";
        makeStringRequest(url, Request.Method.GET, null);
    }

    public void makeStringRequest(String url, int requestMethod, final Map<String, String> params){

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest
                (requestMethod, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                loginResultTxtView.setText(response);
                Log.d("request", response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loginResultTxtView.setText(error.toString());
                Log.d("error", error == null ? "unknown error" : error.toString());
            }

        }){

            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(request);
    }

    //Json request is not working for some reason. I get BadRequest on login.
    //Maybe params are not being sent. Will have to look into it
    public void makeJsonRequest(String url, int requestMethod, JSONObject params){


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = null;

        jsonObjectRequest = new JsonObjectRequest
                (requestMethod, url, params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loginResultTxtView.setText(response.toString());
                        Log.d("request", response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginResultTxtView.setText(error.toString());
                        Log.d("error", error == null ? "unknown error" : error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }
}
