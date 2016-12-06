package cs371m.godj;

import android.content.Intent;
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

/**
 * Created by Jasmine on 12/1/2016.
 */

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    protected static String userName;
    protected boolean screenIsBlank;
    protected static MyEventsFragment mef = new MyEventsFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_events_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        if(getIntent().hasExtra("userName") && getIntent().getStringExtra("userName") != null) {
            userName = getIntent().getStringExtra("userName");
        } else {
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                    //getSupportFragmentManager().popBackStack();
                    CreateEventFragment cef = CreateEventFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            // Replace any other Fragment with our new Details Fragment with the right data
                            .replace(R.id.main_frame, cef)
                            // Let us come back
                            .addToBackStack(null)
                            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    break;

                case R.id.find_event :
                    //getSupportFragmentManager().popBackStack();
                    toggleHamburgerToBack();
                    EventSearchFragment esf = EventSearchFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()

                            // Replace any other Fragment with our new Details Fragment with the right data
                            .replace(R.id.main_frame, esf, "searchFrag")
                            // Let us come back
                            .addToBackStack("searchFrag")
                            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
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
                /*TODO: See if togglehamburgertoback can just be called here*/
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if(backStackCount == 0) {
                    toggle.setDrawerIndicatorEnabled(true);
                    System.out.println("screen is blank: " + screenIsBlank);

                    if(screenIsBlank) {
                        showMyEventFrag(); ///////////////////////////////////
                        screenIsBlank = false;
                    }
                } else if(toggle.isDrawerIndicatorEnabled()) {
                    toggleHamburgerToBack();
                }
            }
        });



    }



    // We have logged in or out, update all items that display user name
    protected void updateUserDisplay() {
        String loginString = "";
        String userString = userName;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            System.out.println("update username: " + userName);
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


    // If we return to MainActivity from fragment via Back button,
    // make sure drawer is closed
    @Override
    public void onBackPressed() {
        //toggleHelper();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        ;
    }

    protected void toggleHelper() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        System.out.println(backStackCount);
        System.out.println(screenIsBlank);
        if(backStackCount == 1) {
            toggle.setDrawerIndicatorEnabled(true);
            if(screenIsBlank) {
                showMyEventFrag();
            }
        }
    }

    protected void toggleHamburgerToBack() {

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(getDrawerToggleDelegate().getThemeUpIndicator());
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                System.out.println(backStackCount);
                System.out.println(screenIsBlank);
                if(backStackCount > 0) {
                    getSupportFragmentManager().popBackStack();

                }
                // toggleHelper();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("main", "menu option selected");
        if (id == R.id.nav_login) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut(); // Will call updateUserDisplay via callback
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startLoginScreen);
                return true;
            } else {
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startLoginScreen);
                return true;
            }
        } else if(id == R.id.host_event) {
            toggleHamburgerToBack();
            CreateEventFragment cef = CreateEventFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace any other Fragment with our new Details Fragment with the right data
            ft.replace(R.id.main_frame, cef);
            // Let us come back
            ft.addToBackStack(null);
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if (id == R.id.song_search) {
            getSupportFragmentManager().popBackStack();
            Intent startUserMain = new Intent(getApplicationContext(), UserMainActivity.class);
            startActivity(startUserMain);
        } else if(id == R.id.find_event) {
            toggleHamburgerToBack();
            EventSearchFragment esf = EventSearchFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            // Replace any other Fragment with our new Details Fragment with the right data
            ft.replace(R.id.main_frame, esf, "searchFrag");
            // Let us come back
            ft.addToBackStack("searchFrag");
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

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
        // Replace any other Fragment with our new Details Fragment with the right data
        ft.add(R.id.main_frame, mef);
        // Let us come back
//        ft.addToBackStack(null);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}