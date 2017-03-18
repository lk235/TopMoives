package com.example.android.topmoives;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoiveDetailFragment extends Fragment {
    private Movie movie;
    private TextView mMovieTitleTextView;
    private ImageView mMoiveImageView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mOverViewTextView;



    public MoiveDetailFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_moive_detail, container, false);
        mMovieTitleTextView = (TextView)rootView.findViewById(R.id.movie_title_text_view);
        mMoiveImageView = (ImageView)rootView.findViewById(R.id.moive_image_view);
        mReleaseDateTextView = (TextView)rootView.findViewById(R.id.release_date_text_view);
        mVoteAverageTextView = (TextView)rootView.findViewById(R.id.vote_average_text_view);
        mOverViewTextView = (TextView)rootView.findViewById(R.id.overview_text_view);
        int position = (int)getActivity().getIntent().getSerializableExtra(MovieListFragment.MOVIE_ITEM_POSITION);

        movie = MovieLab.get(getActivity()).getMovie(position);
        mMovieTitleTextView.setText(movie.getTitle());
        Picasso.with(getActivity())
                .load(movie.getImageUrl())
                .placeholder(R.drawable.ic_sync_black_24dp)
                .into(mMoiveImageView);
        mReleaseDateTextView.setText(movie.getRelease_date());
        mVoteAverageTextView.setText(movie.getVote());
        mOverViewTextView.setText(movie.getOverview());



        return rootView;

    }

}
