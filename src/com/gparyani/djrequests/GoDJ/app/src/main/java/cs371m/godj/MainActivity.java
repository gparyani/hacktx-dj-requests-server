package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private List<Track> trackList;
    private List<Artist> artistList;

    private List<Track> displayTrackList;
    private List<Artist> displayArtistList;

    private ListView listView;
    private ListView artistListView;

    private SpotifyItemAdapter spotifyItemAdapter;
    private ArtistItemAdapter artistItemAdapter;

    private TextView textView;
    private TextView artistTextView;

    private TextView trackFooter;
    private TextView artistFooter;

    private EditText et;
    private String userSearchInput;


    public static ArrayList<String[]> favoriteTracks;
    public static HashMap<String, String> faveTrackMap; // for checking if track already in list
    public static boolean clearSearch;
    private Handler myHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        trackList = new ArrayList<>();
        artistList = new ArrayList<>();

        displayTrackList = new ArrayList<>();
        displayArtistList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_view);
        artistListView = (ListView) findViewById(R.id.artist_list_view);

        spotifyItemAdapter = new SpotifyItemAdapter(this);
        artistItemAdapter = new ArtistItemAdapter(this);

        // track listview header
        textView = new TextView(this);
        textView.setText("Songs");
        textView.setTextColor(0xffffffff);
        textView.setGravity(0x01);
        textView.setTextSize(20);
        textView.setTypeface(textView.getTypeface(), 1);
        // add spacing
        textView.setPadding(0,0,0,50);
        listView.addHeaderView(textView, null, false);
        textView.setVisibility(View.INVISIBLE);

        // track listview footer
        trackFooter = new TextView(this);
        trackFooter.setText("See more songs");
        trackFooter.setTextColor(0xffffffff);
        listView.addFooterView(trackFooter);
        trackFooter.setVisibility(View.INVISIBLE);


        //artist listview header
        artistTextView = new TextView(this);
        artistTextView.setText("Artists");
        artistTextView.setTextColor(0xffffffff);
        artistTextView.setGravity(0x01);
        artistTextView.setTextSize(20);
        artistTextView.setTypeface(artistTextView.getTypeface(), 1);
        artistTextView.setPadding(0,0,0,50);
        artistListView.addHeaderView(artistTextView, null, false);
        artistTextView.setVisibility(View.INVISIBLE);

        //artist listview footer
        artistFooter = new TextView(this);
        artistFooter.setText("See more artists");
        artistFooter.setTextColor(0xffffffff);
        artistListView.addFooterView(artistFooter);
        artistFooter.setVisibility(View.INVISIBLE);

        listView.setAdapter(spotifyItemAdapter);
        artistListView.setAdapter(artistItemAdapter);

        faveTrackMap = new HashMap<>();
        favoriteTracks = new ArrayList<>();
        clearSearch = false; // only clear search if user comes back to search page by selecting from menu, not from back button
        myHandler = new Handler();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    Intent showAllSongResults = new Intent(getApplicationContext(), ShowAllSongResultsActivity.class);
                    showAllSongResults.putParcelableArrayListExtra("list", (ArrayList) trackList);
                    showAllSongResults.putExtra("searchTerm", userSearchInput);
                    startActivity(showAllSongResults);
                } else {

                    Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);

                    TextView trackName = (TextView) view.findViewById(R.id.track_name);
                    TextView artistName = (TextView) view.findViewById(R.id.artist_name);
                    TextView imageURL = (TextView) view.findViewById(R.id.album_art_url);
                    TextView albumName = (TextView) view.findViewById(R.id.album_name);
                    TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

                    showTrackPage.putExtra("trackName", trackName.getText().toString());
                    showTrackPage.putExtra("artistName", artistName.getText().toString());
                    showTrackPage.putExtra("imageURL", imageURL.getText().toString());
                    showTrackPage.putExtra("albumName", albumName.getText().toString());
                    showTrackPage.putExtra("trackURI", trackURI.getText().toString());

                    startActivity(showTrackPage);
                }
            }
        });

        // set on item listener for artistListView
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    Intent showAllArtistResults = new Intent(getApplicationContext(), ShowAllArtistResultsActivity.class);
                    showAllArtistResults.putParcelableArrayListExtra("list", (ArrayList) artistList);
                    showAllArtistResults.putExtra("searchTerm", userSearchInput);
                    startActivity(showAllArtistResults);
                } else {

                }
            }
        });


        et = (EditText) findViewById(R.id.searchTerm);
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            processSearch();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.search_ID:
                break;
            case R.id.favorites_ID:
                launchFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void launchFavorites() {
        Intent startFavorites = new Intent(this, FavoriteTracks.class);
        startFavorites.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startFavorites);
    }


    class UpdateTrackSearchResults implements Runnable {
        @Override
        public void run() {
            textView.setVisibility(View.VISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            trackFooter.setVisibility(View.VISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            spotifyItemAdapter.changeList(displayTrackList);
            ListUtils.setDynamicHeight(listView);
            spotifyItemAdapter.notifyDataSetChanged();

        }
    }

    class UpdateArtistSearchResults implements Runnable {
        @Override
        public void run() {
            artistTextView.setVisibility(View.VISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistFooter.setVisibility(View.VISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistItemAdapter.changeList(displayArtistList);
            ListUtils.setDynamicHeight(artistListView);
            artistItemAdapter.notifyDataSetChanged();
        }
    }


    protected void processSearch() {
        EditText et = (EditText) findViewById(R.id.searchTerm);
        String searchTerm = et.getText().toString();
        userSearchInput = searchTerm;

        trackList.clear();
        artistList.clear();

        displayTrackList.clear();
        displayArtistList.clear();

        spotifyItemAdapter.notifyDataSetChanged();
        artistItemAdapter.notifyDataSetChanged();

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        HashMap<String, Object> options = new HashMap<>();
        int limit = 50;
        options.put(spotify.LIMIT, limit);

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        //api.setAccessToken("myAccessToken");

        textView.setVisibility(View.INVISIBLE);
        spotifyItemAdapter.notifyDataSetChanged();
        artistTextView.setVisibility(View.INVISIBLE);
        artistItemAdapter.notifyDataSetChanged();


        spotify.searchTracks(searchTerm, options, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                trackList = tracksPager.tracks.items;

                int displaySize = Math.min(trackList.size(), 5);

                for(int i = 0; i < displaySize; i++) {
                    displayTrackList.add(trackList.get(i));
                }

                myHandler.post(new UpdateTrackSearchResults());
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("Tracks: ", "ERROR: " + error.getMessage());
            }
        });

        spotify.searchArtists(searchTerm, options, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                artistList = artistsPager.artists.items;

                int displaySize = Math.min(artistList.size(), 5);

                for(int i = 0; i < displaySize; i++) {
                    displayArtistList.add(artistList.get(i));
                }

                myHandler.post(new UpdateArtistSearchResults());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Artist: ", "ERROR: " + error.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clearSearch) {
            textView.setVisibility(View.INVISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            trackFooter.setVisibility(View.INVISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            displayTrackList.clear();
            spotifyItemAdapter.notifyDataSetChanged();
            trackList.clear();

            artistTextView.setVisibility(View.INVISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistFooter.setVisibility(View.INVISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            displayArtistList.clear();
            artistItemAdapter.notifyDataSetChanged();
            artistList.clear();

            EditText et = (EditText) findViewById(R.id.searchTerm);
            et.setText("");
            clearSearch = false;
        }
    }
}
