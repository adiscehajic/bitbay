package helpers;

import controllers.routes;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Denis Cehajic on 9/11/2015.
 */
public class CurrentSeller extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        if(!ctx.session().containsKey("email")) {
            return null;
        }
        String email = ctx.session().get("email");
        User user = User.getUserByEmail(email);
        if (user != null && user.userType.id == 3) {
            return user.email;
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Users.signIn());
    }
}
