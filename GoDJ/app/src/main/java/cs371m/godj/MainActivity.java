package cs371m.godj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Jasmine on 11/15/16.
 * Adapted from witchel class code
 *
 *
 *
 * Music player Icons made by Flaticon from www.flaticon.com
 *
 */


public class MainActivity extends AppCompatActivity implements FirebaseCreateAccountFragment.FirebaseCreateAccountInterface,
        FirebaseLoginFragment.FirebaseLoginInterface,
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    protected static String userName;
    protected FirebaseAuth mAuth;
    protected FirebaseAuth fbAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    public static String TAG = "GoDJ";

    private Handler signInHandler;

    private DatabaseHelper dbHelper;


    ///SPOTIFY STUFF
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "6e8cc80c1cd6419484b88a02348aa7e4";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "go-dj-app-auth://callback";

    public static String myToken;

    public static Player mPlayer;

    private static final int REQUEST_CODE = 1337;


    public static SpotifyApi api;
    public static SpotifyService spotify;


    ///


    protected void firebaseInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                fbAuth = firebaseAuth;
//                if(!(getParent() instanceof  UserMainActivity) && !(getParent() instanceof  HomePage) &&
//                        !(getParent() instanceof  TrackPageActivity)) {
                    startSpotify();
//                }

            }
        };
    }


    public void startSpotify() {

        //SPOTIFY STUFF
        System.out.println("MAIN ACTIVITY HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);



    }


    // SPOTIFY STUFF
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                myToken = response.getAccessToken();
                api = new SpotifyApi();
                spotify = api.getService();
                // Most (but not all) of the Spotify Web API endpoints require authorisation.
                // If you know you'll only use the ones that don't require authorisation you can skip this step
                api.setAccessToken(myToken);



                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });

                FirebaseUser user = fbAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userName = user.getDisplayName();
                    Intent startHomeActivity = new Intent(getApplicationContext(), HomePage.class);
                    startHomeActivity.putExtra("userName", userName);
                    startHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(startHomeActivity);

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    userName = null;
                    FirebaseLoginFragment flf = FirebaseLoginFragment.newInstance();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.main_frame, flf);
                    ft.commit();
                }
                Log.d(TAG, "userName="+userName);
                if( dbHelper != null ) {
                    dbHelper.updateCurrentUserName(userName);
                }

            }
        }
    }
    //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.home_page_events_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        signInHandler = new Handler();

        dbHelper = new DatabaseHelper();

        firebaseInit();



    }

    @Override
    public void firebaseLoginFinish() {
        Intent startHomeActivity = new Intent(this, HomePage.class);
        startHomeActivity.putExtra("userName", userName);
        startHomeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(startHomeActivity);

    }

    @Override
    public void firebaseFromLoginToCreateAccount() {
        FirebaseCreateAccountFragment fcaf = FirebaseCreateAccountFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame, fcaf);
        ft.addToBackStack(null);
        ft.commit();
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

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        /*TODO: destroy player*/
//        if(mPlayer != null) {
//            mPlayer.destroy();
//        }
//    }


    // SPOTIFY STUFF
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error e) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}