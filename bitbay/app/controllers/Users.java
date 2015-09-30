package controllers;


import com.cloudinary.Cloudinary;
import helpers.CurrentBuyerSeller;
import helpers.CurrentSeller;
import helpers.SessionHelper;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import views.html.user.userEdit;
import views.html.user.userProfile;
import views.html.user.userMessages;
import java.lang.*;
import java.util.List;
import java.util.Date;
import com.avaje.ebean.Ebean;
import models.*;
import views.html.user.userProducts;
import views.html.user.userCart;
import models.Country;
import javax.persistence.PersistenceException;


/**
 * Created by Adis Cehajic on 02/09/15.
 */
public class Users extends Controller {

    // Declaring user form.
    private static final Form<User> userRegistration = Form.form(User.class);

    /**
     * Deletes selected user. Only administrator user can delete other users, except users that are also administrators.
     * When selected user is deleted it automatically deletes all products that that user has, if the user is seller,
     * or deletes cart and cart items, if the deleted user is buyer.
     *
     * @param id - Id of the user that administrator user wants to delete.
     * @return Administrator panel page where all users are listed.
     */
    public Result deleteUser(Integer id){
        // Finding user with inputed id.
        User user = User.findById(id);
        // Deleting selected user.
        user.delete();
        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminUsers());
    }

    /**
     * Enables current user that is signed in to delete his account. After deleting user can not undo action and his
     * account is deleted. Also after deleting session is cleared.
     *
     * @return Main page of the application.
     */
    public Result deleteUserAccount(){
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Deleting current user.
        user.delete();
        // Clearing session.
        session().clear();
        // Redirecting to the main page of the application.
        return redirect(routes.ApplicationController.index());
    }

    /**
     * Renders profile page of the current user that is signed in. On profile page user can view all information that
     * he has inputed in the application. Also he can select to edit or delete his account. To this page can access
     * only users whose account it is.
     *
     * @return - Profile page of current user that is signed in.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result getUser(){
        return ok(userProfile.render(SessionHelper.currentUser()));
    }

    /**
     * Renders page where user can edit his personal informations. He can alter inputed information that he has inputed
     * during registration, except email, and input other personal informations. If the inputed values are not in right
     * format warning message occurs. After editing user is redirected to his profile page where he can see all new
     * informations that he has altered.
     *
     * @return - Page where user can alter his personal informations.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result editUser() {
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Declaring list of all countries in the world.
        List<Country> countries = Country.findAllCountries();
        // Checking if the user exists.
        if (user != null) {
            return ok(userEdit.render(user, countries));
        } else {
            return redirect(routes.ApplicationController.signIn());
        }
    }

    /**
     * Reads informations that current user has inputed in the profile edit form and saves them into the database. All
     * inputed informations must be in the right format, otherwise warning message occurs. If the user does not alter
     * informations, they stay same as before editing. Only users whose account is can access and manipulate with his
     * personal informations.
     *
     * @return Opens current user profile page if the profile editing was successful and redirects to the profile edit
     * page if the informations have errors.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result updateUser(){
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Declaring list of all countries in the world.
        List<Country> countries = Country.findAllCountries();
        // Declaring form.
        DynamicForm form = Form.form().bindFromRequest();

        // Getting all informations that are inputed and storing them into string variables.
        String firstName = form.get("firstName");
        String lastName = form.get("lastName");
        String pass = form.get("password");
        String conPass = form.get("confirmPassword");
        String countryName = form.get("country-state");
        String city = form.get("city");
        String address = form.get("address");

        //Checking if any user information was changed and if the information has errors.
        try {
            // Checking first name and last name.
            if (!firstName.equals(user.firstName) || !lastName.equals(user.lastName)) {
                // First name can not be empty and can not contain numbers.
                if (!firstName.matches("^[a-z A-Z]*$") || !lastName.matches("^[a-z A-Z]*$")) {
                    flash("updateUserNameDiggitError","First name and last name can't contain diggits.");
                    return badRequest(userEdit.render(user, countries));
                } else if (firstName.isEmpty() || lastName.isEmpty()) {
                    flash("updateUserNameEmptyError","First name and last name can't be empty string.");
                    return badRequest(userEdit.render(user, countries));
                } else {
                    user.firstName = firstName;
                    user.lastName = lastName;
                }
            }
        }catch (Exception e){
            Logger.info("ERROR: UserLogin failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(userEdit.render(user, countries));
        }

        // Checking if the password match confirm password and if they are in the right format.
        if(pass.equals(conPass)) {
            if (!pass.isEmpty() || pass.length() > 8) {
                user.password = BCrypt.hashpw(pass, BCrypt.gensalt());
            }
        } else {
            flash("updatePasswordError","Password and confirm password must match.");
            return badRequest(userEdit.render(user, countries));
        }
        // Updating user country.
        user.country = Country.findCountryByName(countryName);
        // Checking if the city is altered and if it is, saving him.
        if(!city.equals(user.city)){
            user.city = city;
        }
        // Checking if the address is altered and if it is, saving him.
        if(!address.equals(user.address)){
            user.address = address;
        }
        // Updating time when profile information has been changed.
        user.updated= new Date();
        // Updating all altered information into the database.
        user.update();
        // Redirecting to the profile page of the current user.
        return redirect(routes.Users.getUser());
    }

    /**
     * Renders page where user that is seller can view all products that he is selling. From this page user can access
     * to all products and update and delete products.
     *
     * @return Page where all products of the user are listed.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result getAllUserProducts() {
        // Getting current from the session.
        User user = SessionHelper.currentUser();
        // Creating the list of the product from current user.
        List<Product> products = Product.findAllProductsByUser(user);
        if (user != null) {
            return ok(userProducts.render(products, user));
        } else {
            return redirect(routes.ApplicationController.signIn());
        }
    }

    /**
     * This will just validate the form for the AJAX call
     * @return ok if there are no errors or a JSON object representing the errors
     */
    public Result validateFormUser(){
        Form<User> binded = userRegistration.bindFromRequest();
        if(binded.hasErrors()){
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

}
