package controllers;

import helpers.CurrentAdmin;
import play.Logger;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
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

    /**
     * Renders page where user can create new category. Only users that are administrators can create new category. To
     * create new category user needs to input name of the category that he want to create. Name of the category can
     * contain only letters and can not be already in database.
     *
     * @return Page where administrator user can create new category.
     */
    public Result newCategory(){
        return ok(newCategory.render(categoryForm));
    }

    /**
     * Renders page where administrator user can edit selected category. Edited category name can contain only letters
     * and can not be already in database. If the inputed values are incorrect warning message occurs.
     *
     * @param id - Id of the category that user wants to edit.
     * @return Page where administrator user can edit existing category.
     */
    public Result editCategory(Integer id) {
        // Finding selected category from database.
        Category category = Category.getCategoryById(id);
        // Declaring filled category form.
        Form<Category> fillForm = categoryForm.fill(category);
        // Returning filled category form.
        return ok(editCategory.render(fillForm, category));
    }

    /**
     * Enables administrator user to edit the selected category. Administrator user can only edit category name. If the
     * edited category name already exists in the database warning message occurs.
     *
     * @param id - Id of the category that user wants to edit.
     * @return If the edit is successful renders administrator panel page where all categories are listed, othervise
     *         warning message occurs.
     */
    @RequireCSRFCheck
    public Result updateCategory(Integer id){
        // Declaring category form.
        Form<Category> boundForm = categoryForm.bindFromRequest();
        // Checking if category form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(newCategory.render(boundForm));
        } else {
            // Creating new category with inputed name and saving it into database.
            Category category = Category.getCategoryById(id);
            category.name = boundForm.bindFromRequest().field("name").value();
            // Editing selected category.
            category.update();
        }
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
            product.category = Category.getCategoryById(Category.OTHER);
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
    @RequireCSRFCheck
    public Result saveCategory(){
        // Declaring category form.
        Form<Category> boundForm = categoryForm.bindFromRequest();
        // Checking if category form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(newCategory.render(boundForm));
        } else {
            // Creating new category with inputed name and saving it into database.
            Category category = new Category(boundForm.bindFromRequest().field("name").value());
            category.save();
        }

        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminCategories());
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormCategory() {
        Form<Category> binded = categoryForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }
}