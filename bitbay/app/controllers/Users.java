package controllers;


import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import views.html.user.userEdit;
import views.html.user.userProfile;
import java.lang.*;
import java.text.Normalizer;
import java.util.List;
import views.html.admin.allUsers;
import java.util.Date;
import com.avaje.ebean.Ebean;
import models.*;
import models.Country;

import javax.persistence.PersistenceException;


/**
 * Created by adis.cehajic on 02/09/15.
 */
public class Users extends Controller {

    // Declaring variable.
    private static final Form<User> userRegistration = Form.form(User.class);

    private String name;

    public Result index() {
        Logger.info(session().get("email"));
        return ok(index.render(name));
    }

    /**
     * Opens page with sign up content.
     * @return
     */
    public Result signup() {
        if (session().get("email") == null) {
            return ok(signup.render(userRegistration));
        } else {
            return redirect(routes.Users.index());
        }
    }

    /**
     * Opens page with sign in content.
     * @return
     */
    public Result signIn() {
        return ok(signIn.render(userRegistration));
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
        String type = boundForm.bindFromRequest().field("type").value();
        UserType userType = UserType.getUserTypeById(Integer.parseInt(type));
        Logger.info(type);

        if (firstName.equals("")) {
            flash("errorFirstName", "Please input first name.");
            return ok(signup.render(userRegistration));
        } else if (lastName.equals("")) {
            flash("errorLastName", "Please input last name.");
            return ok(signup.render(userRegistration));
        }

        // Checking are the inputed password and confirm password equal, and if they are creating
        // new user and storing him into database.
        if (password.equals(confirmPassword) && password != "" && password.length() >= 8) {
            // Creating new user.
            User user = new User(null, firstName, lastName, email, password, userType);
            // Saving new user into database.
            try {
                Ebean.save(user);
            } catch (PersistenceException e) {
                flash("errorEmail", "Email already exists.");
                return badRequest(signup.render(boundForm));
            }

            session().clear();
            session("email", email);
            // Redirecting to the main page.
            return ok(index.render(user.firstName));
        } else {
            flash("error", "Password must have min. 8 characters and must match.");
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

      //  if (boundForm.hasErrors()) {
        //    flash("signInError", "Wrong input!");
         //   return badRequest(signIn.render(boundForm));
       // }

        // Calling method authenticate and creating new user.

        if (email.equals("") || password.equals("")) {
            flash("errorNoInput", "Please input email and password.");
            return ok(signIn.render(userRegistration));
        }
        User user = User.authenticate(email,password);
        // Checking if the user exists. If the inputed email and password are correct
        // redirecting to the main page, othewise opens sign in page.

        if (user == null) {
            flash("errorEmail", "Wrong email or password.");
            return ok(signIn.render(userRegistration));
        } else if (user.toString().equals(" ")) {
            flash("errorNoInput", "Please input email and password.");
            return ok(signIn.render(userRegistration));
        } else if (user != null) {
            session().clear();
            session("email", email);
            return ok(index.render(user.firstName));
        } else {
            return null;
        }

    }

    /**
     * Method that ends current session
     */
    public Result logout() {
        session().clear();
        flash("successLogout", "You have successfully logged out!");
        return redirect(routes.Users.signIn());
    }

    /**
     * This method delete selected user
     * @param id - ID of selected user
     */
    public Result deleteUser(Integer id){
        User user = User.findById(id);
        Ebean.delete(user);

        return redirect(routes.AdminController.adminUsers());

    }

    /**
     * This method find and return user by ID
     * @param id - ID of wanted user
     * @return - user with selected ID
     */
    public Result getUser(Integer id){
        String email = session().get("email");
        User user =  User.getUserByEmail(email);

        return ok(userProfile.render(user));

    }


    /**
     * Mehtod for changing user data
     * @param id - ID of selected user
     * @return - User with selected ID
     */
    public Result editUser(Integer id) {
       String email = session().get("email");
       User user = User.getUserByEmail(email);

        return ok(userEdit.render(user));

    }

    /**
     * Method for updating user information
     * @param id - ID of selected user
     */
    public Result updateUser(Integer id){

        String email = session().get("email");
        User user = User.getUserByEmail(email);

        Form<User> boundForm = userRegistration.bindFromRequest();

        //Collecting data from user edit page
        String name = boundForm.bindFromRequest().field("firstName").value();
        String lastName = boundForm.bindFromRequest().field("lastName").value();
        String pass = boundForm.bindFromRequest().field("password").value();
        String conPass = boundForm.bindFromRequest().field("confirmPassword").value();
       // TODO Country country = boundForm.bindFromRequest().field("country").value();
        String city = boundForm.bindFromRequest().field("city").value();
        String address = boundForm.bindFromRequest().field("address").value();

        //Checking if any user information was changed
        if(!name.equals(user.firstName)){
            user.firstName = name;
        }

        if(!lastName.equals(user.lastName)){
            user.lastName = lastName;
        }

        if(pass.equals(conPass)){
            user.password = BCrypt.hashpw(pass, BCrypt.gensalt());
        }

//      TODO  if(!country.equals(user.country)){
//            user.country = country;
//        }

        if(!city.equals(user.city)){
            user.city = city;
        }

        if(!address.equals(user.address)){
            user.address = address;
        }
        //Updating time when profile information has been changed
        user.updated= new Date();
        Ebean.update(user);


        return redirect(routes.Users.getUser(user.id));
    }

}
