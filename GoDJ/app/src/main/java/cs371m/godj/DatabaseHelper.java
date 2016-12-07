package cs371m.godj;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jasmine on 11/27/2016.
 */

public class DatabaseHelper {

    protected DatabaseReference userDB;

    static DatabaseHelper newInstance() {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        return  databaseHelper;
    }


    public String updateCurrentUserName(String _userName) {
        if( _userName != null ) {
            _userName = _userName.replaceAll("\\.", "@");
            if (userDB == null) {
                userDB = FirebaseDatabase.getInstance().getReference().child("users").child(_userName).push();
            }
        } else {
            userDB = null;
        }
        return _userName;
    }
}
