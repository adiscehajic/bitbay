package controllers;


import helpers.CurrentAdmin;
import helpers.CurrentBuyerSeller;
import helpers.MailHelper;
import helpers.SessionHelper;
import models.*;
import helpers.*;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.purchase.showUserPurchases;
import views.html.user.userEdit;
import views.html.user.userProducts;
import views.html.user.userProfile;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import models.Country;


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
    @Security.Authenticated(CurrentAdmin.class)
    public Result deleteUser(Integer id) {
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

    public Result deleteUserAccount() {
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
    public Result getUser(String email) {
        User user = User.getUserByEmail(email);
        return ok(userProfile.render(user));
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
    public Result updateUser() {
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
            user.phoneNumber = boundForm.data().get("phone").trim();
            // Updating time when profile information has been changed.
            user.updated = new Date();
            // Updating all altered information into the database.
            user.update();
        } catch (Exception e) {
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
     *
     * @return ok if there are no errors or a JSON object representing the errors
     */
    public Result validateFormUser() {
        Form<User> binded = userRegistration.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

    /**
     * This action method is activated after user click on a link in verification email. It is using token from email
     * to verify user and activate validateUser(user) method which is saving validation data to database.
     *
     * @param token - User token.
     * @return - If the user has validated account it renders index page, otherwise renders sign in page.
     */
    public Result emailValidation(String token) {
        // Clearing the session.
        session().clear();
        try {
            // Checking if the user has already validated account.
            if (token == null) {
                return redirect(routes.ApplicationController.index());
            }
            // Finding the user with inputed token.
            User user = User.findUserByToken(token);
            // Validating the user account and rendering sign in page.
            if (User.validateUser(user)) {
                return redirect(routes.ApplicationController.signIn());
            } else {
                return redirect(routes.ApplicationController.index());
            }
        } catch (Exception e) {
            return redirect(routes.ApplicationController.signIn());
        }
    }

    /**
     * This method is activated after user click on a link in verification email. It is using token from email to
     * verify user and redirect user to page where user can enter new password for his account.
     *
     * @param token - Token in the forgot password URL.
     * @return
     */
    public Result validateForgottenPassword(String token) {
        try {
            // Finding the user with inputed token.
            User user = User.findUserByToken(token);
            // Validating the user account and rendering sign in page.
            if (User.validateUser(user)) {
                user.token = null;
                return redirect(routes.ApplicationController.newPassword(user.email));
            } else {
                return redirect(routes.ApplicationController.forgotPassword());
            }

        } catch (Exception e) {
            return redirect(routes.ApplicationController.forgotPassword());
        }
    }

    /**
     * This method is checking if email user typed in email field is in database and if user is registrated before. If
     * user is registrated in database new unique token is generated and verification mail is sent to users email.
     *
     * @return Page where user can input email on which he wants to send new password.
     */
    public Result getRegistratedUserByEmail() {
        // Connecting with form.
        DynamicForm form = Form.form().bindFromRequest();
        // Declaring variable that contains inputed email.
        String email = form.data().get("email");
        // Finding user with inputed email.
        User user = User.getUserByEmail(email);
        // Checking if the user exists in database and sending email.
        if (user != null) {
            user.token = UUID.randomUUID().toString();
            user.update();
            MailHelper.sendNewPassword(user.email, ConstantsHelper.BIT_BAY + "/signin/forgotpassword/" + user.token);
            flash("success", "Email successfully sent");
        } else {
            flash("error", "Could not find user with that email ");
        }
        return redirect(routes.ApplicationController.forgotPassword());
    }

    /**
     * This method is used to change old users password with new one, in case when new password and confirm password
     * are equal and redirect user to sign in page.
     *
     * @return Page where user can input new password.
     */
    public Result setNewPassword(String email) {
        // Connecting with form.
        DynamicForm form = Form.form().bindFromRequest();
        // Declaring variables that contain inputed password and confirm password.
        String password = form.get("password");
        String confirmPassword = form.get("confirmPassword");
        // Checking are the all validations correct.
        if(password == ""){
            flash("emptyError", "Password is required");
            return redirect(routes.ApplicationController.newPassword(email));
        }
        if(password.length()<8){
            flash("lengthError", "Password must be min 8 characters long");
            return redirect(routes.ApplicationController.newPassword(email));
        }

        if (password.equals(confirmPassword)) {
            User user = User.getUserByEmail(email);
            user.password = BCrypt.hashpw(form.get("confirmPassword"), BCrypt.gensalt());
            user.update();
            return redirect(routes.ApplicationController.signIn());
        }
        flash("matchError", "Password don't match");
        return redirect(routes.ApplicationController.newPassword(email));
    }

    /**
     * Uploads selected user profile image to cloudinery and saves path of the image into database.
     *
     * @return
     */
    @RequireCSRFCheck
    public Result saveUserPicture() {
        // Finding current user.
        User user = SessionHelper.currentUser();
        // Getting selected image.
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("image");
        // Uploading selected image on cloudinery and saving image path into database. If user has already uploaded
        // profile image it finds the old image and deletes it, and then uploads and saves new image.
        if (user.image != null) {
            user.image.deleteImage();
            user.image.delete();
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
        return redirect(routes.Users.getUser(user.email));
    }

    /**
     * Renders page where all user purchases are listed. If the time for the refund of the purchese has not passed,
     * user can refund the purchase.
     *
     * @return Page where all user purchases are listed.
     */
    public Result getUserPurchases(){
        // Findind current user.
        User currentUser = SessionHelper.currentUser();
        // Finding all purchases of the user.
        List<PurchaseItem> items = PurchaseItem.getPurchasedItemsByUser(currentUser);
        // Rendering page.
        return ok(showUserPurchases.render(currentUser, items));
    }
}