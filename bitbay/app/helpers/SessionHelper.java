package helpers;

import models.User;
import models.UserType;
import play.mvc.Http;

/**
 * Created by Adis Cehajic on 9/11/2015.
 */
public class SessionHelper {

    public static User currentUser(){
        Http.Context ctx = Http.Context.current();
        String email = ctx.session().get("email");
        if(email == null) {
            return null;
        }
        return User.getUserByEmail(email);
    }

    public static boolean isUserType(Integer userType) {
        User user = currentUser();
        if (user == null) {
            return false;
        }
        return user.userType.id.equals(userType);
    }

    public static boolean isAuthenticated() {
        return currentUser() != null;
    }
}
