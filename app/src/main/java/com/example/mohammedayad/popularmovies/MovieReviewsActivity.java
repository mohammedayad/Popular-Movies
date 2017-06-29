package com.example.mohammedayad.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammed.ayad on 6/24/2017.
 */

public class MovieReviewsActivity extends AppCompatActivity {


    @BindView(R.id.movieReview) ListView movieReviewsList;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_reviews_list);
        ButterKnife.bind(this);




        List<HashMap<String,String>> movieReviews= (List<HashMap<String, String>>) getIntent().getSerializableExtra("movieReviews");
        SimpleAdapter adapter = new SimpleAdapter(this, movieReviews, R.layout.movie_reviews_item,
                new String[] {"author", "content"},
                new int[] {R.id.reviewerName,
                        R.id.reviewContent});
        movieReviewsList.setAdapter(adapter);

    }
}
