package com.android.asynchttpclient;

/**
 * Created by Song on 2015/8/14.
 */
public class AndroidClient {
    public static final String VOLLEY_TAG = "VOLLEY_TAG";
    public static final String BASE_URL = "http://10.0.3.2:8087/androidcloud/";


    public static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
