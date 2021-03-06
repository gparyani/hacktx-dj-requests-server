package cs371m.godj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Jasmine on 11/8/2016.
 */

public class ShowAllSongResults extends AppCompatActivity {

    private SpotifyItemAdapter spotifyItemAdapter;
    private ListView listView;
    private List<Track> tracks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.all_results_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();

        String searchTerm = intent.getStringExtra("searchTerm");
        String formatTitle;
        if (searchTerm == null) {
            formatTitle = "All Popular Songs";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Songs";
        }
        getSupportActionBar().setTitle(formatTitle);

        listView = (ListView) findViewById(R.id.show_all_list_view);
        tracks = intent.getParcelableArrayListExtra("list");
        spotifyItemAdapter = new SpotifyItemAdapter(this);
        listView.setAdapter(spotifyItemAdapter);
        spotifyItemAdapter.changeList(tracks);
        spotifyItemAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.search_ID:
                Intent goSearch = new Intent(this, UserMainActivity.class);
                goSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserMainActivity.clearSearch = true;
                startActivity(goSearch);
                break;
            case R.id.return_home_ID:
                Intent goHome = new Intent(this, HomePage.class);
                startActivity(goHome);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
