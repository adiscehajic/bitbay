package controllers.rest;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.ApplicationController.UserRegistration;
import controllers.ApplicationController.UserLogin;


import controllers.Users;
import controllers.routes;
import helpers.ConstantsHelper;
import helpers.MailHelper;
import models.Product;
import models.Recommendation;
import models.User;
import models.UserType;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signIn;
import views.html.signup;

import java.util.List;
import java.util.UUID;

/**
 * Created by Kerim on 26.10.2015.
 */
public class ApiUserController extends Controller {

    private static final Form<UserLogin> loginForm = Form.form(UserLogin.class);
    private static final Form<UserApiRegistration> registrationForm = Form.form(UserApiRegistration.class);

    public Result validateSignIn() {
        // Connecting with sign in form.
        Form<UserLogin> boundForm = loginForm.bindFromRequest();
        // Checking are the inputed values correct and if the form has errors.
        if (!boundForm.hasErrors()) {
            // Clearing all sessions and creating new session that stores user email
            session().clear();
            session("email", boundForm.bindFromRequest().field("email").value());

            // kerim ispravio
            List<Product> recommendations = Recommendation.getRecommendations();
            // Declaring list that contains all products from database.
            List<Product> products = Product.findAll();
            // Redirecting to main page.
            return ok(index.render(products, recommendations));

        } else {
            // If form has errors printing warning message.
            //flash("signInError", "Please input email and password.");
            return badRequest(signIn.render(boundForm));
        }
    }


    public Result newUser() {
        // Connecting with sign up form.
        Form<UserApiRegistration> boundForm = registrationForm.bindFromRequest();
        // Creating the list of user types.
        List<UserType> userTypes = UserType.getAllUserTypes();
        if(!boundForm.hasErrors()) {
            // Reading inputed values and creating registration user.
            UserApiRegistration newUser = boundForm.get();
            // Creating new user.
            User user = new User();
            // Adding values to the new user.
            user.firstName = newUser.firstName;
            user.lastName = newUser.lastName;
            user.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
            user.email = newUser.email;
            user.userType = new UserType();
            user.userType.id = ConstantsHelper.BUYER;
            //Setting new token
            user.token = UUID.randomUUID().toString();
            //setting validated to false
            user.setValidated(false);
            // Saving new user into database.
            user.save();
            MailHelper.send(user.email, ConstantsHelper.BIT_BAY + "/signup/validateForgotenPassword/" + user.token);
            return ok(signIn.render(loginForm));
        }
        return badRequest();
    }

    public static class UserApiRegistration {
        // Declaring properties.
        public String firstName;

        public String lastName;

        public String email;

        public String password;

        public String confirmPassword;

        public UserApiRegistration(){}

    }
}
