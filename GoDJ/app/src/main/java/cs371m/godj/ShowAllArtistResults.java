package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Jasmine on 11/8/2016.
 */

public class ShowAllArtistResults extends AppCompatActivity {

    private ArtistItemAdapter artistItemAdapter;
    private ListView listView;
    private List<Artist> artists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.all_results_layout);

        Intent intent = getIntent();
        String searchTerm = intent.getStringExtra("searchTerm");
        String formatTitle;
        if (searchTerm == null) {
            formatTitle = "All Related Artists";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Artists";
        }
        getSupportActionBar().setTitle(formatTitle);

        listView = (ListView) findViewById(R.id.show_all_list_view);
        artists = intent.getParcelableArrayListExtra("list");
        artistItemAdapter = new ArtistItemAdapter(this);
        listView.setAdapter(artistItemAdapter);
        artistItemAdapter.changeList(artists);
        artistItemAdapter.notifyDataSetChanged();



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
                Intent goSearch = new Intent(this, UserMainActivity.class);
                goSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserMainActivity.clearSearch = true;
                finish();
                startActivity(goSearch);
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
        finish();
        startActivity(startFavorites);
    }
}