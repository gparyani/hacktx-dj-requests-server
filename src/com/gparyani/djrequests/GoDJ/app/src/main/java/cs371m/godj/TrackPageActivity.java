package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by Jasmine on 10/23/2016.
 */

public class TrackPageActivity extends AppCompatActivity {

    private String trackName;
    private String artistName;
    private String imageURL;
    private String albumName;
    private String trackURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_layout);

        Intent loadTrackPage = getIntent();
        trackName = loadTrackPage.getExtras().getString("trackName");
        artistName = loadTrackPage.getExtras().getString("artistName");
        imageURL = loadTrackPage.getExtras().getString("imageURL");
        albumName = loadTrackPage.getExtras().getString("albumName");
        trackURI = loadTrackPage.getExtras().getString("trackURI");


        ImageView art = (ImageView) findViewById(R.id.album_art);

        Picasso.with(this).load(imageURL).into(art);

        TextView trackTV = (TextView) findViewById(R.id.track_info);
        TextView artistTV = (TextView) findViewById(R.id.artist_info);

        trackTV.setText(trackName);
        artistTV.setText(artistName);

        ImageButton addSongBut = (ImageButton) findViewById(R.id.add_song_button);
        addSongBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] trackInfo = {trackName, albumName, artistName, imageURL, trackURI};
                if(!MainActivity.faveTrackMap.containsKey(trackURI)) {
                    MainActivity.faveTrackMap.put(trackURI, trackName);
                    MainActivity.favoriteTracks.add(trackInfo);
                    Toast.makeText(getApplicationContext(), "Added Song to Favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    //Intent sendTrackBack = new Intent();
                    //sendTrackBack.putExtra("trackInfo", trackInfo);
                    //setResult(RESULT_OK, sendTrackBack);
                    Toast.makeText(getApplicationContext(), "Song Already in Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton reqBut = (ImageButton) findViewById(R.id.req_song_button);
        reqBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client = new Client(getApplicationContext(), trackURI);
                client.execute();
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
                Intent goSearch = new Intent(this, MainActivity.class);
                goSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainActivity.clearSearch = true;
                startActivity(goSearch);
                break;
            case R.id.favorites_ID:
                Intent goFave = new Intent(this, FavoriteTracks.class);
                goFave.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goFave);
                break;
        }




        return super.onOptionsItemSelected(item);
    }


}
