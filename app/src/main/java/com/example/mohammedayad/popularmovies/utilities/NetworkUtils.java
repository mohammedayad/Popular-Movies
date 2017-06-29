package com.example.mohammedayad.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import com.example.mohammedayad.popularmovies.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mohammed.ayad on 2/27/2017.
 */

public final class NetworkUtils {
    public static final String DOMAIN_URL="http://api.themoviedb.org/3/movie/";
    public static final String popularMoviesUrl=DOMAIN_URL+"popular";
    public static final String topRatedMoviesUrl=DOMAIN_URL+"top_rated";
    public static final String QUERY_PARAM="api_key";
    public static final String moviesApiKey="a1799d5a26903b406f268fd4e32ea518";
    public static final String TAG="Popular Movies";

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

        Log.v("++++++++++", "Built URI " + url);

        return url;
    }
    public static String buildPosterUrl(String imageName,String imageResolution){//w185
        Uri.Builder builder=new Uri.Builder();
        builder.scheme("http").path("//image.tmdb.org/t/p/"+imageResolution+"//"+imageName);
        Uri posterAddress= builder.build();
        Log.i("+++++++++++++++++++++", posterAddress.toString());
        return posterAddress.toString();
    }

    public static boolean isNetworkConnected(Context context) {


        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();




    }

}
