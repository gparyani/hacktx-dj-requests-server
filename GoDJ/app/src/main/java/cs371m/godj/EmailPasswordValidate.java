package cs371m.godj;

import android.content.res.Resources;

/**
 * Created by Jasmine on 11/15/16.
 * Adapted from witchel
 */

public class EmailPasswordValidate {
    // Locally check login or account creation credentials
    // Empty string means all is ok
    static String validate(Resources resources, String username, String password, String passwordAgain) {
        boolean validationError = false;
        StringBuilder validationErrorMessage =
                new StringBuilder("Please");
        if (username.trim().length() == 0) {
            validationError = true;
            validationErrorMessage.append("enter a username");
        } else if (!username.contains("@")) {
            validationError = true;
            validationErrorMessage.append(" use a valid email address");
        }
        if (password.trim().length() == 0) {
            if (validationError) {
                validationErrorMessage.append(", and ");
            }
            validationError = true;
            validationErrorMessage.append("enter a password");
        } else if( password.trim().length() < 6 ) {
            if (validationError) {
                validationErrorMessage.append(", and ");
            }
            validationError = true;
            validationErrorMessage.append("This password is too short (less than 6 characters)");
        }
        if (passwordAgain != null
                && !password.equals(passwordAgain)) {
            if (validationError) {
                validationErrorMessage.append(", and ");
            }
            validationError = true;
            validationErrorMessage.append("enter the same password twice");
        }
        validationErrorMessage.append(".");

        String returnString = "";
        if (validationError) {
            returnString = validationErrorMessage.toString();
        }
        return returnString;
    }
}
