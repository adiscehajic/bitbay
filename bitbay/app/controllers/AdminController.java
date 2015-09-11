package controllers;

import helpers.CurrentAdmin;
import models.Category;
import models.Product;
import models.User;
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
 * Created by Kerim on 9.9.2015.
 */
public class AdminController extends Controller {

    // Declaring variable.
    private static final Form<User> adminRegistration = Form.form(User.class);

    public Result adminLogin() { return ok(login.render()); }

    public Result adminLogout() {
        session().clear();
        return redirect(routes.AdminController.adminLogin());
    }

    @Security.Authenticated(CurrentAdmin.class)
    public Result adminIndex() {
        return ok(adminHome.render());
    }

    @Security.Authenticated(CurrentAdmin.class)
    public Result adminUsers() {
        List<User> users = User.findAll();
        return ok(allUsers.render(users));
    }

    @Security.Authenticated(CurrentAdmin.class)
    public Result adminCategories() {
        List<Category> categories = Category.findAll();
        return ok(allCategories.render(categories));
    }

    @Security.Authenticated(CurrentAdmin.class)
    public Result adminProducts() {
        List<Product> list = Product.findAll();
        return ok(allProducts.render(list));}

    public Result validateLogin() {
        // Connecting with sign in form.
        Form<User> boundForm = adminRegistration.bindFromRequest();
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
            return redirect(routes.AdminController.adminLogin());
        }
        User user = User.authenticate(email,password);
        Logger.info(user.firstName);
            // Checking if the user exists. If the inputed email and password are correct
            // redirecting to the main page, othewise opens sign in page.

            if (user == null) {
                flash("errorEmail", "Wrong email or password.");
                return redirect(routes.AdminController.adminLogin());
            } else if (user.toString().equals(" ")) {
                flash("errorNoInput", "Please input email and password.");
                return redirect(routes.AdminController.adminLogin());
            } else if (user != null && user.userType.id == 1) {
                session().clear();
                session("email", email);
                return redirect(routes.AdminController.adminIndex());
            } else {
            return redirect(routes.AdminController.adminLogin());
        }

    }

}

