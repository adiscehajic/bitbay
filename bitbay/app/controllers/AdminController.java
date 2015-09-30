package controllers;

import helpers.CurrentAdmin;
import models.Category;
import models.Product;
import models.User;
import controllers.ApplicationController.UserLogin;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.allCategories;
import views.html.admin.login;
import views.html.admin.allProducts;
import views.html.admin.allUsers;
import views.html.admin.adminHome;
import java.util.List;

/**
 * Created by Kerim Dragolj on 9.9.2015.
 */
public class AdminController extends Controller {

    // Declaring user form.
    private static final Form<UserLogin> adminLogin = Form.form(UserLogin.class);
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
        return ok(login.render(adminLogin));
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
    public Result validateLogin() {
        // Connecting with sign in form.
        Form<UserLogin> boundForm = adminLogin.bindFromRequest();
        // Reading inputed values and storing them into string variables.
        String email = boundForm.bindFromRequest().field("email").value();
        String password = boundForm.bindFromRequest().field("password").value();

        if (boundForm.hasErrors()) {
            flash("adminError", "Please insert valid email and password.");
            return badRequest(login.render(boundForm));
        }
        try {
            // Calling method authenticate and creating new user.
            User user = User.authenticate(email,password);
            // Checking if the user exists. If the inputed email and password are correct
            // redirecting to the main page, othewise opens sign in page.
            if (user == null) {
                flash("errorEmail", "Wrong email or password.");
                return redirect(routes.AdminController.adminLogin());
            } else if (user.toString().equals(" ")) {
                flash("errorNoInput", "Please input email and password.");
                return redirect(routes.AdminController.adminLogin());
            } else if (user != null && user.userType.id == ADMIN) {
                // Clearing all sessions and creating new session that stores user email
                session().clear();
                session("email", email);
                // Redirecting to the main page.
                return redirect(routes.AdminController.adminIndex());
            } else {
                return redirect(routes.AdminController.adminLogin());
            }
        } catch (Exception e) {
            Logger.info("ERROR: UserLogin failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(login.render(boundForm));
        }
    }
}

