package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Tracks;


/**
 * Created by rebeccal on 11/17/16.
 */

public class ArtistPageActivity extends AppCompatActivity {

    private ImageView artistImage;
    private TextView artistName;

    private TextView popularTitle;
    private TextView albumTitle;
    private TextView relatedTitle;

    private ListView popularTrackListView;
    private ListView albumListView;
    private ListView relatedArtistListView;

    private List<Tracks> popularTracks;
//    private List<Album> albums;
//    private List<Artist> relatedArtists;

    // private


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_page_layout);

        artistImage = (ImageView) findViewById(R.id.artist_image);
        artistName = (TextView) findViewById(R.id.artist_nm);
        popularTrackListView = (ListView) findViewById(R.id.popular_tracks);
        albumListView = (ListView) findViewById(R.id.albums);
        relatedArtistListView = (ListView) findViewById(R.id.related_artists);

        popularTitle = new TextView(this);
        popularTitle.setText("Popular");

        albumTitle = new TextView(this);
        albumTitle.setText("Albums");

        relatedTitle = new TextView(this);
        relatedTitle.setText("Related Artists");

        popularTitle.setTextColor(0xffffffff);
        popularTitle.setGravity(0x01);
        popularTitle.setTextSize(20);
        popularTitle.setTypeface(popularTitle.getTypeface(), 1);
        popularTitle.setPadding(0,0,0,50);
        popularTrackListView.addHeaderView(popularTitle, null, false);
        popularTitle.setVisibility(View.VISIBLE); //////////// fix this!

        Intent intent = getIntent();

        popularTracks = intent.getParcelableArrayListExtra("popTrackList");
        Picasso.with(getApplicationContext()).load(intent.getStringExtra("artistURL")).into(artistImage);
        artistName.setText(intent.getStringExtra("artistName"));

    }
}
