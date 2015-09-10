package controllers;

import models.Category;
import models.Product;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.admin.allCategories;
import views.html.admin.login;
import views.html.admin.allProducts;
import views.html.admin.allUsers;
import views.html.admin.newCategory;

import java.util.List;

/**
 * Created by Kerim on 9.9.2015.
 */
public class AdminController extends Controller {

    public Result adminLogin() { return ok(login.render()); }

    public Result adminUsers() {
        List<User> users = User.findAll();
        return ok(allUsers.render(users));
    }

    public Result adminCategories() {
        List<Category> categories = Category.findAll();
        return ok(allCategories.render(categories));
    }

    public Result adminProducts() {
        List<Product> list = Product.findAll();
        return ok(allProducts.render(list));}

}

