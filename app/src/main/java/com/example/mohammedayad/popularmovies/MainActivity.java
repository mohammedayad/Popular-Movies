package com.example.mohammedayad.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mohammedayad.popularmovies.pojos.Movie;
import com.example.mohammedayad.popularmovies.utilities.NetworkUtils;
import com.example.mohammedayad.popularmovies.volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.popularMoviesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_PopularMovies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
//        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter=new PopularMoviesAdapter(this,getApplicationContext());
        mRecyclerView.setAdapter(mPopularMoviesAdapter);
//        String den=getDeviceResolution();
//        Toast.makeText(this, "density "+den, Toast.LENGTH_LONG).show();
//        determineScreenSize();
//        default url to fetch the movies
        loadPopularMoviesData(NetworkUtils.popularMoviesUrl);
    }

    private void loadPopularMoviesData(String moviesOrderUrl) {
        showMoviesDataView();
        mLoadingIndicator.setVisibility(View.VISIBLE);
        String moviesUrl=NetworkUtils.buildUrl(moviesOrderUrl).toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,moviesUrl, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            JSONObject results=response.getJSONObject("results");
                            JSONArray results= response.getJSONArray("results");
                            movies=new ArrayList<>();
                            for(int i=0;i<results.length();i++){
                                JSONObject movieObject=results.getJSONObject(i);
                                Movie movie=new Movie();
                                movie.setPosterPath(movieObject.getString("poster_path"));
                                movie.setAdult(Boolean.parseBoolean(movieObject.getString("adult")));
                                movie.setOverview(movieObject.getString("overview"));
                                movie.setReleaseDate(movieObject.getString("release_date"));
                                movie.setGenreIds(movieObject.getString("genre_ids"));
                                movie.setId(movieObject.getString("id"));
                                movie.setOriginalTitle(movieObject.getString("original_title"));
                                movie.setOriginalLanguage(movieObject.getString("original_language"));
                                movie.setTitle(movieObject.getString("title"));
                                movie.setBackdropPath(movieObject.getString("backdrop_path"));
                                movie.setPopularity(movieObject.getString("popularity"));
                                movie.setVoteCount(movieObject.getString("vote_count"));
                                movie.setVideo(Boolean.parseBoolean(movieObject.getString("video")));
                                movie.setVoteAverage(movieObject.getString("vote_average"));
                                movies.add(movie);
                                Log.i("+++++++++++++++++++++", movies.get(i).getPosterPath());
                            }

                            Log.i("+++++++++++++++++++++", movies.get(0).getPosterPath());
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            if (movies != null) {
                                showMoviesDataView();
                                mPopularMoviesAdapter.setPopularMoviesData(movies);
                            } else {
                                Log.i("+++++++++++++++++++++","no movies here");
                                showErrorMessage();
                            }
//                            Toast.makeText(getApplicationContext(),organizationObject.toString(),Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        showErrorMessage();

                    }
                });

// Access the RequestQueue through your singleton class.
//        jsObjRequest.setTag(Urls.TAG);
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    private void showMoviesDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie selectedMovie) {
        Intent intent=new Intent(this,MovieDetails.class);
        intent.putExtra("movie", selectedMovie);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_order_by,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMovieOrder=item.getItemId();
        switch (selectedMovieOrder){
            case R.id.mostPopular:
                mPopularMoviesAdapter.setPopularMoviesData(null);
                loadPopularMoviesData(NetworkUtils.popularMoviesUrl);
                return true;
            case R.id.topRated:
                mPopularMoviesAdapter.setPopularMoviesData(null);
                loadPopularMoviesData(NetworkUtils.topRatedMoviesUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void determineScreenSize(){
        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show();
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG).show();
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Toast.makeText(this, "Small sized screen", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Screen size is neither large, normal or small", Toast.LENGTH_LONG).show();
        }

    }


    private String getDeviceResolution()
    {
        int density = getResources().getDisplayMetrics().densityDpi;
        switch (density)
        {
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TV";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "XXXHDPI";
            default:
                return "Unknown";
        }
    }
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
