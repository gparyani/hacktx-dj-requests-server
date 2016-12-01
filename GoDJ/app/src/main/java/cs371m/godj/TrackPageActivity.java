package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
        trackTV.setSelected(true);
        trackTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        trackTV.setMarqueeRepeatLimit(-1); // repeat indefinitely
        trackTV.setMaxLines(1);
        trackTV.setFocusable(true);
        trackTV.setFocusableInTouchMode(true);
        trackTV.setHorizontallyScrolling(true);
        artistTV.setText(artistName);

        ImageButton addSongBut = (ImageButton) findViewById(R.id.add_song_button);
        addSongBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] trackInfo = {trackName, albumName, artistName, imageURL, trackURI};
                if(!UserMainActivity.faveTrackMap.containsKey(trackURI)) {
                    UserMainActivity.faveTrackMap.put(trackURI, trackName);
                    UserMainActivity.favoriteTracks.add(trackInfo);
                    String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String _userName = user.replaceAll("\\.", "@"); // . illegal in Firebase key
                    FirebaseDatabase.getInstance().getReference("users").child(_userName).child("track").push().setValue(trackURI);
                    Toast.makeText(getApplicationContext(), "Added Song to Favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Song Already in Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton reqBut = (ImageButton) findViewById(R.id.req_song_button);
        reqBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                userName = userName.replaceAll("\\.", "@");
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("users").child(userName).child("eventAttending")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String currEvent = (String) dataSnapshot.getValue();
                        if(!currEvent.equals("none")) {
                            Query q = db.child("eventPlaylists")
                                    .child(currEvent)
                                    .orderByChild("trackName")
                                    .equalTo(trackName);
                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() == null) {
                                        TrackDatabaseObject trackDatabaseObject = new TrackDatabaseObject();
                                        trackDatabaseObject.setArtistName(artistName);
                                        trackDatabaseObject.setAlbumName(albumName);
                                        trackDatabaseObject.setTrackName(trackName);
                                        trackDatabaseObject.setTrackURI(trackURI);
                                        db.child("eventPlaylists")
                                                .child(currEvent).push().setValue(trackDatabaseObject);
                            /*TODO: TOAST OR SNACKBAR ON SUCCESS/FAILURE*/
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


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
                Intent goFave = new Intent(this, FavoriteTracks.class);
                goFave.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(goFave);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
