package com.example.mohammedayad.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mohammedayad.popularmovies.pojos.Movie;

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



    public PopularMoviesAdapter(popularMoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
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
        return new PopularMoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PopularMoviesAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.size();
    }
    public void setPopularMoviesData(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class PopularMoviesAdapterViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView firstPoster;
        private ImageView secondPoster;
        public PopularMoviesAdapterViewHolder(View itemView) {
            super(itemView);
            firstPoster=(ImageView)itemView.findViewById(R.id.firstPoster);
            secondPoster=(ImageView)itemView.findViewById(R.id.secondPoster);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition =getAdapterPosition();
            Movie selectedNovie = movies.get(adapterPosition);
            mClickHandler.onClick(selectedNovie);

        }
    }
}
