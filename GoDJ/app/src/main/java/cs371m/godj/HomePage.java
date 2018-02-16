package cs371m.godj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.player.Metadata;

/**
 * Created by Jasmine on 12/1/2016.
 */

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {





    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    protected static String userName;
    protected boolean screenIsBlank;
    protected static MyEventsFragment mef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_events_layout);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        mef = new MyEventsFragment();

        if(getIntent().hasExtra("userName") && getIntent().getStringExtra("userName") != null) {
            userName = getIntent().getStringExtra("userName");
        } else {
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        // from witchel class code
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerMenu = navigationView.getMenu();

        updateUserDisplay();


        if(getIntent().hasExtra("menuItemID")) {
            screenIsBlank = true;
            int id = getIntent().getIntExtra("menuItemID", R.id.my_events);
            switch(id) {
                case R.id.host_event :
                    toggleHamburgerToBack();
                    CreateEventFragment cef = CreateEventFragment.newInstance();
                    Bundle b = new Bundle();
                    b.putBoolean("returnToSearch", true);
                    cef.setArguments(b);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, cef)
                            .addToBackStack(null)
                            .commit();
                    break;

                case R.id.find_event :
                    toggleHamburgerToBack();
                    EventSearchFragment esf = EventSearchFragment.newInstance();
                    Bundle b2 = new Bundle();

                    b2.putBoolean("returnToSearch", true);

                    esf.setArguments(b2);

                    getSupportFragmentManager().beginTransaction()

                            .replace(R.id.main_frame, esf, "searchFrag")
                            .addToBackStack("searchFrag")
                            .commit();
                    break;

                case R.id.saved_songs:
                    SavedSongsFragment savedSongsFragment = new SavedSongsFragment();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.main_frame, savedSongsFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                    break;

                default :
                    screenIsBlank = false;
                    showMyEventFrag();

            }
        } else {
            screenIsBlank = false;
            showMyEventFrag();

        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if(backStackCount == 0) {
                    toggle.setDrawerIndicatorEnabled(true);

                    if(screenIsBlank) {
                        showMyEventFrag();
                        screenIsBlank = false;
                    }
                } else if(toggle.isDrawerIndicatorEnabled()) {
                    toggleHamburgerToBack();
                }
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
        MenuItem logMenu = drawerMenu.findItem(R.id.nav_login);
        if (logMenu != null) {
            logMenu.setTitle(loginString);
            logMenu.setTitleCondensed(loginString);
        }
    }


    // If we return to MainActivity from fragment via Back button,
    // make sure drawer is closed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // adapted from witchel class code
    protected void toggleHamburgerToBack() {
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(getDrawerToggleDelegate().getThemeUpIndicator());
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if(backStackCount > 0) {
                    getSupportFragmentManager().popBackStack();
                }
            }
        });

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
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startLoginScreen);
                return true;
            } else {
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startLoginScreen);
                return true;
            }
        } else if(id == R.id.host_event) {
            toggleHamburgerToBack();
            CreateEventFragment cef = CreateEventFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, cef);
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.song_search) {
            getSupportFragmentManager().popBackStack();
            Intent startUserMain = new Intent(getApplicationContext(), UserMainActivity.class);
            startActivity(startUserMain);
        } else if(id == R.id.find_event) {
            toggleHamburgerToBack();
            EventSearchFragment esf = EventSearchFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, esf, "searchFrag");
            ft.addToBackStack("searchFrag");
            ft.commit();

        } else if(id == R.id.saved_songs) {
            SavedSongsFragment savedSongsFragment = new SavedSongsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, savedSongsFragment);
            ft.addToBackStack(null);
            ft.commit();

        } else if(id == R.id.user_playing) {
            if(MainActivity.mPlayer.getPlaybackState().isActiveDevice) {

                getSupportFragmentManager().popBackStack();

                Intent showTrackPage = new Intent(getApplicationContext(), TrackPageActivity.class);

                Metadata.Track track = MainActivity.mPlayer.getMetadata().currentTrack;

                showTrackPage.putExtra("trackName", track.name);
                showTrackPage.putExtra("artistName", track.artistName);
                showTrackPage.putExtra("imageURL", track.albumCoverWebUrl);
                showTrackPage.putExtra("albumName", track.albumName);
                showTrackPage.putExtra("trackURI", track.uri);

                startActivity(showTrackPage);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showMyEventFrag() {
        mef = new MyEventsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putString("userName", userName);
        ft.add(R.id.main_frame, mef);
        ft.commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mPlayer.destroy(); // TODO: might cause a crash but working for now
    }
}