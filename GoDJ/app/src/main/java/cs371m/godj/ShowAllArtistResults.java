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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView artistID = (TextView) view.findViewById(R.id.artist_id);
                TextView artistName = (TextView) view.findViewById(R.id.artist);
                TextView artistURL = (TextView) view.findViewById(R.id.artist_image_url);


                Intent showArtistPage = new Intent(getApplicationContext(), ArtistPageActivity.class);
                showArtistPage.putExtra("artistID", artistID.getText().toString());
                showArtistPage.putExtra("artistName", artistName.getText().toString());
                showArtistPage.putExtra("artistURL", artistURL.getText().toString());
                startActivity(showArtistPage);
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