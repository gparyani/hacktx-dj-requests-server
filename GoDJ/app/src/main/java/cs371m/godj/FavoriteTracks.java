package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Jasmine on 10/23/2016.
 */

public class FavoriteTracks extends AppCompatActivity {

    //public ArrayList<String[]> favoriteTracks;
    private ListView listView;
    private FavoritesItemAdapter favoritesItemAdapter;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);

        listView = (ListView) findViewById(R.id.fav_list_view);
        favoritesItemAdapter = new FavoritesItemAdapter(this);
        listView.setAdapter(favoritesItemAdapter);
        favoritesItemAdapter.changeList(UserMainActivity.favoriteTracks);
        favoritesItemAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);
                final int result = 1;
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

                startActivityForResult(showTrackPage, result);
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
            case R.id.favorites_ID:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}