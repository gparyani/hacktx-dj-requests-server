package cs371m.godj;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jasmine on 11/27/2016.
 */

public class DatabaseHelper {

    protected DatabaseReference userDB;

    // NB: Must be called on every authentication change
    // Input: Display name  Output: sanitized name for root of Firebase data
    public void updateCurrentUserName(String _userName) {
        if( _userName != null ) {
            // . is illegal in Firebase key, and more than one @ is illegal in email address
            _userName = _userName.replaceAll("\\.", "@"); // . illegal in Firebase key
            if (userDB == null) {
                userDB = FirebaseDatabase.getInstance().getReference().child("users").child(_userName).push();
                System.out.println("userdb: " + userDB);

            }
        } else {
            userDB = null;
        }
    }
}
