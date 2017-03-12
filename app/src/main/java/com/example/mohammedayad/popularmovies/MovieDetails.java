package com.example.mohammedayad.popularmovies;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohammedayad.popularmovies.pojos.Movie;
import com.example.mohammedayad.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {

    private Movie movie;
    @BindView(R.id.movieTitle) TextView movieTitle;
    @BindView(R.id.fullMoviePoster) ImageView moviePoster;
    @BindView(R.id.releaseDate) TextView releaseDate;
    @BindView(R.id.voteAverage) TextView voteAverage;
    @BindView(R.id.overview) TextView movieOverview;
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
        movie = (Movie) data.getParcelable("movie");
        movieTitle.setText(movie.getOriginalTitle());
        setMoviePoster();
        releaseDate.setText(movie.getReleaseDate());
        voteAverage.setText(movie.getVoteAverage()+"/10");
        movieOverview.setText(movie.getOverview());
    }
    public void setMoviePoster(){
        String posterPath=movie.getPosterPath();
        String moviePosterFullPath= NetworkUtils.buildPosterUrl(posterPath);
        Picasso.with(getApplicationContext())
                .load(moviePosterFullPath)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .into(moviePoster);

    }
}
