package cs371m.godj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/*TODO: OPTION TO OPEN TRACK IN SPOTIFY OR PLAY IN APP*/
public class TrackPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String trackName;
    private String artistName;
    private String imageURL;
    private String albumName;
    private String trackURI;


    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    protected String userName;



    public static final int REQ_LIMIT = 5;
    public static final long ONE_HOUR = 3600000;

    public ViewGroup viewGroup;


    public static final int PLAY_PAUSE_BUT = 0;
    public static final int FORWARD_BUT = 1;
    public static final int BACK_BUT = 2;

    public static final int REMAINING_TIME = 0;
    public static final int CURRENT_TIME = 1;

    //private String[] playlist; // songs in the playlist
    // private ListView songs; // ListView to display the songs
    private PlayerStatus status; // used to control the media playback
    private SeekBar mySeekBar; // seekbar for this app
    private Boolean isLooping; // did user set song to loop?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff); // MIGHT JUST CHANGE THEME IN STYLE XML INSTEAD

        setSupportActionBar(toolbar);







        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout3);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateUserDisplay();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view3);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();


        updateUserDisplay();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

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


        //MUSIC PLAYER STUFF

        // array of user control buttons
        ImageButton[] userButs = new ImageButton[3];
        userButs[PLAY_PAUSE_BUT] = (ImageButton) findViewById(R.id.pause_button);
        userButs[FORWARD_BUT] = (ImageButton) findViewById(R.id.next_button);
        userButs[BACK_BUT] = (ImageButton) findViewById(R.id.prev_button);

        // array of textviews I need to edit during playback
        TextView[] tvArr = new TextView[2];
        tvArr[REMAINING_TIME] = (TextView) findViewById(R.id.timeLeft);
        tvArr[CURRENT_TIME] = (TextView) findViewById(R.id.timeProgress);




        mySeekBar = (SeekBar) findViewById(R.id.seekBar);
        status = new PlayerStatus(getApplicationContext(), userButs, tvArr, mySeekBar, trackURI);
        isLooping = false;


        /*TODO: To change track info when track changes*/
//        MainActivity.mPlayer.addNotificationCallback(new Player.NotificationCallback() {
//            @Override
//            public void onPlaybackEvent(PlayerEvent playerEvent) {
//                switch(playerEvent) {
//                    /*TODO: I DON'T WANT LISTENERS IN THREE PLACES*/
//                    case kSpPlaybackNotifyTrackChanged:
//                        trackName = MainActivity.mPlayer.getMetadata().currentTrack.name;
//                        artistName = MainActivity.mPlayer.getMetadata().currentTrack.artistName;
//                        imageURL = MainActivity.mPlayer.getMetadata().currentTrack.albumCoverWebUrl;
//                        albumName = MainActivity.mPlayer.getMetadata().currentTrack.albumName;
//                        trackURI = MainActivity.mPlayer.getMetadata().currentTrack.uri;
//
//                        ImageView art = (ImageView) findViewById(R.id.album_art);
//
//                        Picasso.with(getApplicationContext()).load(imageURL).into(art);
//
//                        TextView trackTV = (TextView) findViewById(R.id.track_info);
//                        TextView artistTV = (TextView) findViewById(R.id.artist_info);
//
//                        trackTV.setText(trackName);
//                        trackTV.setSelected(true);
//                        trackTV.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//                        trackTV.setMarqueeRepeatLimit(-1); // repeat indefinitely
//                        trackTV.setMaxLines(1);
//                        trackTV.setFocusable(true);
//                        trackTV.setFocusableInTouchMode(true);
//                        trackTV.setHorizontallyScrolling(true);
//                        artistTV.setText(artistName);
//                }
//            }
//
//            @Override
//            public void onPlaybackError(Error error) {
//
//            }
//        });


        ///

        ImageButton addSongBut = (ImageButton) findViewById(R.id.add_song_button);
        addSongBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] trackInfo = {trackName, albumName, artistName, imageURL, trackURI};
                TrackDatabaseObject trackDatabaseObject = new TrackDatabaseObject(trackName, artistName,
                        albumName, trackURI, imageURL, 1);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String _userName = user.replaceAll("\\.", "@"); // . illegal in Firebase key
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(_userName)
                        .child("savedSongs").child(trackURI).setValue(trackDatabaseObject);
                Snackbar snack = Snackbar.make(viewGroup, "Song Saved!", Snackbar.LENGTH_LONG);
                View view = snack.getView();
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snack.show();
            }
        });

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
                                                        final EventObject eventObject = dataSnapshot.getValue(EventObject.class);
                                                        long eventStart = eventObject.getStartTime();
                                                        long eventEnd = eventObject.getEndTime();
                                                        long currTime = System.currentTimeMillis();

                                                        final String eventName = eventObject.getEventName();


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
                                                                                    Date d = new Date(requestObject.getNextAvailableRequest());
                                                                                    SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                                                                                    String nextTime = sdf.format(d);
                                                                                    Snackbar snack = Snackbar.make(viewGroup, "Your request limit has been reached. Requests available again at " + nextTime + ".", Snackbar.LENGTH_LONG);
                                                                                    View view = snack.getView();
                                                                                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                                                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                                    snack.show();
                                                                                }
                                                                            } else if (requestObject.getNumRequests() < REQ_LIMIT) {
                                                                                Query q = db.child("eventPlaylists")
                                                                                        .child(currEvent)
                                                                                        .orderByChild("trackName")
                                                                                        .equalTo(trackName);
                                                                                q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot.getValue() == null) {
                                                                                            TrackDatabaseObject trackDatabaseObject = new TrackDatabaseObject(trackName,
                                                                                                    artistName, albumName, trackURI, imageURL, 1);

                                                                                            db.child("eventPlaylists")
                                                                                                    .child(currEvent).child(trackURI)
                                                                                                    .setValue(trackDatabaseObject, trackDatabaseObject.getPriority());

                                                                                        } else {

                                                                                            TrackDatabaseObject trackDatabaseObject = dataSnapshot.getChildren()
                                                                                                    .iterator().next().getValue(TrackDatabaseObject.class);
                                                                                            int priority = trackDatabaseObject.getPriority() + 1;
                                                                                            trackDatabaseObject.setPriority(priority);

                                                                                            FirebaseDatabase.getInstance().getReference("eventPlaylists")
                                                                                                    .child(eventObject.getKey()).child(trackDatabaseObject
                                                                                                    .getTrackURI()).setValue(trackDatabaseObject, 0 - priority);
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                                int reqs = requestObject.getNumRequests() + 1;
                                                                                requestObject.setNumRequests(reqs);
                                                                                reqRef.setValue(requestObject);
                                                                                Snackbar snack = Snackbar.make(viewGroup, "Song Requested!", Snackbar.LENGTH_LONG);
                                                                                View view = snack.getView();
                                                                                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                                                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                                                                snack.show();
                                                                            }
                                                                        }
                                                                    } else {

                                                                        Query q = db.child("eventPlaylists")
                                                                                .child(currEvent)
                                                                                .orderByChild("trackName")
                                                                                .equalTo(trackName);
                                                                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot.getValue() == null) {
                                                                                    TrackDatabaseObject trackDatabaseObject = new TrackDatabaseObject(trackName,
                                                                                            artistName, albumName, trackURI, imageURL, 1);

                                                                                    db.child("eventPlaylists")
                                                                                            .child(currEvent).child(trackURI)
                                                                                            .setValue(trackDatabaseObject, trackDatabaseObject.getPriority());

                                                                                } else {

                                                                                    TrackDatabaseObject trackDatabaseObject = dataSnapshot.getChildren()
                                                                                            .iterator().next().getValue(TrackDatabaseObject.class);
                                                                                    int priority = trackDatabaseObject.getPriority() + 1;
                                                                                    trackDatabaseObject.setPriority(priority);

                                                                                    FirebaseDatabase.getInstance().getReference("eventPlaylists")
                                                                                            .child(eventObject.getKey()).child(trackDatabaseObject
                                                                                            .getTrackURI()).setValue(trackDatabaseObject, 0 - priority);
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                        long startTime = System.currentTimeMillis();
                                                                        RequestObject requestObject1 = new RequestObject(1,
                                                                                startTime, startTime + ONE_HOUR);
                                                                        reqRef.setValue(requestObject1);
                                                                        Snackbar snack = Snackbar.make(viewGroup, "Song Requested!", Snackbar.LENGTH_LONG);
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
                                                            String message = (currTime > eventStart) ? "Request Failed. " + eventName + " has already ended." : "Request Failed. " + eventName + " has not yet started.";
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



    // from witchel class code
    // We have logged in or out, update all items that display user name
    protected void updateUserDisplay() {
        String loginString = "";
        String userString = userName;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            loginString = String.format("Log out as %s", userName);
        } else {
            userString = "Please log in";
            loginString = "Login";
        }
        TextView dit = (TextView) findViewById(R.id.drawerIDText);
        if (dit != null) {
            dit.setText(userString);
        }
        // findViewById does not work for menu items.
        MenuItem logMenu = (MenuItem) drawerMenu.findItem(R.id.nav_login);
        if (logMenu != null) {
            logMenu.setTitle(loginString);
            logMenu.setTitleCondensed(loginString);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("main", "menu option selected");
        if (id == R.id.nav_login) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut(); // Will call updateUserDisplay via callback
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(startLoginScreen);
                return true;
            } else {
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(startLoginScreen);
                return true;
            }
        } else if (id == R.id.my_events) {
            Intent goHome = new Intent(this, HomePage.class);
            goHome.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            goHome.putExtra("menuItemID", id);
            startActivity(goHome);
        } else if (id == R.id.user_playing) {


        } else if (id == R.id.song_search) {
            getSupportFragmentManager().popBackStack();
            Intent startUserMain = new Intent(getApplicationContext(), UserMainActivity.class);
            startActivity(startUserMain);
         }else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }



   /* @Override
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
    }*/


}
