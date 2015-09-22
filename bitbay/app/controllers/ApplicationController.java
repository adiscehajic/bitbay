package controllers;

import models.Category;
import models.Product;
import models.User;
import models.UserType;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signIn;
import views.html.signup;
import java.util.List;

/**
 * Created by Adis Cehajic on 9/21/2015.
 */
public class ApplicationController extends Controller {

    // Declaring variable.
    private static final Form<User> userRegistration = Form.form(User.class);

    private static final Form<Registration> userForm = Form.form(Registration.class);

    String name;

    public Result index() {
        List<Category> categories = Category.findAll();
        List<Product> products = Product.findAll();

        return ok(index.render(name, products));
    }

    /**
     * Opens page with sign up content.
     * @return
     */
    public Result signup() {
        Form<Registration> boundForm = userForm.bindFromRequest();

        if (session().get("email") == null) {
            return badRequest(signup.render(boundForm));
        } else {
            return redirect(routes.ApplicationController.index());
        }
    }

    /**
     * Opens page with sign in content.
     * @return
     */
    public Result signIn() {
        return ok(signIn.render(userForm));
    }

    /**
     * Reads inputed values that are inputed on sign in page and validates them.
     * @return
     */
    public Result validateSignIn() {
        // Connecting with sign in form.
        Form<Registration> boundForm = userForm.bindFromRequest();

        if (boundForm.hasErrors()) {
            flash("signInError", "Please input email and password.");
            return badRequest(signIn.render(boundForm));
        }
        // Reading inputed values and storing them into string variables.
        Registration signedUser = boundForm.get();
        // Calling method authenticate and creating new user.
        try {

            String email = signedUser.email;
            String password = signedUser.password;

            User user = User.authenticate(email, password);


            // Checking if the user exists. If the inputed email and password are correct
            // redirecting to the main page, othewise opens sign in page.

            if (user == null) {
                flash("errorEmail", "Wrong email or password.");
                return badRequest(signIn.render(boundForm));
            } else if (user.toString().equals(" ")) {
                flash("errorNoInput", "Wrong email or password.");
                return badRequest(signIn.render(boundForm));
            } else if (user != null) {
                if (user.userType.id == 1) {
                    return redirect(routes.ApplicationController.signIn());
                }
                session().clear();
                session("email", user.email);
                return redirect(routes.ApplicationController.index());
            } else {
                return redirect(routes.ApplicationController.signIn());
            }
        } catch (Exception e) {
            Logger.info("ERROR: Login failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(signIn.render(boundForm));
        }
    }

    /**
     * Reads inputed values that are inputed on sign up page and inputing them into the database.
     * @return
     */
    public Result newUser() {
        // Connecting with sign up form.
        Form<Registration> boundForm = userForm.bindFromRequest();

        Registration newUser = boundForm.get();

        try {
            User existingUser = User.getUserByEmail(newUser.email);

            if (existingUser != null) {
                flash("errorUserExists", "User already exists.");
                return badRequest(signup.render(boundForm));
            }

            String confirmPassword = boundForm.data().get("confirmPassword");

            if (!newUser.password.equals(confirmPassword)) {
                flash("errorPassword", "Password must have min. 8 characters and must match.");
                return badRequest(signup.render(boundForm));
            } else {
                User user = new User();
                // Reading inputed values and storing them into string variables.
                String firstName = boundForm.data().get("first-name");
                String lastName = boundForm.data().get("last-name");
                String type = boundForm.data().get("type");
                UserType userType = UserType.getUserTypeById(Integer.parseInt(type));

                user.firstName = firstName;
                user.lastName = lastName;
                user.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
                user.email = newUser.email;
                user.userType = userType;
                Logger.info(user.toString());
                user.save();

                session().clear();
                session("email", user.email);
                // Redirecting to the main page.
                return redirect(routes.ApplicationController.index());
            }
        } catch (Exception e) {
            Logger.info("ERROR: Registration failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(signup.render(boundForm));
        }
    }

    /**
     * Method that ends current session
     */
    public Result logout() {
        session().clear();
        flash("successLogout", "You have successfully logged out!");
        return redirect(routes.ApplicationController.signIn());
    }

    /**
     *
     */
    public static class Registration {
        @Constraints.MaxLength(255)
        @Constraints.Required()
        @Constraints.Email
        public String email;
        @Constraints.MaxLength(255)
        @Constraints.MinLength(8)
        @Constraints.Required()
        public String password;
    }

}
