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

public class MainActivity extends AppCompatActivity implements FirebaseCreateAccountFragment.FirebaseCreateAccountInterface,
        FirebaseLoginFragment.FirebaseLoginInterface{

    protected static String userName;
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    public static String TAG = "GoDJ";

    private Handler signInHandler;

    private DatabaseHelper dbHelper;


    protected void firebaseInit() {
        System.out.println("firebase init called");
        mAuth = FirebaseAuth.getInstance();
        System.out.println("firebaseinit mauth: " + mAuth);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
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
        };
    }


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
        System.out.println("user in loginfinish:: " + userName);
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
}