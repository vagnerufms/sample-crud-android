package com.meng.android.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHandler {

    private String url;

    public HttpHandler(String url) {
        this.url = url;
    }

    public String sendRequest() {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            Log.v("MainActivity", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return sb.toString();
    }
}