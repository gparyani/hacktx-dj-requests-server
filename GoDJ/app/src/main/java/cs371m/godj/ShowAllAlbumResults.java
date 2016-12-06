package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jasmine on 11/10/2016.
 */

public class ShowAllAlbumResults extends AppCompatActivity {


    private  AlbumItemAdapter albumItemAdapter;
    private ListView listView;
    private List<AlbumSimple> albums;
    private Handler myHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.all_results_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String searchTerm = intent.getStringExtra("searchTerm");
        String formatTitle;
        if (searchTerm == null) {
            formatTitle = "All Albums";
        } else {
            formatTitle = "\"" + searchTerm + "\"" + " in Songs";
        }
        getSupportActionBar().setTitle(formatTitle);

        myHandler = new Handler();

        listView = (ListView) findViewById(R.id.show_all_list_view);
        albums = intent.getParcelableArrayListExtra("list");
        albumItemAdapter = new AlbumItemAdapter(this);
        listView.setAdapter(albumItemAdapter);
        albumItemAdapter.changeList(albums);
        albumItemAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.album_id);
                String albumID = tv.getText().toString();

                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                spotify.getAlbum(albumID, new Callback<Album>() {
                    @Override
                    public void success(Album album, Response response) {
                        myHandler.post(new AlbumSelected(album));
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }


    private class AlbumSelected implements Runnable {

        Album album;

        public AlbumSelected(Album a) {
            album = a;
        }

        @Override
        public void run() {
            Intent showAlbumPage = new Intent(getApplicationContext(), AlbumPageActivity.class);


            List<Image> albumImages = album.images;
            String albumName = album.name;
            String albumID = album.id;
            List<TrackSimple> albumTracks = album.tracks.items;
            List<ArtistSimple> albumArtists = album.artists;

            showAlbumPage.putParcelableArrayListExtra("albumImages", (ArrayList) albumImages);
            showAlbumPage.putExtra("albumName", albumName);
            showAlbumPage.putExtra("albumID", albumID);
            showAlbumPage.putParcelableArrayListExtra("albumTracks", (ArrayList) albumTracks);
            showAlbumPage.putParcelableArrayListExtra("albumArtists", (ArrayList) albumArtists);

            // TextView tv = (TextView) findViewById(R.id.album_id);
            //String albumID = tv.getText().toString();
            //showAlbumPage.putExtra("albumID", albumID);
            startActivity(showAlbumPage);
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

        switch (id) {
            case R.id.search_ID:
                Intent goSearch = new Intent(this, UserMainActivity.class);
                goSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                UserMainActivity.clearSearch = true;
                startActivity(goSearch);
                break;
            case R.id.return_home_ID:
                Intent goHome = new Intent(this, HomePage.class);
//                goHome.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(goHome);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
