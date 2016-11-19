package cs371m.godj;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        FirebaseLoginFragment.FirebaseLoginInterface{

    private List<Track> trackList;
    private List<Artist> artistList;
    private List<AlbumSimple> albumList;

    private List<Track> displayTrackList;
    private List<Artist> displayArtistList;
    private List<AlbumSimple> displayAlbumList;

    private ListView listView;
    private ListView artistListView;
    private ListView albumListView;

    private SpotifyItemAdapter spotifyItemAdapter;
    private ArtistItemAdapter artistItemAdapter;
    private AlbumItemAdapter albumItemAdapter;

    private TextView textView;
    private TextView artistTextView;
    private TextView albumTextView;

    private TextView trackFooter;
    private TextView artistFooter;
    private TextView albumFooter;

    private EditText et;
    private String userSearchInput;


    public static ArrayList<String[]> favoriteTracks;
    public static HashMap<String, String> faveTrackMap; // for checking if track already in list
    public static boolean clearSearch;
    private Handler myHandler;


    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    protected String userName;
    private Handler signInHandler;

    public static String TAG = "GoDJ";


    protected void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userName = user.getDisplayName();
                    signInHandler.post(new showOpeningScreen(userName));
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userName = null;
                    signInHandler.post(new showOpeningScreen(userName));
                }
                Log.d(TAG, "userName="+userName);
//                if( photoFragment != null ) {
//                    photoFragment.updateCurrentUserName(userName);
//                }
                updateUserDisplay();
            }
        };
    }

    class showOpeningScreen implements Runnable {

        private String userName;

        public showOpeningScreen(String user) {
            userName = user;
        }

        @Override
        public void run() {
            if(userName == null) {
                toggleHamburgerToBack();
                EditText et = (EditText) findViewById(R.id.searchTerm);
                et.setVisibility(View.INVISIBLE);
//                FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                // Replace any other Fragment with our new Details Fragment with the right data
//                ft.add(R.id.main_frame, flf);
//                // Let us come back
//                ft.addToBackStack(null);
//                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                ft.commit();
            } else {


                /////temporary
                EditText et = (EditText) findViewById(R.id.searchTerm);
                et.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle(""); // MIGHT JUST CHANGE THEME IN STYLE XML INSTEAD

        setSupportActionBar(toolbar);


        signInHandler = new Handler();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Putting it here means you can see it change
                updateUserDisplay();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();

        firebaseInit();

        updateUserDisplay();




        trackList = new ArrayList<>();
        artistList = new ArrayList<>();
        albumList = new ArrayList<>();

        // Set of lists for tracks that are displayed (not full track list)
        displayTrackList = new ArrayList<>();
        displayArtistList = new ArrayList<>();
        displayAlbumList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_view);
        artistListView = (ListView) findViewById(R.id.artist_list_view);
        albumListView = (ListView) findViewById(R.id.album_list_view);

        spotifyItemAdapter = new SpotifyItemAdapter(this);
        artistItemAdapter = new ArtistItemAdapter(this);
        albumItemAdapter = new AlbumItemAdapter(this);

        // track listview header
        textView = new TextView(this);
        textView.setText("Songs");
        textView.setTextColor(0xffffffff);
        textView.setGravity(0x01);
        textView.setTextSize(20);
        textView.setTypeface(textView.getTypeface(), 1);
        textView.setPadding(0,0,0,50);
        listView.addHeaderView(textView, null, false);
        textView.setVisibility(View.INVISIBLE);

        // track listview footer
        trackFooter = new TextView(this);
        trackFooter.setText("See more songs");
        trackFooter.setTextColor(0xffffffff);
        trackFooter.setPadding(0,15,0,0);
        listView.addFooterView(trackFooter);
        trackFooter.setVisibility(View.INVISIBLE);


        //artist listview header
        artistTextView = new TextView(this);
        artistTextView.setText("Artists");
        artistTextView.setTextColor(0xffffffff);
        artistTextView.setGravity(0x01);
        artistTextView.setTextSize(20);
        artistTextView.setTypeface(artistTextView.getTypeface(), 1);
        artistTextView.setPadding(0,0,0,50);
        artistListView.addHeaderView(artistTextView, null, false);
        artistTextView.setVisibility(View.INVISIBLE);

        //artist listview footer
        artistFooter = new TextView(this);
        artistFooter.setText("See more artists");
        artistFooter.setTextColor(0xffffffff);
        artistFooter.setPadding(0,15,0,0);
        artistListView.addFooterView(artistFooter);
        artistFooter.setVisibility(View.INVISIBLE);


        // album listview header
        albumTextView = new TextView(this);
        albumTextView.setText("Albums");
        albumTextView.setTextColor(0xffffffff);
        albumTextView.setGravity(0x01);
        albumTextView.setTextSize(20);
        albumTextView.setTypeface(albumTextView.getTypeface(), 1);
        albumTextView.setPadding(0,0,0,50);
        albumListView.addHeaderView(albumTextView, null, false);
        albumTextView.setVisibility(View.INVISIBLE);

        // album listview footer
        albumFooter = new TextView(this);
        albumFooter.setText("See more albums");
        albumFooter.setTextColor(0xffffffff);
        albumFooter.setPadding(0,15,0,0);
        albumListView.addFooterView(albumFooter);
        albumFooter.setVisibility(View.INVISIBLE);

        listView.setAdapter(spotifyItemAdapter);
        artistListView.setAdapter(artistItemAdapter);
        albumListView.setAdapter(albumItemAdapter);

        faveTrackMap = new HashMap<>();
        favoriteTracks = new ArrayList<>();
        clearSearch = false; // only clear search if user comes back to search page by selecting from menu, not from back button
        myHandler = new Handler();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    Intent showAllSongResults = new Intent(getApplicationContext(), ShowAllSongResults.class);
                    showAllSongResults.putParcelableArrayListExtra("list", (ArrayList) trackList);
                    showAllSongResults.putExtra("searchTerm", userSearchInput);
                    startActivity(showAllSongResults);
                } else {

                    Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);

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

                    startActivity(showTrackPage);
                }
            }
        });

        // set on item listener for artistListView
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    Intent showAllArtistResults = new Intent(getApplicationContext(), ShowAllArtistResults.class);
                    showAllArtistResults.putParcelableArrayListExtra("list", (ArrayList) artistList);
                    showAllArtistResults.putExtra("searchTerm", userSearchInput);
                    startActivity(showAllArtistResults);
                } else {

                    TextView tv = (TextView) findViewById(R.id.artist_id);
                    String artistID = tv.getText().toString();

                    SpotifyApi api = new SpotifyApi();
                    SpotifyService spotify = api.getService();

                    spotify.getArtistTopTrack(artistID, "US", new Callback<Tracks>() {
                                @Override
                                public void success(Tracks tracks, Response response) {
                                    myHandler.post(new TopTracks(tracks));

                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                    });


                    spotify.getArtistAlbums(artistID, new Callback<Pager<Album>>() {
                        @Override
                        public void success(Pager<Album> albumPager, Response response) {

                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            }
        });

        albumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int footerPos = parent.getCount() - 1;

                if (footerPos == position) {
                    Intent showAllArtistResults = new Intent(getApplicationContext(), ShowAllAlbumResults.class);
                    showAllArtistResults.putParcelableArrayListExtra("list", (ArrayList) albumList);
                    showAllArtistResults.putExtra("searchTerm", userSearchInput);
                    startActivity(showAllArtistResults);
                } else {
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
            }
        });


        et = (EditText) findViewById(R.id.searchTerm);
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            processSearch();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }


    // We have logged in or out, update all items that display user name
    protected void updateUserDisplay() {
        String loginString = "";
        String userString = userName;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ;
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
    public void firebaseLoginFinish() {
        // Dismiss the Login fragment
        getFragmentManager().popBackStack();
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);



        /////temporary

    }

    @Override
    public void firebaseFromLoginToCreateAccount() {
        // Dismiss the Login fragment
        getFragmentManager().popBackStack();
        // Toggle back button to hamburger
        toggle.setDrawerIndicatorEnabled(true);
        toggleHamburgerToBack();

        // Replace main screen with the create account fragment
        FirebaseCreateNewAccountFragment fcaf = FirebaseCreateNewAccountFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_frame, fcaf);
        // Let us pop without explicit fragment remove
        ft.addToBackStack(null);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    protected void toggleHamburgerToBack() {
        // Ideas on how to transition toggle from
        // https://stackoverflow.com/questions/28263643/tool-bar-setnavigationonclicklistener-breaks-actionbardrawertoggle-functionality/30951016#30951016
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(getDrawerToggleDelegate().getThemeUpIndicator());
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseLoginFinish();
            }
        });
        // Maybe this would be better, but above works.
        //http://stackoverflow.com/questions/27742074/up-arrow-does-not-show-after-calling-actionbardrawertoggle-setdrawerindicatorena
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("main", "menu option selected");
        if (id == R.id.nav_login) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                mAuth.signOut(); // Will call updateUserDisplay via callback
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
                drawer.closeDrawer(Gravity.LEFT);

                Intent goHome = new Intent(this, MainActivity.class);
                goHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(goHome);
                return true;
            } else {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
                //drawerLayout.setBackgroundColor(0x000);
                toggleHamburgerToBack();
                FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Replace any other Fragment with our new Details Fragment with the right data
                ft.add(R.id.main_frame, flf);
                // Let us come back
                ft.addToBackStack(null);
                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else if (id == R.id.song_search) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
            drawer.closeDrawer(Gravity.LEFT);
//          Intent startUserMain = new Intent(getApplicationContext(), UserMainActivity.class);
//          startActivity(startUserMain);

        } else if (id == R.id.my_events) {
            Intent goHome = new Intent(this, MainActivity.class);
            goHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(goHome);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class TopTracks implements Runnable {

        private Tracks topTracks;

        public TopTracks(Tracks t) {
            topTracks = t;
        }


        @Override
        public void run() {
            Intent showArtistPage = new Intent(getApplicationContext(), ArtistPageActivity.class);




        }

    }

    private class AlbumSelected implements Runnable {

        private Album album;

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

            startActivity(showAlbumPage);
        }
    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                return;
            }
            int height = 0;
            int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(), MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        switch (id) {
//            case R.id.search_ID:
//                break;
//            case R.id.favorites_ID:
//                launchFavorites();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private void launchFavorites() {
        Intent startFavorites = new Intent(this, FavoriteTracks.class);
        startFavorites.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startFavorites);
    }


    class UpdateTrackSearchResults implements Runnable {
        @Override
        public void run() {
            textView.setVisibility(View.VISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            trackFooter.setVisibility(View.VISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            spotifyItemAdapter.changeList(displayTrackList);
            ListUtils.setDynamicHeight(listView);
            spotifyItemAdapter.notifyDataSetChanged();

        }
    }

    class UpdateArtistSearchResults implements Runnable {
        @Override
        public void run() {
            artistTextView.setVisibility(View.VISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistFooter.setVisibility(View.VISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistItemAdapter.changeList(displayArtistList);
            ListUtils.setDynamicHeight(artistListView);
            artistItemAdapter.notifyDataSetChanged();
        }
    }

    class UpdateAlbumSearchResults implements Runnable {
        @Override
        public void run() {
            albumTextView.setVisibility(View.VISIBLE);
            albumItemAdapter.notifyDataSetChanged();
            albumFooter.setVisibility(View.VISIBLE);
            albumItemAdapter.notifyDataSetChanged();
            albumItemAdapter.changeList(displayAlbumList);
            ListUtils.setDynamicHeight(albumListView);
            albumItemAdapter.notifyDataSetChanged();
        }
    }


    protected void processSearch() {
        EditText et = (EditText) findViewById(R.id.searchTerm);
        String searchTerm = et.getText().toString();
        userSearchInput = searchTerm;

        trackList.clear();
        artistList.clear();
        albumList.clear();

        displayTrackList.clear();
        displayArtistList.clear();
        displayAlbumList.clear();

        spotifyItemAdapter.notifyDataSetChanged();
        artistItemAdapter.notifyDataSetChanged();
        albumItemAdapter.notifyDataSetChanged();

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();

        HashMap<String, Object> options = new HashMap<>();
        int limit = 50;
        options.put(spotify.LIMIT, limit);

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        //api.setAccessToken("myAccessToken");

        textView.setVisibility(View.INVISIBLE);
        spotifyItemAdapter.notifyDataSetChanged();
        artistTextView.setVisibility(View.INVISIBLE);
        artistItemAdapter.notifyDataSetChanged();


        spotify.searchTracks(searchTerm, options, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tracksPager, Response response) {
                trackList = tracksPager.tracks.items;

                int displaySize = Math.min(trackList.size(), 5);

                for(int i = 0; i < displaySize; i++) {
                    displayTrackList.add(trackList.get(i));
                }

                myHandler.post(new UpdateTrackSearchResults());
            }
            @Override
            public void failure(RetrofitError error) {
                Log.d("Tracks: ", "ERROR: " + error.getMessage());
            }
        });

        spotify.searchArtists(searchTerm, options, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                artistList = artistsPager.artists.items;

                int displaySize = Math.min(artistList.size(), 5);

                for(int i = 0; i < displaySize; i++) {
                    displayArtistList.add(artistList.get(i));
                }

                myHandler.post(new UpdateArtistSearchResults());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Artist: ", "ERROR: " + error.getMessage());
            }
        });

        spotify.searchAlbums(searchTerm, new Callback<AlbumsPager>() {
            @Override
            public void success(AlbumsPager albumsPager, Response response) {
                albumList = albumsPager.albums.items;


                int displaySize = Math.min(albumList.size(), 5);
                for(int i = 0; i < displaySize; i++) {
                    displayAlbumList.add(albumList.get(i));
                }

                myHandler.post(new UpdateAlbumSearchResults());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Album: ", "ERROR: " + error.getMessage());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clearSearch) {
            textView.setVisibility(View.INVISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            trackFooter.setVisibility(View.INVISIBLE);
            spotifyItemAdapter.notifyDataSetChanged();
            displayTrackList.clear();
            spotifyItemAdapter.notifyDataSetChanged();
            trackList.clear();

            artistTextView.setVisibility(View.INVISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            artistFooter.setVisibility(View.INVISIBLE);
            artistItemAdapter.notifyDataSetChanged();
            displayArtistList.clear();
            artistItemAdapter.notifyDataSetChanged();
            artistList.clear();

            albumTextView.setVisibility(View.INVISIBLE);
            albumItemAdapter.notifyDataSetChanged();
            albumFooter.setVisibility(View.INVISIBLE);
            albumItemAdapter.notifyDataSetChanged();
            displayAlbumList.clear();
            albumItemAdapter.notifyDataSetChanged();
            albumList.clear();


            EditText et = (EditText) findViewById(R.id.searchTerm);
            et.setText("");
            clearSearch = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
