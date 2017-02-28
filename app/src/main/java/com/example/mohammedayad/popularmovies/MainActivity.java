package com.example.mohammedayad.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPopularMoviesAdapter=new PopularMoviesAdapter(this);
        loadPopularMoviesData();
    }

    private void loadPopularMoviesData() {
        showMoviesDataView();
        mLoadingIndicator.setVisibility(View.VISIBLE);
        String moviesUrl=NetworkUtils.buildUrl(NetworkUtils.popularMoviesUrl).toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,moviesUrl, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            JSONObject results=response.getJSONObject("results");
                            JSONArray results= response.getJSONArray("results");
                            movies=new ArrayList<>();
                            for(int i=0;i<results.length();i++){
                                Movie movie=new Movie();
                                movie.setPosterPath(results.getString(0));
                                movie.setAdult(Boolean.parseBoolean(results.getString(1)));
                                movie.setOverview(results.getString(2));
                                movie.setReleaseDate(results.getString(3));
                                movie.setGenreIds(results.getString(4));
                                movie.setId(results.getString(5));
                                movie.setOriginalTitle(results.getString(6));
                                movie.setOriginalLanguage(results.getString(7));
                                movie.setTitle(results.getString(8));
                                movie.setBackdropPath(results.getString(9));
                                movie.setPopularity(results.getString(10));
                                movie.setVoteCount(results.getString(11));
                                movie.setVideo(Boolean.parseBoolean(results.getString(12)));
                                movie.setVoteAverage(results.getString(13));
                                movies.add(movie);
                            }

                            Log.i("+++++++++++++++++++++", movies.get(0).getPosterPath());
                            mLoadingIndicator.setVisibility(View.INVISIBLE);
                            if (movies != null) {
                                showMoviesDataView();
                                mPopularMoviesAdapter.setPopularMoviesData(movies);
                            } else {
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
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie selectedMovie) {

    }
}
