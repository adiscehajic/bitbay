package controllers;

import helpers.SessionHelper;
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
    private static final Form<Registration> userForm = Form.form(Registration.class);
    private static final Integer ADMIN = 1;
    /**
     * Renders index.html page on which are listed all products from database. User can select product and depending on
     * his type he can buy and comment product and also like and dislike product comments.
     *
     * @return Index page of the application.
     */
    public Result index() {
        // Declaring list that contains all products from database.
        List<Product> products = Product.findAll();
        return ok(index.render(products));
    }

    /**
     * Renders signup.html page on which new users can registrate or switch to signIn.html page. New users must input
     * all required fields that are on signup.html page. If new user does not input all required fields prints warning
     * message.
     *
     * @return Sign up page of the application.
     */
    public Result signUp() {
        // Declaring registration form.
        Form<Registration> boundForm = userForm.bindFromRequest();
        // Checking if there is user that is logged in.
        if (!SessionHelper.isAuthenticated()) {
            return ok(signup.render(boundForm));
        } else {
            return redirect(routes.ApplicationController.index());
        }
    }

    /**
     * Renders signIn.html page on which user can sign in into application. To sign in user needs to input correct
     * email and password. If inputed email and password are wrond warning message occurs.
     *
     * @return Sign in page of the application.
     */
    public Result signIn() {
        // Checking if there is user that is logged in.
        if (!SessionHelper.isAuthenticated()) {
            return ok(signIn.render(userForm));
        } else {
            return redirect(routes.ApplicationController.index());
        }
    }

    /**
     * Reads values that are inputed on signIn.html page and validates them. On sign in user needs to input correct
     * email and password. If the email or password are incorrect, or if the email or password are not inputed, warning
     * message occurs.
     *
     * @return Opens index.html page if the sign in is successful and redirects signIn.html page if the sign in has
     * errors
     */
    public Result validateSignIn() {
        // Connecting with sign in form.
        Form<Registration> boundForm = userForm.bindFromRequest();
        // Checking are the inputed values correct and if the form has errors.
        if (boundForm.hasErrors()) {
            // If form has errors printing warning message.
            flash("signInError", "Please input email and password.");
            return badRequest(signIn.render(boundForm));
        }
        // Reading inputed values and creating registration user.
        Registration signedUser = boundForm.get();

        try {
            // Declaring string variables.
            String email = signedUser.email;
            String password = signedUser.password;
            // Calling method authenticate and creating new user.
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
                // Checking if the user that wants to sign in is an administrator.
                if (user.userType.id == ADMIN) {
                    return redirect(routes.ApplicationController.signIn());
                }
                // Clearing all sessions and creating new session that stores user email
                session().clear();
                session("email", user.email);
                // Redirecting to main page.
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
     * Reads values that are inputed on signup.html page and, if they are correct, creates new user and inputs him into
     * the database. User must input all required fields and only than registration is successful. If any of fields is
     * not filled, or if field contains wrong value warning message occurs.
     *
     * @return Opens index.html page if the sign up is successful and redirects signup.html page if the sign up has
     * errors
     */
    public Result newUser() {
        // Connecting with sign up form.
        Form<Registration> boundForm = userForm.bindFromRequest();
        // Checking are the inputed values correct and if the form has errors.
        if (boundForm.hasErrors()) {
            // If form has errors printing warning message.
            flash("signUpError", "Please fill required fields.");
            return badRequest(signup.render(boundForm));
        }
        // Reading inputed values and creating registration user.
        Registration newUser = boundForm.get();

        try {
            // Creating user from database with inputed email.
            User existingUser = User.getUserByEmail(newUser.email);
            // Checking if the user with inputed email exists.
            if (existingUser != null) {
                // Printing warning message if the user already exists.
                flash("errorUserExists", "User already exists.");
                return badRequest(signup.render(boundForm));
            }
            // Declaring variable that represents confirm password.
            String confirmPassword = boundForm.data().get("confirmPassword");
            // Checking are the inputed password and confirm password equal and if they have more than 8 characters.
            if (!newUser.password.equals(confirmPassword) || newUser.password.length() < 8) {
                // Printing warning message if the passwords does not match and are smaller than 8 characters.
                flash("errorPassword", "Password must have min. 8 characters and must match.");
                return badRequest(signup.render(boundForm));
            } else {
                // Creating new user.
                User user = new User();
                // Reading inputed values and storing them into string variables.
                String firstName = boundForm.data().get("first-name");
                String lastName = boundForm.data().get("last-name");
                String type = boundForm.data().get("type");
                // Checking if the inputed first name and last name contain numbers.
                if (firstName.equals("") || lastName.equals("")) {
                    flash("signUpError", "Please fill required fields.");
                    return badRequest(signup.render(boundForm));
                } else if (!firstName.matches("^[a-z A-Z]*$") || !lastName.matches("^[a-z A-Z]+$")) {
                    flash("nameError", "First name and last name can contain only letters.");
                    return badRequest(signup.render(boundForm));
                }
                // Declaring user type.
                UserType userType = UserType.getUserTypeById(Integer.parseInt(type));
                // Adding values to the new user.
                user.firstName = firstName;
                user.lastName = lastName;
                user.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
                user.email = newUser.email;
                user.userType = userType;
                // Saving new user into database.
                user.save();
                // Clearing all sessions and creating new session that stores user email
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
     * Clears current sessions and prints message.
     */
    public Result logout() {
        session().clear();
        flash("successLogout", "You have successfully logged out!");
        return redirect(routes.ApplicationController.signIn());
    }

    /**
     * Created by Adis Cehajic on 9/21/2015.
     *
     * Represents inner class that has two variables that are required.
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
