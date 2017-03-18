package com.example.android.topmoives;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lk235 on 2017/3/18.
 */

public class MovieLab {
    private static MovieLab sMovieLab;
    private List<Movie> mMovies;



    public static MovieLab get(Context context){
        if (sMovieLab == null){
            sMovieLab = new MovieLab(context);
        }
        return sMovieLab;
    }

    private MovieLab(Context context){
        mMovies = new ArrayList<>();

    }

    public List<Movie> getmMovies(){
        return mMovies;
    }

    public Movie getMovie( int id){

        return mMovies.get(id);
    }

    public void addMovie(Movie movie){
        mMovies.add(movie);
    }

    public void clearMovies(){
        mMovies.clear();
    }

    public boolean isNotEmpty(){
        if(mMovies.size() > 0){
            return true;
        }else {
            return false;
        }
    }


}
