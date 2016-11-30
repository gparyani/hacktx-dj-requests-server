package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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
    private ListView albumsListView;
    private ListView relatedArtistListView;

    private List<Track> popularTracks;
    private List<Album> albums;
    private List<Artist> relatedArtists;

    private SpotifyItemAdapter popTracksAdapter;
    private AlbumItemAdapter albumsItemAdapter;
    private ArtistItemAdapter relatedArtistsAdapter;

    private Handler myHandler;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_page_layout);

        artistImage = (ImageView) findViewById(R.id.artist_image);
        artistName = (TextView) findViewById(R.id.artist_nm);
        popularTrackListView = (ListView) findViewById(R.id.popular_tracks);
        albumsListView = (ListView) findViewById(R.id.albums);
        relatedArtistListView = (ListView) findViewById(R.id.related_artists);
        myHandler = new Handler();

        popularTitle = new TextView(this);
        popularTitle.setText("Popular");
        popularTitle.setTextColor(0xffffffff);
        popularTitle.setTextSize(20);
        popularTitle.setTypeface(popularTitle.getTypeface(), 1);
        popularTitle.setPadding(350,35,0,50);
        popularTrackListView.addHeaderView(popularTitle, null, false);
        popularTitle.setVisibility(View.INVISIBLE);

        albumTitle = new TextView(this);
        albumTitle.setText("Albums");
        albumTitle.setTextColor(0xffffffff);
        albumTitle.setTextSize(20);
        albumTitle.setTypeface(albumTitle.getTypeface(), 1);
        albumTitle.setPadding(350,0,0,50);
        albumsListView.addHeaderView(albumTitle, null, false);
        albumTitle.setVisibility(View.INVISIBLE);

        relatedTitle = new TextView(this);
        relatedTitle.setText("Related Artists");
        relatedTitle.setTextColor(0xffffffff);
        relatedTitle.setTextSize(20);
        relatedTitle.setTypeface(relatedTitle.getTypeface(), 1);
        relatedTitle.setPadding(270,0,0,50);
        relatedArtistListView.addHeaderView(relatedTitle, null, false);
        relatedTitle.setVisibility(View.INVISIBLE);

        popTracksAdapter = new SpotifyItemAdapter(this);
        albumsItemAdapter = new AlbumItemAdapter(this);
        relatedArtistsAdapter = new ArtistItemAdapter(this);

        popularTrackListView.setAdapter(popTracksAdapter);
        albumsListView.setAdapter(albumsItemAdapter);
        relatedArtistListView.setAdapter(relatedArtistsAdapter);


        callServices();
    }


    class TopTracks implements Runnable {

        @Override
        public void run() {
            popularTitle.setVisibility(View.VISIBLE);
            popTracksAdapter.notifyDataSetChanged();
            popTracksAdapter.notifyDataSetChanged();
            popTracksAdapter.changeList(popularTracks);
            UserMainActivity.ListUtils.setDynamicHeight(popularTrackListView);
            popTracksAdapter.notifyDataSetChanged();
        }
    }

    class ArtistAlbums implements Runnable {

        @Override
        public void run() {
            albumTitle.setVisibility(View.VISIBLE);
            albumsItemAdapter.notifyDataSetChanged();
            albumsItemAdapter.notifyDataSetChanged();
            albumsItemAdapter.changeList(albums);
            UserMainActivity.ListUtils.setDynamicHeight(albumsListView);
            albumsItemAdapter.notifyDataSetChanged();

        }
    }

    class RelatedArtists implements Runnable {

        @Override
        public void run() {
            relatedTitle.setVisibility(View.VISIBLE);
            relatedArtistsAdapter.notifyDataSetChanged();
            relatedArtistsAdapter.notifyDataSetChanged();
            relatedArtistsAdapter.changeList(relatedArtists);
            UserMainActivity.ListUtils.setDynamicHeight(relatedArtistListView);
            relatedArtistsAdapter.notifyDataSetChanged();

        }
    }



    protected void callServices() {
        Intent intent = getIntent();
        String artistID = intent.getStringExtra("artistID");
        Picasso.with(getApplicationContext()).load(intent.getStringExtra("artistURL")).into(artistImage);
        artistName.setText(intent.getStringExtra("artistName"));


        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        spotify.getArtistTopTrack(artistID, "US", new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                popularTracks = tracks.tracks;
                myHandler.post(new TopTracks());


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                albums = albumPager.items;
                myHandler.post(new ArtistAlbums());

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        spotify.getRelatedArtists(artistID, new Callback<Artists>() {
            @Override
            public void success(Artists artists, Response response) {
                relatedArtists = artists.artists;
                myHandler.post(new RelatedArtists());


            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
