package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * Created by rebeccal on 11/17/16.
 */

public class ArtistPageActivity extends AppCompatActivity {

    private ImageView artistImage;

    private TextView popularTitle;
    private TextView albumTitle;
    private TextView relatedTitle;

    private ListView popularTrackList;
    private ListView albumList;
    private ListView relatedArtistList;

    private List<Tracks> popularTracks;
    private List<Album> albums;
    private List<Artist> relatedArtists;

    private Handler myHandler;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_page_layout);

        artistImage = (ImageView) findViewById(R.id.artist_image);

        popularTitle = new TextView(this);
        popularTitle.setText("Popular");

        albumTitle = new TextView(this);
        albumTitle.setText("Albums");

        relatedTitle = new TextView(this);
        relatedTitle.setText("Related Artists");

        Intent intent = getIntent();



    }
}
