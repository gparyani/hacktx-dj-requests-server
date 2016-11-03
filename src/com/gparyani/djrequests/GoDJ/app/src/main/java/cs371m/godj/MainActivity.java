package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private List<Track> trackList;
    private ListView listView;
    private SpotifyItemAdapter spotifyItemAdapter;
    public static ArrayList<String[]> favoriteTracks;
    public static HashMap<String, String> faveTrackMap; // for checking if track already in list
    public static boolean clearSearch;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        spotifyItemAdapter = new SpotifyItemAdapter(this);
        listView.setAdapter(spotifyItemAdapter);
        faveTrackMap = new HashMap<>();
        favoriteTracks = new ArrayList<>();
        trackList = new ArrayList<>();
        clearSearch = false;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);
                //final int result = 1;
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

//                startActivityForResult(showTrackPage, result);
                startActivity(showTrackPage);
            }
        });

        EditText et = (EditText) findViewById(R.id.searchTerm);
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
//        for(int i = 0; i < favoriteTracks.size(); i++) {
//            String[] track = favoriteTracks.get(i);
//            startFavorites.putExtra("" + i, track);
//        }
//        startFavorites.putExtra("listSize", favoriteTracks.size());
        startActivity(startFavorites);

    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == RESULT_OK) {
//            String[] track = (String[]) data.getStringArrayExtra("trackInfo");
//            favoriteTracks.add(track);
//        }
//    }

    protected void processSearch() {
        EditText et = (EditText) findViewById(R.id.searchTerm);
        String searchTerm = et.getText().toString();
        trackList.clear();

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        //api.setAccessToken("myAccessToken");


        spotify.searchTracks(searchTerm, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                trackList = tracksPager.tracks.items;
                for(Track t: trackList) {
                    System.out.println(t.name);
                }
                Log.d("Tracks:", tracksPager.toString());
                spotifyItemAdapter.changeList(trackList);
                spotifyItemAdapter.notifyDataSetChanged();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("Tracks:", "ERROR:" + error.getMessage());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clearSearch) {
            trackList.clear();
            spotifyItemAdapter.notifyDataSetChanged();
            EditText et = (EditText) findViewById(R.id.searchTerm);
            et.setText("");
            clearSearch = false;
        }
    }
}
