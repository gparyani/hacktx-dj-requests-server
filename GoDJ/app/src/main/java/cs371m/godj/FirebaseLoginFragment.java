package cs371m.godj;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;




public class FirebaseLoginFragment extends Fragment {
    public interface FirebaseLoginInterface {
        void firebaseLoginFinish();
        void firebaseFromLoginToCreateAccount();
    }

    // We can have this member variable because it is initialized in onAttach
    // If the system kills us when memory is low, it will reattach and reinitialize this variable
    private FirebaseLoginInterface firebaseLoginInterface;
    private FirebaseAuth mAuth;
    public static String TAG = "Flogin";
    protected View myRootView;
    protected Button loginBut;
    protected Button anonLoginBut;
    protected Button createFragBut;

    // If create is false, log in screen and log in action, otherwise create account screen and action
    static FirebaseLoginFragment newInstance() {
        FirebaseLoginFragment firebaseLoginFragment = new FirebaseLoginFragment();
        return firebaseLoginFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            firebaseLoginInterface = (FirebaseLoginInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FirebaseEmailPasswordInterface ");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.login_create_account, container, false);
        // Why doesn't getView return the root view?
        // Alternative is the less savory: getActivity().findViewById(R.id.yourId)
        myRootView = v;
        loginBut = (Button) v.findViewById(R.id.loginButton);
        createFragBut = (Button) v.findViewById(R.id.createFragButton);
        if (loginBut == null || createFragBut == null) {
            Log.d(TAG, "Null buttons!");
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up the login form.
        View v = myRootView;
        // These are final because they are accessed from inner classes
        final EditText usernameView = (EditText) v.findViewById(R.id.username);
        final EditText passwordView = (EditText) v.findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        usernameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_TAB) {
                    passwordView.setFocusableInTouchMode(true);
                    passwordView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        // Sign up button click handler
        createFragBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                firebaseLoginInterface.firebaseFromLoginToCreateAccount();
            }
        });

        // Set up the submit button click handler
        loginBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = usernameView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();

                // Validate the log in data
                String validateString =
                        EmailPasswordValidate.validate(getResources(), username, password, null);
                // If there is a validation error, display the error
                if (validateString.length() > 0) {
                    Snackbar.make(myRootView, validateString, Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(getActivity());
                dlg.setTitle("Please wait.");
                dlg.setMessage("Logging in.  Please wait.");
                dlg.show();
                // Sign in
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dlg.dismiss();
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail " + task.getException().getMessage());
                                    Snackbar.make(myRootView,
                                            "Authentication failed: " + task.getException().getMessage(),
                                            Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    firebaseLoginInterface.firebaseLoginFinish();
                                }
                            }
                        });
            }
        });

    }
}
