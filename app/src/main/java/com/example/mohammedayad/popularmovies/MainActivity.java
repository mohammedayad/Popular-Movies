package com.example.mohammedayad.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import com.example.mohammedayad.popularmovies.data.MovieContract;
import com.example.mohammedayad.popularmovies.pojos.Movie;
import com.example.mohammedayad.popularmovies.sync.MovieTrailersIntentService;
import com.example.mohammedayad.popularmovies.utilities.NetworkUtils;
import com.example.mohammedayad.popularmovies.volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.example.mohammedayad.popularmovies.data.MovieContract.MovieEntry;

public class MainActivity extends AppCompatActivity implements PopularMoviesAdapter.popularMoviesAdapterOnClickHandler,LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private PopularMoviesAdapter mPopularMoviesAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private ArrayList<Movie> movies;
    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;
    private static final int TASK_LOADER_FAVORITE_MOVIES_ID = 1;
    private static final int TASK_LOADER_MOVIES_ID = 2;
    private int currentLoaderId=TASK_LOADER_MOVIES_ID;
    private int currentMovieCategory=1;//mostPopular
    private boolean isFirstLoad=true;

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
        Log.i("########", "onCreate");
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
//            loadPopularMoviesData(NetworkUtils.popularMoviesUrl);
            loadMoviesDataFromServer();
        }else{
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
            loadDataFromDataBase();

        }
    }

    private void loadPopularMoviesData(String moviesOrderUrl) {
        Log.i("########", "loadPopularMoviesData");
        showMoviesDataView();
        if (isFirstLoad) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            isFirstLoad=false;
        }
        String moviesUrl=NetworkUtils.buildUrl(moviesOrderUrl).toString();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,moviesUrl, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("########", "onResponse");
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
                                int smallestWidth=getSmallestWidth();
                                String moviePosterFullPath=null;
                                if(smallestWidth>=800){//tablet
                                    moviePosterFullPath = NetworkUtils.buildPosterUrl(movie.getPosterPath(), "w500");

                                }else {
                                     moviePosterFullPath = NetworkUtils.buildPosterUrl(movie.getPosterPath(), "w185");
                                }
                                movie.setPosterFullPath(moviePosterFullPath);
                                movies.add(movie);
                                Log.i("+++++++++++++++++++++", movies.get(i).getPosterPath());
                            }
                                mLoadingIndicator.setVisibility(View.INVISIBLE);
                                if (movies != null) {
                                    Log.i("movies", movies.toString());
                                    addNewMovieToDataBase(movies);
                                    showMoviesDataView();
                                    mPopularMoviesAdapter.setPopularMoviesData(movies);
                                } else {
                                    Log.i("+++++++++++++++++++++", "no movies here");
//                                showErrorMessage();
//                                    loadDataFromDataBase();
                                }
//                            Toast.makeText(getApplicationContext(),organizationObject.toString(),Toast.LENGTH_LONG).show();

                            } catch (JSONException e) {
                                Log.i("????", "no movies here");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.i("????", "no internet access");
                            showErrorMessage();
//                            mLoadingIndicator.setVisibility(View.INVISIBLE);
//                            loadDataFromDataBase();

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
        Intent movieTrailers=new Intent(this, MovieTrailersIntentService.class);
        movieTrailers.setAction(MovieDetails.ACTION_LOAD_MOVIES_TRAILERS);
        movieTrailers.putExtra("movieId",selectedMovie.getId());
        startService(movieTrailers);
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
                this.currentMovieCategory=1;
                mPopularMoviesAdapter.setPopularMoviesData(null);
                loadPopularMoviesData(NetworkUtils.popularMoviesUrl);
                return true;
            case R.id.topRated:
                this.currentMovieCategory=2;
                mPopularMoviesAdapter.setPopularMoviesData(null);
                loadPopularMoviesData(NetworkUtils.topRatedMoviesUrl);
                return true;
            case R.id.favoriteMovies:
                this.currentMovieCategory=3;
                mPopularMoviesAdapter.setPopularMoviesData(null);
                loadFavoriteMoviesFromDataBase();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mMovieData = null;


            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMovieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data
                Cursor returnedMovies=null;
                try {
                    if(id==TASK_LOADER_ID) {
                        returnedMovies= getContentResolver().query(MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    }else if(id==TASK_LOADER_FAVORITE_MOVIES_ID){
                        Uri favoriteMovies=MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath("1").build();
                        returnedMovies= getContentResolver().query(favoriteMovies,
                                null,
                                null,
                                null,
                                null);

                    }else if(id==TASK_LOADER_MOVIES_ID){
                        if(currentMovieCategory==1) {
                            loadPopularMoviesData(NetworkUtils.popularMoviesUrl);
                        }else if(currentMovieCategory==2){
                            loadPopularMoviesData(NetworkUtils.topRatedMoviesUrl);
                        }

                    }
                    return returnedMovies;

                } catch (Exception e) {
                    Log.e(NetworkUtils.TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
//        mAdapter.swapCursor(data);

        Log.i("########", "Loader Id " + loader.getId());
        if (data!=null&&loader.getId()!=TASK_LOADER_MOVIES_ID) {
            Log.i("########", "cached movies count " + data.getCount());
            showMoviesDataView();
            try {
                movies = new ArrayList<>();
                if (data.getCount() > 0) {
                    while (data.moveToNext()) {
                        int idIndex = data.getColumnIndex(MovieEntry._ID);
                        int movieTitle = data.getColumnIndex(MovieEntry.COLUMN_ORIGINAL_TITLE);
                        int releaseDate = data.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE);
                        int vote = data.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE);
                        int overview = data.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
                        int movieId = data.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
                        int moviePoster = data.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
                        int isFavoriteMovie = data.getColumnIndex(MovieEntry.COLUMN_IS_FAVORITE);
                        // Determine the values of the wanted data
                        final int id = data.getInt(idIndex);
                        String movieName = data.getString(movieTitle);
                        String movieReleaseDate = data.getString(releaseDate);
                        String movieRate = data.getString(vote);
                        String movieOverview = data.getString(overview);
                        String movieNum = data.getString(movieId);
                        String posterImage = data.getString(moviePoster);
                        int favoriteMovie = data.getInt(isFavoriteMovie);
                        Movie movie = new Movie();
                        movie.setOverview(movieOverview);
                        movie.setReleaseDate(movieReleaseDate);
                        movie.setId(movieNum);
                        movie.setOriginalTitle(movieName);
                        movie.setVoteAverage(movieRate);
                        movie.setPosterFullPath(posterImage);
                        movie.setIsFavoriteMovie(favoriteMovie);
                        movies.add(movie);


                    }
                } else {
                    Toast.makeText(getApplicationContext(), "no data found", Toast.LENGTH_LONG).show();
                    showErrorMessage();
                }
            } finally {
                data.close();
            }
            Log.i("????????????", "Load movies data from DataBase");
            Log.i("???????????????????", "" + movies.size());
            mPopularMoviesAdapter.setPopularMoviesData(movies);
        }

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPopularMoviesAdapter.setPopularMoviesData(null);
        Log.i("+++++++++++++++++++++", "loader restarted");
    }

    public void addNewMovieToDataBase(ArrayList<Movie> movies){
        Log.i("???????????????????", ""+movies.size());
        // Create new empty ContentValues object
        ContentValues contentValues = new ContentValues();
        Uri uri=null;
        for(int i=0;i<movies.size();i++) {
            Movie movie=movies.get(i);
            // Put the task description and selected mPriority into the ContentValues
            contentValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            contentValues.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterFullPath());
            contentValues.put(MovieEntry.COLUMN_IS_FAVORITE, 0);
            uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        }

        // COMPLETED (8) Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if(uri != null) {
            Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadDataFromDataBase(){
        Toast.makeText(getApplicationContext(),"no internet connection ", Toast.LENGTH_LONG).show();
        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
          created, otherwise the last created loader is re-used.
          */
        this.currentLoaderId=TASK_LOADER_ID;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader cachedMovies = loaderManager.getLoader(TASK_LOADER_ID);
        if (cachedMovies == null) {
            loaderManager.initLoader(TASK_LOADER_ID, null, MainActivity.this);
        } else {
            loaderManager.restartLoader(TASK_LOADER_ID, null, MainActivity.this);
        }
//        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, MainActivity.this);


    }

    private void loadFavoriteMoviesFromDataBase(){
        /*
            Ensure a loader is initialized and active. If the loader doesn't already exist, one is
           created, otherwise the last created loader is re-used.
        */
        this.currentLoaderId=TASK_LOADER_FAVORITE_MOVIES_ID;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader FavoriteMoviesLoader = loaderManager.getLoader(TASK_LOADER_FAVORITE_MOVIES_ID);
        if (FavoriteMoviesLoader == null) {
            loaderManager.initLoader(TASK_LOADER_FAVORITE_MOVIES_ID, null, MainActivity.this);
        } else {
            loaderManager.restartLoader(TASK_LOADER_FAVORITE_MOVIES_ID, null, MainActivity.this);
        }
//        getSupportLoaderManager().initLoader(TASK_LOADER_FAVORITE_MOVIES_ID, null, MainActivity.this);


    }

    private void loadMoviesDataFromServer(){
        /*
        Ensure a loader is initialized and active. If the loader doesn't already exist, one is
        created, otherwise the last created loader is re-used.
        */
        this.currentLoaderId=TASK_LOADER_MOVIES_ID;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loadMoviesData = loaderManager.getLoader(TASK_LOADER_MOVIES_ID);
        if (loadMoviesData == null) {
            loaderManager.initLoader(TASK_LOADER_MOVIES_ID, null, MainActivity.this);
        } else {
            loaderManager.restartLoader(TASK_LOADER_MOVIES_ID, null, MainActivity.this);
        }
//        getSupportLoaderManager().initLoader(TASK_LOADER_MOVIES_ID, null, MainActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all movies
//        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);

        Log.i("########", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("########", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("########", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("########", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("########", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("+++++++++++++++++++++", "onRestart");
//        if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
//            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
//        }
        mPopularMoviesAdapter.setPopularMoviesData(movies);
//        Log.i("#####", "currentLoaderId "+this.currentLoaderId);
//        switch (this.currentLoaderId){
//            case 0:
//                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
//                break;
//            case 1:
//                getSupportLoaderManager().restartLoader(TASK_LOADER_FAVORITE_MOVIES_ID, null, MainActivity.this);
//                break;
//            case 2:
//                getSupportLoaderManager().restartLoader(TASK_LOADER_MOVIES_ID, null, MainActivity.this);
//                break;
//        }
    }

    private int getSmallestWidth(){
        Configuration config = getResources().getConfiguration();
        int sw=config.smallestScreenWidthDp;
//        Toast.makeText(getApplicationContext(), "Smallest Width "+sw, Toast.LENGTH_LONG).show();
        return sw;

    }
}
