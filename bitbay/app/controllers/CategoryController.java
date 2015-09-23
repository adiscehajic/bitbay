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
import views.html.category.viewProductsByCategory;


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
     * Method for making changes on single Category
     * @param id - ID of selected Category
     * @return
     */
    public Result updateCategory(Integer id){
        Category c = Category.getCategoryById(id);
        Form<Category> boundForm = categoryForm.bindFromRequest();
        String name = boundForm.bindFromRequest().field("categoryName").value();

        try {
            List<Category> categories = Category.findAll();
            for(Category category: categories){
                if(category.name.equals(name)){
                    flash("editCategoryEqualError", "Category already exists.");
                    throw new Exception();
                }
            }
            if (!name.equals(c.name)) {
                    if (!name.matches("^[a-z A-Z]*$")) {
                        flash("editCategoryDigitError", "Category name can't contain digits.");
                        throw new Exception();
                    }

                if(name.isEmpty()){
                    flash("editCategoryEmptyError", "Category name can't be empty string.");
                    throw new Exception();
                }else{
                    c.name = name;
                }
            }
        }catch (Exception e){
        return badRequest(editCategory.render(c));
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

        List<Product> products = Product.findAllProductsByCategory(c);

        for (Product product : products) {
            product.category = Category.getCategoryById(5);
            product.save();
        }

        Ebean.delete(c);
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * This method is used to save new category to database
     */
    public Result saveCategory(){
        Form<Category> boundForm = categoryForm.bindFromRequest();

        String name = boundForm.bindFromRequest().field("categoryName").value();

        try {
            List<Category> categories = Category.findAll();
            for(Category category: categories){
                if(category.name.equals(name)){
                    flash("saveCategoryEqualError", "Category already exists.");
                    throw new Exception();
                }
            }
                    if (!name.matches("^[a-z A-Z]*$")) {
                        flash("saveCategoryDigitError", "Category name can't contain digits.");
                        throw new Exception();
                    }
                if(name.isEmpty()){
                    flash("saveCategoryEmptyError", "Category name can't be empty string.");
                    throw new Exception();
                }else{
                    Category c = new Category(name);
                    Ebean.save(c);
                }
        }catch (Exception e){
            return badRequest(newCategory.render());
        }



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

