package controllers;

import helpers.ConstantsHelper;
import helpers.CurrentAdmin;
import com.fasterxml.jackson.databind.JsonNode;
import helpers.CurrentAdmin;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.newCategory;
import views.html.admin.editCategory;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

import models.*;


/**
 * Created by Adnan Lapendic on 8.9.2015.
 */
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
    @Security.Authenticated(CurrentAdmin.class)
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
    @Security.Authenticated(CurrentAdmin.class)
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
    @Security.Authenticated(CurrentAdmin.class)
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
    @Security.Authenticated(CurrentAdmin.class)
    public Result deleteCategory(Integer id){
        // Finding selected category from database.
        Category category = Category.getCategoryById(id);
        // Going trough all products from selected category and putting them into 'Other' category.

        if (category.parent != null) {
            // Creating list of products from selected category.
            List<Product> products = Product.findAllProductsByCategory(category);
            for (Product product : products) {
                product.category = Category.getCategoryById(ConstantsHelper.CATEGORY_OTHER);
                product.save();
            }
        } else {
            // Finding all subcategories of selected category.
            List<Category> subcategories = Category.getAllSubcategories(category);
            for (Category subcategory : subcategories) {
                // Creating list of products from selected category.
                List<Product> products = Product.findAllProductsByCategory(subcategory);
                for (Product p : products) {
                    p.category = Category.getCategoryById(ConstantsHelper.CATEGORY_OTHER);
                    p.save();
                }
            }
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
    @Security.Authenticated(CurrentAdmin.class)
    public Result saveCategory(){
        // Declaring category form.
        Form<Category> boundForm = categoryForm.bindFromRequest();
        // Checking if category form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(newCategory.render(boundForm));
        } else {
            if (boundForm.data().get("parentName") == null) {
                // Creating new category with inputed name and saving it into database.
                Category category = new Category(boundForm.bindFromRequest().field("name").value(), null);
                category.save();
            } else {
                // Creating new category with inputed name and saving it into database.
                Category category = new Category(boundForm.bindFromRequest().field("name").value(), Category.getCategoryByName(boundForm.data().get("parentName")));
                category.save();
            }
        }

        // Redirecting to the administrator panel page where all categories are listed.
        return redirect(routes.AdminController.adminCategories());
    }

    public Result findAllSubcategories(){
        DynamicForm form = Form.form().bindFromRequest();

        String categoryName = form.get("categoryName");

        List<Category> subcategories = Category.getAllSubcategories(Category.getCategoryByName(categoryName));

        List<String> subcategoryNames = new ArrayList<>();

        for (Category c : subcategories) {
            subcategoryNames.add(c.name);
        }

        JsonNode object = Json.toJson(subcategoryNames);

        return ok(object);
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