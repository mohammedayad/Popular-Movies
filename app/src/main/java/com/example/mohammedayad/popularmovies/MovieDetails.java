package com.example.mohammedayad.popularmovies;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {

    private Movie movie;
    @BindView(R.id.movieTitle) TextView movieTitle;
    @BindView(R.id.fullMoviePoster) ImageView moviePoster;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.voteAverage) TextView voteAverage;
    @BindView(R.id.overview) TextView movieOverview;
    @BindView(R.id.movieRate) ImageView movieRate;
    @BindView(R.id.playTrailerOne) ImageButton playMovieTrailerOne;
    @BindView(R.id.playTrailerTwo) ImageButton playMovieTrailerTwo;
    @BindView(R.id.showReviews) Button mShowReviews;
    private int isFavoriteMovie;
    private Dialog dialog;
    public static final String ACTION_LOAD_MOVIES_TRAILERS = "load-movie-trailer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
//        movieTitle=(TextView)findViewById(R.id.movieTitle);
//        moviePoster=(ImageView)findViewById(R.id.fullMoviePoster);
//        releaseDate=(TextView)findViewById(R.id.releaseDate);
//        voteAverage=(TextView)findViewById(R.id.voteAverage);
//        movieOverview=(TextView)findViewById(R.id.overview);
        ButterKnife.bind(this);
        Bundle data = getIntent().getExtras();
        if(savedInstanceState==null) {
             movie = (Movie) data.getParcelable("movie");
//        Intent movieTrailers=new Intent(this, MovieTrailersIntentService.class);
//        movieTrailers.setAction(ACTION_LOAD_MOVIES_TRAILERS);
//        movieTrailers.putExtra("movieId",movie.getId());
//        startService(movieTrailers);
            setMovieDetails();
        }

        movieRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("?????","onClick "+isFavoriteMovie);
                if (isFavoriteMovie==0) {
                    movieRate.setImageResource(R.drawable.movie_rate);
                    isFavoriteMovie=1;
                }else{
                    movieRate.setImageResource(R.drawable.default_star);
                    isFavoriteMovie=0;
                }

            }
        });

        playMovieTrailerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=createDialog(0);
                dialog.show();

            }
        });
        playMovieTrailerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=createDialog(1);
                dialog.show();

            }
        });

        mShowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmovieReviews();

            }
        });
    }
    public void setMoviePoster(){
//        String posterPath=movie.getPosterPath();
//        String moviePosterFullPath= NetworkUtils.buildPosterUrl(posterPath);
        Picasso.with(getApplicationContext())
                .load(movie.getPosterFullPath())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .into(moviePoster);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Create new empty ContentValues object
        Log.i("========","favorite flag "+this.isFavoriteMovie);
        if(movie.getIsFavoriteMovie()!=this.isFavoriteMovie) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, this.isFavoriteMovie);
            Uri movieUpdated=MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build();
            Toast.makeText(getApplicationContext(),"movie updated uri "+movieUpdated.toString(), Toast.LENGTH_LONG).show();
            int num = getContentResolver().update(movieUpdated, contentValues, null, null);
            Toast.makeText(getApplicationContext(),"returned Num "+num, Toast.LENGTH_LONG).show();
            // re-queries for all tasks
        }else{
            Toast.makeText(getApplicationContext(),"No change on this movie", Toast.LENGTH_LONG).show();

        }
    }
    private void markMovieAsFavorite(){
        if (isFavoriteMovie==1) {
            movieRate.setImageResource(R.drawable.movie_rate);
//            isFavoriteMovie=0;
        }else{
            movieRate.setImageResource(R.drawable.default_star);
//            isFavoriteMovie=1;
        }
    }

    private void playMovieTrailerInWebBrowser(int id){
        if (MovieTrailersIntentService.hasConnection) {
            ArrayList<String> movieTrailers = MovieTrailersIntentService.moviesTrailersKeys;
            if (movieTrailers != null) {
                Uri trailerUrl = Uri.parse("http://www.youtube.com/watch?v=" + movieTrailers.get(id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, trailerUrl);
                startActivity(webIntent);
                Log.i("Video", "Video Playing....");
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.loading_trailer), Toast.LENGTH_LONG).show();


            }
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.check_your_connection), Toast.LENGTH_LONG).show();

        }
    }
    private void playMovieTrailerInNativeApp(int id){
        if (MovieTrailersIntentService.hasConnection) {
            ArrayList<String> movieTrailers = MovieTrailersIntentService.moviesTrailersKeys;
            if (movieTrailers != null) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieTrailers.get(id)));
                startActivity(appIntent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.loading_trailer), Toast.LENGTH_LONG).show();


            }
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.check_your_connection), Toast.LENGTH_LONG).show();

        }
    }

    public Dialog createDialog(final int chosenMovieTrailer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.play_trailer_option))
                .setItems(R.array.play_trailer_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which){
                            case 0:
                                playMovieTrailerInNativeApp(chosenMovieTrailer);
                                break;
                            case 1:
                                playMovieTrailerInWebBrowser(chosenMovieTrailer);
                                break;

                        }
                    }
                });
        return builder.create();
    }

    private void showmovieReviews(){
        if (MovieTrailersIntentService.hasConnection) {
            List<HashMap<String, String>> movieReviews = MovieTrailersIntentService.movieReviews;
            if (movieReviews.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), MovieReviewsActivity.class);
                intent.putExtra("movieReviews", (Serializable) movieReviews);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.loading_reviews), Toast.LENGTH_LONG).show();


            }
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.check_your_connection), Toast.LENGTH_LONG).show();

        }
//        dialog = new Dialog(this);//for show information about place
//        dialog.setContentView(R.layout.movie_reviews_list);
//        dialog.setTitle("Reviews");
//        dialog.getWindow().setLayout(900,700);
//        dialog.show();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie",movie);


    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Movie savedMovie=savedInstanceState.<Movie>getParcelable("movie");
        this.movie=savedMovie;
        setMovieDetails();
    }

    private void setMovieDetails(){
        movieTitle.setText(movie.getOriginalTitle());
        setMoviePoster();
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(movie.getVoteAverage()+"/10");
        movieOverview.setText(movie.getOverview());
//        if (NetworkUtils.isNetworkConnected(getApplicationContext())){
//            isFavoriteMovie=loadFavoriteMovieFlag();
//            Log.d("######","internet");
//        }else {
//            isFavoriteMovie = movie.getIsFavoriteMovie();
//            Log.d("######","cached");
//
//        }
        loadFavoriteMovieFlag();
        isFavoriteMovie = movie.getIsFavoriteMovie();
        Log.d("######","before click "+isFavoriteMovie);
        markMovieAsFavorite();


    }

    private void loadFavoriteMovieFlag(){
        Uri selectedMovie=MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build();
        int isFavoriteMovie=0;
        Cursor returnedMovie=getContentResolver().query(selectedMovie,
                null,
                "movie_id=?",
                null,
                null);
        if (returnedMovie.getCount() > 0) {
            returnedMovie.moveToNext();
             int isFavoriteMovieIndex = returnedMovie.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITE);
            isFavoriteMovie=returnedMovie.getInt(isFavoriteMovieIndex);
        }
        Log.d("######","loadFavoriteMovieFlag "+isFavoriteMovie);
        movie.setIsFavoriteMovie(isFavoriteMovie);

    }
}
