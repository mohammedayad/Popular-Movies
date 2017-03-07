package com.example.mohammedayad.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.mohammedayad.popularmovies.pojos.Movie;
import com.example.mohammedayad.popularmovies.utilities.NetworkUtils;
import com.example.mohammedayad.popularmovies.volley.MySingleton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mohammed.ayad on 2/28/2017.
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesAdapterViewHolder>{

    private ArrayList<Movie> movies;

    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final popularMoviesAdapterOnClickHandler mClickHandler;
    private final Context context;



    public PopularMoviesAdapter(popularMoviesAdapterOnClickHandler clickHandler,Context mContext) {
        mClickHandler = clickHandler;
        context=mContext;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface popularMoviesAdapterOnClickHandler {
        void onClick(Movie selectedMovie);
    }

    @Override
    public PopularMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        int layoutIdForListItem =R.layout.popular_movies_list_item;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view =inflater.inflate(layoutIdForListItem,parent,shouldAttachToParentImmediately);
        Log.i("+++++++++++++++++++++", "onCreateViewHolder ");
        return new PopularMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesAdapterViewHolder holder, int position) {
        Log.i("+++++++++++++++++++++", "onBindViewHolder "+position);
        Movie movie =movies.get(position);
        String posterPath=movie.getPosterPath();
        String moviePosterFullPath=NetworkUtils.buildPosterUrl(posterPath);
        holder.setMoviePoster(moviePosterFullPath,context);

    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        Log.i("+++++++++++++++++++++", movies.size()+"");
        return movies.size();
    }
    public void setPopularMoviesData(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class PopularMoviesAdapterViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
//        private ImageView firstPoster;
//        private ImageView secondPoster;
          private ImageView moviePoster;
        public PopularMoviesAdapterViewHolder(View itemView) {
            super(itemView);
//            firstPoster=(ImageView)itemView.findViewById(R.id.firstPoster);
//            secondPoster=(ImageView)itemView.findViewById(R.id.secondPoster);
            moviePoster=(ImageView)itemView.findViewById(R.id.moviePoster);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition =getAdapterPosition();
            Movie selectedNovie = movies.get(adapterPosition);
            mClickHandler.onClick(selectedNovie);

        }

        public void setMoviePoster(String moviePosterFullPath,Context context){
            Picasso.with(context)
                    .load(moviePosterFullPath)
                    .into(moviePoster);

        }
    }
}
