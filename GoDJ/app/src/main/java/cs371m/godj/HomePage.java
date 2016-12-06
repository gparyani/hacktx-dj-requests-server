package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine on 12/1/2016.
 */

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Menu drawerMenu;
    protected ActionBarDrawerToggle toggle;
    protected static String userName;
    protected boolean screenIsBlank;
    protected static MyEventsFragment mef;

    protected List<Fragment> activeFrags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_events_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
        EditText et = (EditText) findViewById(R.id.searchTerm);
        et.setVisibility(View.GONE);
        setSupportActionBar(toolbar);

        mef = new MyEventsFragment();
        activeFrags = new ArrayList<>();

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

        showMyEventFrag();


//        if(getIntent().hasExtra("menuItemID")) {
//            screenIsBlank = true;
//            int id = getIntent().getIntExtra("menuItemID", R.id.my_events);
//            switch(id) {
//
//                default :
//                    screenIsBlank = false;
//                    showMyEventFrag();
//
//            }
//        } else {
//            screenIsBlank = false;
//            showMyEventFrag();
//
//        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                /*TODO: See if togglehamburgertoback can just be called here*/
                int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                if(backStackCount == 0) {
//                    toggle.setDrawerIndicatorEnabled(true);
//                    System.out.println("screen is blank: " + screenIsBlank);

                    if(screenIsBlank) {
                        showMyEventFrag(); ///////////////////////////////////
                        screenIsBlank = false;
                    }
//                } else if(toggle.isDrawerIndicatorEnabled()) {
//                    //toggleHamburgerToBack();
                }
//                List<Fragment> fragList = getSupportFragmentManager().getFragments();
                System.out.println("fragList size: " + activeFrags.size());
                System.out.println("backstack count: " + getSupportFragmentManager().getBackStackEntryCount());

                if(activeFrags.size() != getSupportFragmentManager().getBackStackEntryCount() + 1) {
                    activeFrags.remove(activeFrags.size() - 1);
                    if(activeFrags.size() > 0) {
                        getSupportFragmentManager().beginTransaction().show(activeFrags.get(activeFrags.size() - 1)).commit();
                    }
                }

                Fragment f = activeFrags.get(activeFrags.size() - 1);
                if(!(f instanceof MyEventsFragment || f instanceof UserMainFragment)) {
                    toggleHamburgerToBack();
                } else if(!toggle.isDrawerIndicatorEnabled()){
                    toggle.setDrawerIndicatorEnabled(true);
                }


//                if(fragList.size() > 0) {
//                    fragList.remove(fragList.size() - 1);
//                }
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
        //getSupportFragmentManager().popBackStack();
        //getSupportFragmentManager().executePendingTransactions();
        int id = item.getItemId();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        //int index = (backStackCount > 0) ? backStackCount - 1 : backStackCount;
        Fragment f = activeFrags.get(activeFrags.size() - 1);
        System.out.println("fragList size navSelect: " + activeFrags.size());
//
            System.out.println("first: " + f);
//        if(mef.isVisible() || backStackCount == 0) {
//            f = mef;
//        } else {
//            f = activeFrags.get(backStackCount);
//            System.out.println("fragList size navSelect: " + fragList.size());
//
//            System.out.println("first: " + f);
//        }
        Log.d("main", "menu option selected");
        if (id == R.id.nav_login) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().signOut(); // Will call updateUserDisplay via callback
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish(); //
                startActivity(startLoginScreen);
                return true;
            } else {
                Intent startLoginScreen = new Intent(this, MainActivity.class);
                startLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish(); //
                startActivity(startLoginScreen);
                return true;
            }
        } else if(id == R.id.host_event) {
            //toggleHamburgerToBack();
            CreateEventFragment cef = CreateEventFragment.newInstance();
            activeFrags.add(cef);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//            if(backStackCount > 0) {
//                ft.add(R.id.main_frame, cef);
//                ft.hide(getSupportFragmentManager().getFragments().get(index));
//            } else {
                ft.add(R.id.main_frame, cef);
                ft.hide(f);

//            }
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.song_search) {
            //getSupportFragmentManager().popBackStack();
//            Intent startUserMain = new Intent(getApplicationContext(), UserMainFragment.class);
//            startActivity(startUserMain);

            if(!(f instanceof UserMainFragment)) {
                UserMainFragment userMainFragment = new UserMainFragment();
                activeFrags.add(userMainFragment);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//            if(backStackCount > 0) {
                ft.add(R.id.main_frame, userMainFragment);
                ft.hide(f);
//            } else {
//                ft.add(R.id.main_frame, userMainFragment);
//                ft.hide(mef);
//            }
                ft.addToBackStack(null);
                ft.commit();
            }
        } else if(id == R.id.find_event) {
            //toggleHamburgerToBack();
            EventSearchFragment esf = EventSearchFragment.newInstance();
            activeFrags.add(esf);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


//            if(backStackCount > 0) {
//                ft.add(R.id.main_frame, esf, "searchFrag");
//                ft.hide(getSupportFragmentManager().getFragments().get(backStackCount));
//            } else {
                ft.add(R.id.main_frame, esf, "searchFrag");
                ft.hide(f);
//            }
            ft.addToBackStack("searchFrag");
            ft.commit();

        } else if(id == R.id.my_events) {
            if(!mef.isVisible()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.show(mef);
//                Fragment f = getSupportFragmentManager().getFragments().get(backStackCount);
            System.out.println(f);
                ft.hide(f);


                activeFrags.add(mef);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showMyEventFrag() {
        mef = new MyEventsFragment();
        activeFrags.add(mef);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putString("userName", userName);
        // Replace any other Fragment with our new Details Fragment with the right data
        ft.add(R.id.main_frame, mef);
        // Let us come back
//        ft.addToBackStack(null);
        // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
        ft.commit();
    }
}
