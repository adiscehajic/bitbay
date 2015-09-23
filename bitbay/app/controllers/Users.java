package controllers;


import com.cloudinary.Cloudinary;
import helpers.CurrentBuyerSeller;
import helpers.CurrentSeller;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import views.html.user.userEdit;
import views.html.user.userProfile;
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

    // Declaring variable.
    private static final Form<User> userRegistration = Form.form(User.class);

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
     * This method delete selected user
     * @param id - ID of selected user
     */
    public Result deleteUserAccount(Integer id){
        User user = User.findById(id);
        Ebean.delete(user);
        session().clear();

        return redirect(routes.ApplicationController.index());

    }

    /**
     * This method find and return user by ID
     * @param id - ID of wanted user
     * @return - user with selected ID
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
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
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result editUser(Integer id) {
       String email = session().get("email");
       User user = User.getUserByEmail(email);
        List<Country> countries = Country.findAllCountries();
        if (user != null) {
            return ok(userEdit.render(user, countries));
        } else {
            return redirect(routes.ApplicationController.signIn());
        }
    }

    /**
     * Method for updating user information
     * @param id - ID of selected user
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result updateUser(Integer id){

        String email = session().get("email");
        User user = User.getUserByEmail(email);

        Form<User> boundForm = userRegistration.bindFromRequest();


        //Collecting data from user edit page
        String firstName = boundForm.bindFromRequest().field("firstName").value();
        String lastName = boundForm.bindFromRequest().field("lastName").value();
        String pass = boundForm.bindFromRequest().field("password").value();
        String conPass = boundForm.bindFromRequest().field("confirmPassword").value();
        String countryName = boundForm.bindFromRequest().field("country").value();
        String city = boundForm.bindFromRequest().field("city").value();
        String address = boundForm.bindFromRequest().field("address").value();
        List<Country> countries = Country.findAllCountries();
        Logger.info(countryName);
        //Checking if any user information was changed
        try {
            if (!firstName.equals(user.firstName)) {
                    if (!firstName.matches("^[a-z A-Z]*$")) {
                        flash("updateUserNameDiggitError","Name can't contain diggits.");
                        throw new Exception();
                    }
                if (firstName.isEmpty()) {
                    flash("updateUserNameEmptyError","Name can't be empty string.");
                        throw new Exception();
                } else {
                    user.firstName = firstName;
                }
            }
        }catch (Exception e){
            return badRequest(userEdit.render(user, countries));
        }

        try {
            if (!lastName.equals(user.lastName)) {
                    if (!lastName.matches("^[a-z A-Z]*$")) {
                        flash("updateUserLastNameDiggitError","Last name can't contain diggits.");
                        throw new Exception();
                    }
                if (lastName.isEmpty()) {
                    flash("updateUserLastNameEmptyError","Last name can't be empty string.");
                    throw new Exception();
                } else {
                    user.lastName = lastName;
                }
            }
        }catch (Exception e){
            return badRequest(userEdit.render(user, countries));
        }

        if(pass.equals(conPass)) {
            if (pass.isEmpty() || pass.length() < 8) {
               
            }else{
                user.password = BCrypt.hashpw(pass, BCrypt.gensalt());
            }
        }


                user.country = Country.findCountryByName(countryName);




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

    @Security.Authenticated(CurrentSeller.class)
    public Result getAllUserProducts() {
        List<Product> products = Product.findAllProductsByUser(session().get("email"));
        User user = User.getUserByEmail(session().get("email"));
        if (user != null) {
            return ok(userProducts.render(products, user));
        } else {
            return redirect(routes.ApplicationController.signIn());
        }
    }

}
