package helpers;

import controllers.routes;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by adis.cehajic on 09/10/15.
 */
public class CurrentUser extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        if(!ctx.session().containsKey("email")) {
            return null;
        }
        String email = ctx.session().get("email");
        User user = User.getUserByEmail(email);
        if (user != null) {
            return user.email;
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.ApplicationController.signIn());
    }
}
