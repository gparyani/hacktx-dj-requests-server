package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.design.widget.Snackbar.make;

/**
 * Created by Jasmine on 10/23/2016.
 */

public class TrackPageActivity extends AppCompatActivity {

    private String trackName;
    private String artistName;
    private String imageURL;
    private String albumName;
    private String trackURI;

    public static final int REQ_LIMIT = 5;
//    public static final long ONE_HOUR = 60000;
    public static final long ONE_HOUR = 3600000;


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
                if (!UserMainActivity.faveTrackMap.containsKey(trackURI)) {
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

        /*TODO: limit request checks, check if event is live*/
        ImageButton reqBut = (ImageButton) findViewById(R.id.req_song_button);
        reqBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                final String thisUserName = userName.replaceAll("\\.", "@");
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child("users").child(thisUserName).child("eventAttending")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String currEvent = (String) dataSnapshot.getValue();
                        if (!currEvent.equals("none")) {
                            final DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference()
                                    .child("events");
                                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(currEvent)) {
                                                eventsRef.child(currEvent).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        EventObject eventObject = dataSnapshot.getValue(EventObject.class);
                                                        long eventStart = eventObject.getStartTime();
                                                        long eventEnd = eventObject.getEndTime();
                                                        long currTime = System.currentTimeMillis();


                                                        if(currTime > eventStart && currTime < eventEnd) {
                                                            final DatabaseReference reqRef = FirebaseDatabase.getInstance()
                                                                    .getReference("requests")
                                                                    .child(thisUserName).child(currEvent);
                                                            reqRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    RequestObject requestObject = dataSnapshot.getValue(RequestObject.class);
                                                                    if(requestObject != null) {
                                                                        if (requestObject.getNumRequests() > 0) {
                                                                            if (requestObject.getNumRequests() == REQ_LIMIT) {
                                                                                long currTime = System.currentTimeMillis();
                                                                                if (currTime > requestObject.getNextAvailableRequest()) {
                                                                                    requestObject.setNumRequests(1);
                                                                                    requestObject.setFirstRequestTime(currTime);
                                                                                    requestObject.setNextAvailableRequest(currTime + ONE_HOUR);
                                                                                    reqRef.setValue(requestObject);
                                                                                } else {
                                                                        /*TODO: Not quite on the dot timewise*/
                                                                                    System.out.println("REQ_LIMIT REACHED");

                                                                                    Date d = new Date(requestObject.getNextAvailableRequest());
                                                                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                                                                                    String nextTime = sdf.format(d);
                                                                                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                                                                                    Snackbar snack = Snackbar.make(viewGroup, "Request Limit Reached. Next request available at: " + nextTime, Snackbar.LENGTH_LONG);
                                                                                    View view = snack.getView();
                                                                                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                                                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                                    snack.show();
                                                                                }
                                                                            } else if (requestObject.getNumRequests() < REQ_LIMIT) {
                                                                                int reqs = requestObject.getNumRequests() + 1;
                                                                                requestObject.setNumRequests(reqs);
                                                                                reqRef.setValue(requestObject);
                                                                            }
                                                                        }
                                                                    } else {
                                                                        long startTime = System.currentTimeMillis();
                                                                        RequestObject requestObject1 = new RequestObject(1,
                                                                                startTime, startTime + ONE_HOUR);
                                                                        reqRef.setValue(requestObject1);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                            Query q = db.child("eventPlaylists")
                                                                    .child(currEvent)
                                                                    .orderByChild("trackName")
                                                                    .equalTo(trackName);
                                                            q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.getValue() == null) {
                                                                        TrackDatabaseObject trackDatabaseObject = new TrackDatabaseObject(trackName,
                                                                                artistName, albumName, trackURI, 1);

                                                                        db.child("eventPlaylists")
                                                                                .child(currEvent).child(trackURI)
                                                                                .setValue(trackDatabaseObject, trackDatabaseObject.getPriority());

                                                        /*TODO: TOAST OR SNACKBAR ON SUCCESS/FAILURE*/
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                        } else {
                                                            String message = (currTime > eventStart) ? "This event has already ended" : "This event has not yet started";
                                                            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                                                            Snackbar snack = Snackbar.make(viewGroup, message, Snackbar.LENGTH_LONG);
                                                            View view = snack.getView();
                                                            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                            snack.show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                            } else {
                                                ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                                                Snackbar snack = make(viewGroup, "Cannot request. The event you are attending no longer exists.", Snackbar.LENGTH_LONG);
                                                View view = snack.getView();
                                                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                snack.show();
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
