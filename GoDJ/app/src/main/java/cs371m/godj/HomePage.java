package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
    protected static ActionBarDrawerToggle toggle;
    protected String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_events_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        userName = getIntent().getStringExtra("userName");

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        // Toggle back button to hamburger
        //toggle.setDrawerIndicatorEnabled(true);
    }

    protected void toggleHamburgerToBack() {
        // Ideas on how to transition toggle from
        // https://stackoverflow.com/questions/28263643/tool-bar-setnavigationonclicklistener-breaks-actionbardrawertoggle-functionality/30951016#30951016
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(getDrawerToggleDelegate().getThemeUpIndicator());
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportFragmentManager().getBackStackEntryCount() <= 1) {
                    toggle.setDrawerIndicatorEnabled(true);
                }
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
                FirebaseAuth.getInstance().signOut(); // Will call updateUserDisplay via callback
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startLoginScreen);
                return true;
            } else {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                //drawerLayout.setBackgroundColor(0x000);
                toggleHamburgerToBack();
                FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace any other Fragment with our new Details Fragment with the right data
                ft.add(R.id.main_frame, flf);
                // Let us come back
                ft.addToBackStack(null);
                // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else if(id == R.id.host_event) {
            toggleHamburgerToBack();
            getSupportFragmentManager().popBackStack();
            CreateEventFragment cef = CreateEventFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace any other Fragment with our new Details Fragment with the right data
            ft.add(R.id.main_frame, cef);
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
            getSupportFragmentManager().popBackStack();
            toggleHamburgerToBack();
            EventSearch esf = EventSearch.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            // Replace any other Fragment with our new Details Fragment with the right data
            ft.add(R.id.main_frame, esf);
            // Let us come back
            ft.addToBackStack(null);
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        } else if(id == R.id.my_events) {
            //toggleHamburgerToBack();
            getSupportFragmentManager().popBackStack();

            MyEventsFragment mef = new MyEventsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace any other Fragment with our new Details Fragment with the right data
            ft.add(R.id.main_frame, mef);
            // Let us come back
            ft.addToBackStack(null);
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
        //} else if (id == R.id.nav_send) {
        //}
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
