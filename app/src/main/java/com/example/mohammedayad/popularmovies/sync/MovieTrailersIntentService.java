package com.example.mohammedayad.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mohammedayad.popularmovies.MovieDetails;
import com.example.mohammedayad.popularmovies.pojos.Movie;
import com.example.mohammedayad.popularmovies.utilities.NetworkUtils;
import com.example.mohammedayad.popularmovies.volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by mohammed.ayad on 6/23/2017.
 */

public class MovieTrailersIntentService extends IntentService {
    public static ArrayList<String> moviesTrailersKeys;
    public static List<HashMap<String,String>> movieReviews;
    public static boolean hasConnection=true;


    public MovieTrailersIntentService() {
        super("MovieTrailersIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action=intent.getAction();
        Log.i("*******", "load trailers");
        if (NetworkUtils.isNetworkConnected(this)) {
            if (action.equalsIgnoreCase(MovieDetails.ACTION_LOAD_MOVIES_TRAILERS)) {
                Log.i("*******", "load trailers inside action");
                String movieId = intent.getStringExtra("movieId");
                loadMovieTrailers(movieId);
                loadMovieReviews(movieId);
            }
        }else{
            hasConnection=false;
        }

    }

    private void loadMovieTrailers(String movieId){
        Uri movieTrailerUrl= Uri.parse(NetworkUtils.DOMAIN_URL).buildUpon().appendPath(movieId).appendPath("videos").appendQueryParameter(NetworkUtils.QUERY_PARAM,NetworkUtils.moviesApiKey).build();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,movieTrailerUrl.toString(), null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results= response.getJSONArray("results");
                            moviesTrailersKeys=new ArrayList<>();
                            for(int i=0;i<results.length();i++){
                                JSONObject movieObject=results.getJSONObject(i);
                                String trailerKey=movieObject.getString("key");
                                moviesTrailersKeys.add(trailerKey);

                                Log.i("******", moviesTrailersKeys.toString());
                            }

                        } catch (JSONException e) {
                            Log.i("????", "no trailers found");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("????", "no internet access");

                    }
                });

// Access the RequestQueue through your singleton class.
//        jsObjRequest.setTag(Urls.TAG);
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        Log.i("*******", "finish loading trailers");


    }

    private void loadMovieReviews(String movieId){
        Uri movieTrailerUrl= Uri.parse(NetworkUtils.DOMAIN_URL).buildUpon().appendPath(movieId).appendPath("reviews").appendQueryParameter(NetworkUtils.QUERY_PARAM,NetworkUtils.moviesApiKey).build();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,movieTrailerUrl.toString(), null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results= response.getJSONArray("results");
                            movieReviews = new ArrayList<HashMap<String,String>>();;
                            for(int i=0;i<results.length();i++){
                                LinkedHashMap<String,String> movieReview=new LinkedHashMap<>();
                                JSONObject movieObject=results.getJSONObject(i);
                                String author=movieObject.getString("author");
                                String content=movieObject.getString("content");
                                movieReview.put("author",author);
                                movieReview.put("content",content);
                                movieReviews.add(movieReview);

                                Log.i("******", movieReviews.toString());
                            }

                        } catch (JSONException e) {
                            Log.i("****", "no trailers found");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("????", "no internet access");

                    }
                });

// Access the RequestQueue through your singleton class.
//        jsObjRequest.setTag(Urls.TAG);
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        Log.i("*******", "finish loading movie reviews");


    }
}
