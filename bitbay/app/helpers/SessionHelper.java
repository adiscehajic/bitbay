package helpers;

import models.User;
import play.mvc.Http;

/**
 * Created by Adis Cehajic on 9/11/2015.
 */
public class SessionHelper {

    public static User currentUser(Http.Context ctx){
        String email = ctx.session().get("email");
        if(email == null) {
            return null;
        }
        return User.getUserByEmail(email);
    }
}
