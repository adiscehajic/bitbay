package helpers;

import models.User;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;

/**
 * Created by Denis Cehajic on 9/18/2015.
 */
public class CurrentBuyerSeller extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        if(!ctx.session().containsKey("email")) {
            return null;
        }
        String email = ctx.session().get("email");
        User user = User.getUserByEmail(email);
        if (user != null && (user.userType.id == 2 || user.userType.id == 3)) {
            return user.email;
        }
        return null;
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect(routes.Users.signIn());
    }
}
