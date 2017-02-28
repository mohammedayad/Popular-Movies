package com.example.mohammedayad.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mohammed.ayad on 2/27/2017.
 */

public final class NetworkUtils {
    public static final String popularMoviesUrl="http://api.themoviedb.org/3/movie/popular";
    public static final String topRatedMoviesUrl="http://api.themoviedb.org/3//movie/top_rated";
    public static final String QUERY_PARAM="api_key";
    public static final String moviesApiKey="";

    public static URL buildUrl(String locationUrl) {
        Uri builtUri = Uri.parse(locationUrl).buildUpon()
                .appendQueryParameter(QUERY_PARAM, moviesApiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//        Log.v(TAG, "Built URI " + url);

        return url;
    }

}
