package controllers;

import helpers.CurrentAdmin;
import models.Category;
import models.Product;
import models.User;
import controllers.ApplicationController.UserLogin;
import models.UserType;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.allCategories;
import views.html.admin.login;
import views.html.admin.allProducts;
import views.html.admin.allUsers;
import views.html.admin.adminHome;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kerim Dragolj on 9.9.2015.
 */
public class AdminController extends Controller {

    // Declaring administrator login form.
    private static final Form<AdminLogin> adminLoginForm = Form.form(AdminLogin.class);

    // Declaring constant for admin user type.
    private static final Integer ADMIN = 1;

    /**
     * Renders page wher user that is administor of the application can sign in. If the user is not administrator, or
     * there is not user in database warning message occurs. If the email and password are correct it redirrects to the
     * main administrator panel.
     *
     * @return Page where user that is administrator can sign in.
     */
    public Result adminLogin() {
        return ok(login.render(adminLoginForm));
    }

    /**
     * Clears all current sessions and redirects to the administrator sign in page.
     *
     * @return Page where user that is administrator can sign in.
     */
    public Result adminLogout() {
        session().clear();
        return redirect(routes.AdminController.adminLogin());
    }

    /**
     * Renders main administrator panel page where administrator user can access to the list of the users, categories
     * and products. Then administrator user can decide what he want to do.
     *
     * To this page can access only users that are administrators.
     *
     * @return Page where administrator user can access to the users, categorise and products.
     */
    @Security.Authenticated(CurrentAdmin.class)
    public Result adminIndex() {
        return ok(adminHome.render());
    }

    /**
     * Renders administrator panel page where all users of the application are listed. Administrator user can decide if
     * he wants to delete user. Administrator user can not update users.
     *
     * To this page can access only users that are administrators.
     *
     * @return Page where all users of the application are listed.
     */
    @Security.Authenticated(CurrentAdmin.class)
    public Result adminUsers() {
        // Creating the list of the users.
        List<User> users = User.findAll();
        return ok(allUsers.render(users));
    }

    /**
     * Renders administrator panel page where all categories of the application are listed. Administrator user can
     * create new category and update existing category. Also if he decide he can delete existing category. When
     * existing category is deleted, all product from that category are transfered to the 'Other' category.
     *
     * To this page can access only users that are administrators.
     *
     * @return Page where all categories are listed.
     */
    @Security.Authenticated(CurrentAdmin.class)
    public Result adminCategories() {
        // Creating the list of the categories.
        List<Category> categories = Category.findAll();
        return ok(allCategories.render(categories));
    }

    /**
     * Renders administrator panel page where all products of the application are listed. Administrator user can decide
     * if he wants to delete product. Administrator user can not update product.
     *
     * To this page can access only users that are administrators.
     *
     * @return Page where all products of the application are listed.
     */
    @Security.Authenticated(CurrentAdmin.class)
    public Result adminProducts() {
        // Creating the list of the products.
        List<Product> list = Product.findAll();
        return ok(allProducts.render(list));
    }

    /**
     * Reads values that are inputed on administrator panel sign in page and validates them. On sign in administrator
     * user needs to input correct email and password. If the email or password are incorrect, or if the email or
     * password are not inputed, warning message occurs. Also if the email and password are correct and user is not
     * administrator warning message occurs.
     *
     * @return Opens administrator panel index page if the sign in is successful and redirects administrator panel page
     * if the sign in has errors.
     */
    @RequireCSRFCheck
    public Result validateLogin() {
        // Connecting with administrator login form.
        Form<AdminLogin> boundForm = adminLoginForm.bindFromRequest();
        if(boundForm.hasErrors()){
            return badRequest(login.render(boundForm));
        } else {
            // Clearing all sessions and creating new session that stores user email
            //LoginHelper.LoginUser(boundForm.get());
            session().clear();
            session("email", boundForm.bindFromRequest().field("email").value());
            // Redirecting to the main administrator page.
            return redirect(routes.AdminController.adminIndex());
        }
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormAdmin() {
        Form<AdminLogin> binded = adminLoginForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

    /**
     * Created by Adis Cehajic on 30.9.2015.
     */
    public static class AdminLogin {
        // Declaring properties
        @Constraints.Email(message = "Valid email format required.")
        @Constraints.Required(message = "Please input email.")
        public String email;
        @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
        @Constraints.Required(message = "Please input password.")
        public String password;

        /**
         * Validates the admin login form and returns all errors that occur during administrator login.
         * @return Errors that have occur during administrator login.
         */
        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<>();
            // Calling method authenticate and creating new user.
            User user = User.authenticate(email, password);
            // Checking if the user exists. If the inputed email and password are correct
            // redirecting to the main page, othewise opens sign in page.
            if (user == null || user.userType.id != UserType.ADMIN) {
                errors.add(new ValidationError("password", "Wrong email or password."));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}

