package controllers;

import helpers.CurrentAdmin;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.newCategory;
import views.html.admin.editCategory;
import java.lang.*;
import java.util.List;


import com.avaje.ebean.Ebean;
import models.*;


/**
 * Created by Adnan on 8.9.2015.
 */
@Security.Authenticated(CurrentAdmin.class)
public class CategoryController extends Controller {

    private static final Form<Category> categoryForm = Form.form(Category.class);

    /**
     * Method for creating new Category
     * @return
     */
    public Result newCategory(){
        return ok(newCategory.render());
    }

    public Result editCategory(Integer id) {
        Category c = Category.getCategoryById(id);
        return ok(editCategory.render(c));
    }

    /**
     * Method for makeing changes on single Category
     * @param id - ID of selected Category
     * @return
     */
    public Result updateCategory(Integer id){
        Category c = Category.getCategoryById(id);
        Form<Category> boundForm = categoryForm.bindFromRequest();
        String name = boundForm.bindFromRequest().field("categoryName").value();

        if(!name.equals(c.name)) {
            c.name = name;
        }
        Ebean.update(c);
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * This method delete selected Category from database
     * @param id - ID of a selected Category
     * @return
     */
    public Result deleteCategory(Integer id){
       Category c = Category.getCategoryById(id);
        Ebean.delete(c);
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * This method is used to save new category to database
     */
    public Result saveCategory(){
        Form<Category> boundForm = categoryForm.bindFromRequest();

        String name = boundForm.bindFromRequest().field("categoryName").value();
        Logger.info(name);
        Category c = new Category(name);
        Ebean.save(c);

        List<Category> list = Category.findAll();
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * This method is used to list all users from database
     * @return - List of users
     */
    public Result list(){
        List<Category> categories = Category.findAll();
        return TODO;
    }

}

