package com.example.android.topmoives;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment {

    public static final String MOVIE_ITEM_POSITION = "itemPosition";
    public static final String NETWORK_NOT_CONNECTED = "network is not connted!";
    public MovieLab mMovieLab;
    private RecyclerView mMovieListRecylerView;
    private MoiveAdapter mMovieAdapter;


    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mMovieListRecylerView = (RecyclerView) rootView.findViewById(R.id.movie_list_recycler_view);
        mMovieListRecylerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        checkNetworkAndFetchData();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_setting) {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivityForResult(intent, 0);
        }
        if (menuItem.getItemId() == R.id.action_refresh) {
            Log.i("REFRESH", "refresh");
            checkNetworkAndFetchData();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * This method check the networkState and fetch movie data.
     */
    private void checkNetworkAndFetchData() {
        if (isOnline()) {
            updateMovieData();
        } else {
            Toast.makeText(getActivity(), NETWORK_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
        }
    }


    public class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mMoiveImageView;

        public MovieHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMoiveImageView = (ImageView) itemView.findViewById(R.id.movie_image_item);
        }

        public void bindMovieItem(Movie movieItem) {
            Picasso.with(getActivity())
                    .load(movieItem.getImageUrl())
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .into(mMoiveImageView);


        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.putExtra(MOVIE_ITEM_POSITION, getAdapterPosition());

            startActivity(intent);


        }
    }

    public class MoiveAdapter extends RecyclerView.Adapter<MovieHolder> {
        private List<Movie> mMovies;

        public MoiveAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            final View view = inflater.inflate(R.layout.movie_item, viewGroup, false);
            ;
            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(MovieHolder movieHolder, int positon) {
            Movie movieItem = mMovies.get(positon);
            movieHolder.bindMovieItem(movieItem);


        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

    }

    /**
     * This method get the sharedPreftrence value and call the asyncTask to get movie data.
     */
    private void updateMovieData() {

        new FetchMovieDate().execute(getPrefSortType());
    }

    private String getPrefSortType() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        });
        String pref_sort_type = sharedPreferences
                .getString(getString(R.string.pref_sort_key), getString((R.string.pref_sort_default)));
        return pref_sort_type;
    }


    public class FetchMovieDate extends AsyncTask<String, Void, MovieLab> {
        private final String TAG = "FetchMovieData";
        private final String MOVIE_URL = "http://api.themoviedb.org/3/movie";
        private final String IMAGE_URL = "https://image.tmdb.org/t/p/w185";
        private final String VOTE = "VOTE: ";
        private final String API_KEY = "api_key";
        private final String LANGUAGE = "language";
        String movieJsonStr = null;
        String apiKey = "";
        String language = "zh";


        protected MovieLab doInBackground(String... queryType) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            ArrayList<String[]> stringArrayList = null;

            try

            {
                Uri uri = Uri.parse(MOVIE_URL).buildUpon()
                        .appendPath(queryType[0])
                        .appendQueryParameter(LANGUAGE, language)
                        .appendQueryParameter(API_KEY, apiKey)
                        .build();

                URL url = new URL(uri.toString());
                Log.i(TAG, url + "");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                Log.i(TAG, "code= " + responseCode);
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {

                    movieJsonStr = null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {

                    stringBuffer.append(line + "\n");
                }

                if (stringBuffer.length() == 0) {

                    movieJsonStr = null;
                }
                movieJsonStr = stringBuffer.toString();

            } catch (
                    IOException ioe)

            {
                movieJsonStr = null;
                Log.i(TAG, "" + ioe);
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }

            }


            try

            {
                stringArrayList = getMovieDataFromJson(movieJsonStr);
            } catch (
                    JSONException js)

            {

            }
            mMovieLab = MovieLab.get(getActivity());

            if (mMovieLab.isNotEmpty()) {
                mMovieLab.clearMovies();
            }

            for (int i = 0; i < stringArrayList.size(); i++)

            {
                Movie movie = new Movie();
                movie.setTitle(stringArrayList.get(i)[0]);
                movie.setImageUrl(IMAGE_URL + stringArrayList.get(i)[1]);
                movie.setRelease_date(stringArrayList.get(i)[2]);
                movie.setVote(VOTE + stringArrayList.get(i)[3]);
                movie.setOverview(stringArrayList.get(i)[4]);

                mMovieLab.addMovie(movie);

            }

            return mMovieLab;
        }


        protected void onPostExecute(MovieLab result) {


            mMovieAdapter = new MoiveAdapter(result.getmMovies());

            mMovieListRecylerView.setAdapter(mMovieAdapter);


        }


    }


    private ArrayList<String[]> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULTS = "results";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_TITLE = "title";
        final String OWM_RELEASE_DATE = "release_date";
        final String OWM_VOTE_AVERAGE = "vote_average";

        ArrayList<String[]> resultStrs = new ArrayList<>();


        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);


        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject singleMovie = movieArray.getJSONObject(i);

            String[] moiveItem = new String[]{
                    singleMovie.getString(OWM_TITLE),
                    singleMovie.getString(OWM_POSTER_PATH),
                    singleMovie.getString(OWM_RELEASE_DATE),
                    singleMovie.getString(OWM_VOTE_AVERAGE),
                    singleMovie.getString(OWM_OVERVIEW)};

            resultStrs.add(moiveItem);

        }

        return resultStrs;

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}





