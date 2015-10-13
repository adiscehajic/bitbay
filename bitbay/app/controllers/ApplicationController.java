package controllers;

import com.avaje.ebean.Model.Finder;
import helpers.MailHelper;
import helpers.SessionHelper;
import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.contactUs;
import views.html.index;
import views.html.signIn;
import views.html.signup;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adis Cehajic on 9/21/2015.
 */
public class ApplicationController extends Controller {

    // Declaring variable.
    private static final Form<UserLogin> loginForm = Form.form(UserLogin.class);
    private static final Form<UserRegistration> registrationForm = Form.form(UserRegistration.class);
    private static String url = Play.application().configuration().getString("url");




    /**
     * Renders index.html page on which are listed all products from database. User can select product and depending on
     * his type he can buy and comment product and also like and dislike product comments.
     *
     * @return Index page of the application.
     */
    public Result index() {

        List<Product> recommendations = Recommendation.getRecommendations();

        // Declaring list that contains all products from database.
        List<Product> products = Product.findAll();
        return ok(index.render(products, recommendations));
    }

    /**
     * Renders signup.html page on which new users can registrate or switch to signIn.html page. New users must input
     * all required fields that are on signup.html page. If new user does not input all required fields prints warning
     * message.
     *
     * @return Sign up page of the application.
     */
    public Result signUp() {
        // Creating the list of user types.
        List<UserType> userTypes = UserType.getAllUserTypes();
        // Checking if there is user that is logged in.
        if (!SessionHelper.isAuthenticated()) {
            return ok(signup.render(registrationForm, userTypes));
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
            return ok(signIn.render(loginForm));
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
    @RequireCSRFCheck
    public Result validateSignIn() {
        // Connecting with sign in form.
        Form<UserLogin> boundForm = loginForm.bindFromRequest();
        // Checking are the inputed values correct and if the form has errors.
        if (boundForm.hasErrors()) {
            // If form has errors printing warning message.
            //flash("signInError", "Please input email and password.");
            return badRequest(signIn.render(boundForm));
        } else {
            // Clearing all sessions and creating new session that stores user email
            session().clear();
            session("email", boundForm.bindFromRequest().field("email").value());
            // Redirecting to main page.
            return redirect(routes.ApplicationController.index());
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
    @RequireCSRFCheck
    public Result newUser() {
        // Connecting with sign up form.
        Form<UserRegistration> boundForm = registrationForm.bindFromRequest();
        // Creating the list of user types.
        List<UserType> userTypes = UserType.getAllUserTypes();
        // Checking are the inputed values correct and if the form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(signup.render(boundForm, userTypes));
        } else {
            // Reading inputed values and creating registration user.
            UserRegistration newUser = boundForm.get();
            // Creating new user.
            User user = new User();
            // Reading inputed values and storing them into string variables.
            String type = boundForm.bindFromRequest().field("userType").value();
            UserType userType = UserType.getUserTypeByName(type);
            // Adding values to the new user.
            user.firstName = newUser.firstName;
            user.lastName = newUser.lastName;
            user.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
            user.email = newUser.email;
            user.userType = userType;
            //Setting new token
            user.token = UUID.randomUUID().toString();
            //setting validated to false
            user.setValidated(false);
            // Saving new user into database.
            user.save();
            MailHelper.send(user.email, Play.application().configuration().getString("BIT_BAY") + "/signup/validate/" + user.token);

            // Clearing all sessions and creating new session that stores user email
            session().clear();
           // session("email", user.email);
            // Redirecting to the main page.
            return redirect(routes.ApplicationController.signIn());
        }
    }

    /**
     * Clears current sessions and prints message.
     *
     * @return Page where user that is administrator can sign in.
     */
    public Result logout() {
        session().clear();
        return redirect(routes.ApplicationController.signIn());
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormLogin() {
        Form<UserLogin> binded = loginForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormRegistration() {
        Form<UserRegistration> binded = registrationForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

    /**
     * Created by Adis Cehajic on 9/21/2015.
     * <p>
     * Represents inner class that has two variables that are required.
     */
    public static class UserLogin {
        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please input email.")
        @Constraints.Email(message = "Valid email is required.")
        public String email;
        @Constraints.MaxLength(255)
        @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
        @Constraints.Required(message = "Please input password.")
        public String password;

        /**
         * Validates the admin login form and returns all errors that occur during user login.
         * @return Errors that have occur during user login.
         */
        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<>();
            // Calling method authenticate and creating new user.
            User user = User.authenticate(email, password);
            // Checking if the user exists. If the inputed email and password are correct
            // redirecting to the main page, othewise opens sign in page.
            if (user == null || user.userType.id == UserType.ADMIN) {
                errors.add(new ValidationError("password", "Wrong email or password."));
            }
            if(!user.validated){
                errors.add(new ValidationError("password", "Please verify your email"));
            }
            return errors.isEmpty() ? null : errors;
        }
    }


    /**
     * Created by Adis Cehajic on 9/29/2015.
     * <p>
     * Represents inner class that has five variables that are required.
     */
    public static class UserRegistration {
        // Declaring properties.
        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert first name.")
        @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "First name can't contain digits.")
        public String firstName;

        @Constraints.MaxLength(255)
        @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "Last name can't contain digits.")
        @Constraints.Required(message = "Please insert last name.")
        public String lastName;

        @Column(unique = true)
        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert email.")
        @Constraints.Email(message = "Valid email is required.")
        public String email;

        @Constraints.MaxLength(255)
        @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
        @Constraints.Required(message = "Please insert password.")
        public String password;

        @Constraints.MaxLength(255)
        @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
        @Constraints.Required(message = "Please insert confirm password.")
        public String confirmPassword;

        @Constraints.Required(message = "Please select account type.")
        public String userType;

        // Declaring finder.
        private static Finder<String, User> finder =
                new Finder<>(User.class);

        /**
         * Validates the admin login form and returns all errors that occur during user login.
         * @return Errors that have occur during user login.
         */
        public List<ValidationError> validate() {
            // Declaring the list of errors.
            List<ValidationError> errors = new ArrayList<>();
            // Checking if there is another user in database with same email.
            if (finder.where().eq("email", this.email).findRowCount() > 0) {
                errors.add(new ValidationError("email", "User already exists."));
            }
            // Checking are the inputed password and confirm password equal.
            if (!this.confirmPassword.equals(this.password)) {
                errors.add(new ValidationError("confirmPassword", "Confirm password must match with password."));
            }
            return errors.isEmpty() ? null : errors;
        }

    }
}
