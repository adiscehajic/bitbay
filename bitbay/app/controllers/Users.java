package controllers;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signup;
import views.html.singIn;
import java.lang.*;

import com.avaje.ebean.Ebean;
import models.*;

/**
 * Created by adis.cehajic on 02/09/15.
 */
public class Users extends Controller {

    // Declaring variable.
    private static final Form<User> userRegistration = Form.form(User.class);

    public Result index() {
        return ok(index.render());
    }

    /**
     * Opens page with sign up content.
     * @return
     */
    public Result signup() {
        return ok(signup.render(userRegistration));
    }

    /**
     * Opens page with sign in content.
     * @return
     */
    public Result signIn() {
        return ok(singIn.render(userRegistration));
    }

    /**
     * Reads inputed values that are inputed on sign up page and inputing them into the database.
     * @return
     */
    public Result newUser() {
        // Connecting with sign up form.
        Form<User> boundForm = userRegistration.bindFromRequest();
        // Reading inputed values and storing them into string variables.
        String firstName = boundForm.bindFromRequest().field("first-name").value();
        String lastName = boundForm.bindFromRequest().field("last-name").value();
        String email = boundForm.bindFromRequest().field("email").value();
        String password = boundForm.bindFromRequest().field("password").value();
        String confirmPassword = boundForm.bindFromRequest().field("confirmPassword").value();

        // Checking are the inputed password and confirm password equal, and if they are creating
        // new user and storing him into database.
        if (password.equals(confirmPassword)) {
            // Creating new user.
            User user = new User(null, firstName, lastName, email, password);
            // Saving new user into database.
            Ebean.save(user);
            // Redirecting to the main page.
            return redirect(routes.Users.index());
        } else {
            flash("error", "Please input form below.");
            return badRequest(signup.render(userRegistration));
        }

    }

    /**
     * Reads inputed values that are inputed on sign in page and validates them.
     * @return
     */
    public Result validate() {
        // Connecting with sign in form.
        Form<User> boundForm = userRegistration.bindFromRequest();
        // Reading inputed values and storing them into string variables.
        String email = boundForm.bindFromRequest().field("email").value();
        String password = boundForm.bindFromRequest().field("password").value();
        // Calling method authenticate and creating new user.
        User user = User.authenticate(email,password);
        // Checking if the user exists. If the inputed email and password are correct
        // redirecting to the main page, othewise opens sign in page.
        if (user != null) {
            return redirect(routes.Users.index());
        } else {
            return ok(singIn.render(userRegistration));
        }
    }
}
