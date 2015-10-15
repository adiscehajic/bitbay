package controllers;


import com.cloudinary.Cloudinary;
import helpers.CurrentAdmin;
import helpers.CurrentBuyerSeller;
import helpers.CurrentSeller;
import helpers.SessionHelper;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import views.html.user.userEdit;
import views.html.user.userProfile;
import views.html.user.userMessages;

import java.io.File;
import java.lang.*;
import java.util.ArrayList;
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
    public static final String BIT_BAY = Play.application().configuration().getString("BIT_BAY");

    /**
     * Deletes selected user. Only administrator user can delete other users, except users that are also administrators.
     * When selected user is deleted it automatically deletes all products that that user has, if the user is seller,
     * or deletes cart and cart items, if the deleted user is buyer.
     *
     * @param id - Id of the user that administrator user wants to delete.
     * @return Administrator panel page where all users are listed.
     */
    @Security.Authenticated(CurrentAdmin.class)
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

       /* List<Comment> comments = Comment.getCommentsByUser(user);

        for (int i = 0; i < comments.size(); i++) {
            comments.get(i).delete();
        }*/

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
    public Result getUser(String email){
        User user = User.getUserByEmail(email);
        return ok(userProfile.render(user));
    }

//    public Result getSelectedUserByEmail(String email){
//        User user = User.getUserByEmail(email);
//        return ok(userProfile.render(user));
//    }

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
        //Checking if user has selected country
        if (user.country != null) {
            countries.remove(user.country);
        }
        // Declaring filled user form.
        Form<User> filledForm = userRegistration.fill(user);
        // Checking if the user exists.
        if (user != null) {
            return ok(userEdit.render(filledForm, user, countries));
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
    @RequireCSRFCheck
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result updateUser(){
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Declaring list of all countries in the world.
        List<Country> countries = Country.findAllCountries();
        // Declaring user form.
        Form<User> boundForm = userRegistration.bindFromRequest();
        // Checking does user form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(userEdit.render(boundForm, user, countries));
        }
        //Checking if any user information was changed.
        try {
            // Checking if the new password is inputed.
            String password = boundForm.data().get("password");
            if (!password.isEmpty()) {
                user.password = BCrypt.hashpw(boundForm.data().get("password"), BCrypt.gensalt());
            }
            // Updating user profile.
            user.firstName = boundForm.data().get("firstName");
            user.lastName = boundForm.data().get("lastName");
            user.country = Country.findCountryByName(boundForm.data().get("country-state"));
            user.city = boundForm.data().get("city");
            user.address = boundForm.data().get("address");
            // Updating time when profile information has been changed.
            user.updated= new Date();
            // Updating all altered information into the database.
            user.update();
        }catch (Exception e){
            Logger.info("ERROR: UserLogin failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(userEdit.render(boundForm, user, countries));
        }
        // Redirecting to the profile page of the current user.
        return redirect(routes.Users.getUser(user.email));
    }

    /**
     * Renders page where user that is seller can view all products that he is selling. From this page user can access
     * to all products and update and delete products.
     *
     * @return Page where all products of the user are listed.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result getAllUserProducts(String email) {
        // Getting current from the session.
        User user = User.getUserByEmail(email);
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

    /**
     * This action method is activated after user click on a link in verification email.
     * It is using token from email to verify user and activate validateUser(user) method
     * which is saving validation data to DB
     * @param token - User token
     * @return - Result
     */
    public Result emailValidation(String token) {
        try {
            if (token == null) {
                return redirect(routes.ApplicationController.index());
            }

            User user = User.findUserByToken(token);
            if (User.validateUser(user)) {
                session("email", user.email);
                return redirect(routes.ApplicationController.index());
            } else {
                return redirect(routes.ApplicationController.signIn());
            }
        } catch (Exception e){
            return redirect(routes.ApplicationController.signIn());
        }
    }

    @RequireCSRFCheck
    public Result saveUserPicture() {
        User user = SessionHelper.currentUser();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("image");
        // Uploading selected images on cloudinery and saving image path into database.

        if (user.image != null) {
            user.image.deleteImage();
            if (filePart != null) {
                File file = filePart.getFile();
                Image image = Image.createUserImage(file);
                image.save();
            }
        } else {
            if (filePart != null) {
                File file = filePart.getFile();
                Image image = Image.createUserImage(file);
                image.save();
            }
        }
        return redirect(routes.Users.getUser());
    }
}
