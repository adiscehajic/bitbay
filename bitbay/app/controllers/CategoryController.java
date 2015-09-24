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
 * Created by Adnan Lapendic on 8.9.2015.
 */
@Security.Authenticated(CurrentAdmin.class)
public class CategoryController extends Controller {

    // Declaring category form.
    private static final Form<Category> categoryForm = Form.form(Category.class);
    // Declaring constant that represents 'Other' category id.
    private static final Integer OTHER = 1;

    /**
     * Renders page where user can create new category. Only users that are administrators can create new category. To
     * create new category user needs to input name of the category that he want to create. Name of the category can
     * contain only letters and can not be already in database.
     *
     * @return Page where administrator user can create new category.
     */
    public Result newCategory(){
        return ok(newCategory.render());
    }

    /**
     * Renders page where administrator user can edit selected category. Edited category name can contain only letters
     * and can not be already in database. If the inputed values are incorrect warning message occurs.
     *
     * @param id - Id of the category that user wants to edit.
     * @return Page where administrator user can edit existing category.
     */
    public Result editCategory(Integer id) {
        //
        Category category = Category.getCategoryById(id);
        return ok(editCategory.render(category));
    }

    /**
     * Enables administrator user to edit the selected category. Administrator user can only edit category name. If the
     * edited category name already exists in the database warning message occurs.
     *
     * @param id - Id of the category that user wants to edit.
     * @return If the edit is successful renders administrator panel page where all categories are listed, othervise
     *         warning message occurs.
     */
    public Result updateCategory(Integer id){
        // Finding selected category from database.
        Category category = Category.getCategoryById(id);
        // Declaring category form.
        Form<Category> boundForm = categoryForm.bindFromRequest();
        // Declaring string variable that represents inputed name of the category.
        String name = boundForm.bindFromRequest().field("categoryName").value();

        try {
            // Declaring list of all categories.
            List<Category> categories = Category.findAll();
            // Going trough all categories and checking if there is already category with same name.
            for(Category cat: categories){
                if(cat.name.equals(name)){
                    flash("saveCategoryEqualError", "Category already exists.");
                    throw new Exception();
                }
            }
            // Checking does the inputed name contains letters.
            if (!name.matches("^[a-z A-Z]*$")) {
                flash("saveCategoryDigitError", "Category name can't contain digits.");
                throw new Exception();
            }
            // Checking if anything is inputed.
            if(name.isEmpty()){
                flash("saveCategoryEmptyError", "Category name can't be empty string.");
                throw new Exception();
            }else{
                // Creating new category with inputed name and saving it into database.
                Category c = new Category(name);
                c.save();
            }
        }catch (Exception e){
            Logger.info("ERROR: Registration failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(editCategory.render(category));
        }
        // Editing selected category.
        category.update();
        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * Deletes selected category from database.
     *
     * @param id - Id of the category that user wants to edit.
     * @return Administrator panel page where all categories are listed.
     */
    public Result deleteCategory(Integer id){
        // Finding selected category from database.
        Category category = Category.getCategoryById(id);
        // Creating list of products from selected category.
        List<Product> products = Product.findAllProductsByCategory(category);
        // Going trough all products from selected category and putting them into 'Other' category.
        for (Product product : products) {
            product.category = Category.getCategoryById(OTHER);
            product.save();
        }
        // Deleting selected category.
        category.delete();
        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * Enables administrator user to create new category. When creating new category administrator user needs to input
     * category name. If the inputed category name already exists in the database or if the inputed category name is
     * not in the rigth format warning message occurs.
     *
     * @return If create of the new category is successful renders administrator panel page where all categories
     * are listed, othervise warning message occurs.
     */
    public Result saveCategory(){
        // Declaring category form.
        Form<Category> boundForm = categoryForm.bindFromRequest();
        // Declaring string variable that represents inputed name of the category.
        String name = boundForm.bindFromRequest().field("categoryName").value();

        try {
            // Declaring list of all categories.
            List<Category> categories = Category.findAll();
            // Going trough all categories and checking if there is already category with same name.
            for(Category category: categories){
                if(category.name.equals(name)){
                    flash("saveCategoryEqualError", "Category already exists.");
                    throw new Exception();
                }
            }
            // Checking does the inputed name contains letters.
            if (!name.matches("^[a-z A-Z]*$")) {
                flash("saveCategoryDigitError", "Category name can't contain digits.");
                throw new Exception();
            }
            // Checking if anything is inputed.
            if(name.isEmpty()){
                flash("saveCategoryEmptyError", "Category name can't be empty string.");
                throw new Exception();
            }else{
                // Creating new category with inputed name and saving it into database.
                Category category = new Category(name);
                category.save();
            }
        }catch (Exception e){
            Logger.info("ERROR: Registration failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(newCategory.render());
        }
        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminCategories());
    }
}