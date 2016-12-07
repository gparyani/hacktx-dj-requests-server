package cs371m.godj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by Jasmine on 11/10/2016.
 */

public class AlbumPageActivity extends AppCompatActivity {

    private ListView listView;
    private AlbumPageAdapter albumPageAdapter;
    private ImageView albumImage;
    private TextView albumNameTV;

    private List<Image> images;
    private List<TrackSimple> tracks;
    private List<ArtistSimple> artists;
    private String albumName;
    private String albumID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_page_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();


        images = intent.getParcelableArrayListExtra("albumImages");
        tracks = intent.getParcelableArrayListExtra("albumTracks");
        artists = intent.getParcelableArrayListExtra("albumArtists");
        albumName = intent.getStringExtra("albumName");
        albumID = intent.getStringExtra("albumID");


        albumImage = (ImageView) findViewById(R.id.album_page_album_art);
        albumNameTV = (TextView) findViewById(R.id.album_page_album_name);

        final String imgURL = images.get(0).url;
        if(!imgURL.equals("")) {
            Picasso.with(getApplicationContext()).load(imgURL).into(albumImage);
        } else {
            albumImage.setImageResource(R.drawable.music_record);
        }
        albumNameTV.setText(albumName);

        listView = (ListView) findViewById(R.id.album_page_list_view);
        albumPageAdapter = new AlbumPageAdapter(this);
        listView.setAdapter(albumPageAdapter);
        albumPageAdapter.changeList(tracks);
        albumPageAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);

                TextView trackName = (TextView) view.findViewById(R.id.track_name);
                TextView artistName = (TextView) view.findViewById(R.id.artist_name);


                TextView trackURI = (TextView) view.findViewById(R.id.track_uri);

                showTrackPage.putExtra("trackName", trackName.getText().toString());
                showTrackPage.putExtra("artistName", artistName.getText().toString());
                showTrackPage.putExtra("imageURL", imgURL);
                showTrackPage.putExtra("albumName", albumName);
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
