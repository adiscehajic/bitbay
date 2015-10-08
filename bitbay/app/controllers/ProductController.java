package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.cloudinary.Cloudinary;
import helpers.CurrentAdmin;
import helpers.CurrentSeller;
import helpers.SessionHelper;
import models.*;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.viewProductsByCategory;
import views.html.product.newProduct;
import views.html.product.editProduct;
import views.html.product.productProfile;
import views.html.product.searchProduct;
import views.html.user.userProducts;
import views.html.category.viewProductsByCategory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adis Cehajic on 08/09/15.
 */
public class ProductController extends Controller {

    // Declaring product form.
    private static Form<Product> productForm = Form.form(Product.class);

    /**
     * Renders page where all product informations of selected product are shown.
     *
     * @param id - Id of product that user wants to see.
     * @return Page where all product informations are shown.
     */
    public Result getProduct(Integer id) {
        String averageRating = Rating.getAverageRating(id);
        Logger.info(averageRating);
        // Finding selected product.
        Product product = Product.getProductById(id);
        // Declaring image path.
        String path = Image.getImagePath(product);
        // Declaring the list of comments and the list of top comments.
        List<Comment> comments = Comment.sortCommentByDate(product);
        List<Comment> topComments = Thumb.getMostLikedComment(product);
        // Removing comments that are top comments from the list of comments.
        if (topComments.size() > 0) {
            for (int i = 0; i < topComments.size(); i++) {
                comments.remove(topComments.get(i));
            }
        }
        // Rendering product profile page.
        return ok(productProfile.render(product, path, comments, topComments, averageRating));
    }

    /**
     * Renders page where new product can be inputed. When inputing new product all required fields must be inputed,
     * otherwise warning message occurs. Only user that is seller can add new products.
     *
     * @return Page where new product can be inputed.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result newProduct() {
        // Declaring the list of categories.
        List<Category> categories = Category.findAll();
        // Rendering the page for the new product input.
        return ok(newProduct.render(productForm, categories));
    }

    /**
     * Deletes selected product. Only user that has added the selected product can delete selected product.
     *
     * @param id - Id of the product that user wants to delete.
     * @return If the deleting of the product was successful renders page where all product of user are listed.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result deleteProduct(Integer id) {
        // Finding selected product.
        Product product = Product.getProductById(id);
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Deleting product.
        if (user.id == product.user.id) {
            product.delete();
        }
        // Declaring the list of the products from current user.
        List<Product> products = Product.findAllProductsByUser(user);
        // Rendering the page where the list of current user product are listed.
        return ok(userProducts.render(products, user));
    }

    /**
     * Deletes selected product. This method is called when administrator user wants to delete selected product on
     * administrator panel.
     *
     * @param id - Id of the product that user wants to delete.
     * @return Administrator panel page where all products of the application are listed.
     */
    @Security.Authenticated(CurrentAdmin.class)
    public Result deleteProductAdmin(Integer id) {
        Product product = Product.getProductById(id);

        product.delete();

        return redirect(routes.AdminController.adminProducts());
    }

    @RequireCSRFCheck
    @Security.Authenticated(CurrentSeller.class)
    public Result saveProduct() {
        Form<Product> boundForm = productForm.bindFromRequest();

        if (boundForm.hasErrors()) {
            return badRequest(newProduct.render(boundForm, Category.findAll()));
        }

        Image.cloudinary = new Cloudinary("cloudinary://" + Play.application().configuration().getString("cloudinary.string"));
        User user = User.getUserByEmail(session().get("email"));

        String categoryValue = boundForm.bindFromRequest().field("category").value();

        Category category = Category.getCategoryByName(categoryValue);

        Product product = boundForm.get();
        product.user = user;
        product.category = category;
        product.save();

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart filePart = body.getFile("image");

        if (filePart != null) {
            File file = filePart.getFile();
            Image image = Image.create(file, product.id);
            image.save();
        }
        return redirect(routes.ApplicationController.index());
    }

    @Security.Authenticated(CurrentSeller.class)
    public Result editProduct(Integer id) {
        Product product = Product.getProductById(id);
        List<Category> categories = Category.findAll();
        categories.remove(product.category);

        Form<Product> filledForm = productForm.fill(product);
        return ok(editProduct.render(filledForm, product, categories));
    }

    @RequireCSRFCheck
    @Security.Authenticated(CurrentSeller.class)
    public Result updateProduct(Integer id) {
        Product product = Product.getProductById(id);
        Form<Product> boundForm = productForm.bindFromRequest();
        List<Category> categories = Category.findAll();
        categories.remove(product.category);

        if (boundForm.hasErrors()) {
            return badRequest(editProduct.render(boundForm, product, categories));
        }

        try {
            String categoryValue = boundForm.bindFromRequest().field("category").value();
            Category category = Category.getCategoryByName(categoryValue);

            product.category = category;
            product.name = boundForm.bindFromRequest().field("name").value();
            product.description = boundForm.bindFromRequest().field("description").value();
            product.manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
            product.price = Double.parseDouble(boundForm.bindFromRequest().field("price").value());
            product.quantity = Integer.parseInt(boundForm.bindFromRequest().field("quantity").value());
            product.sellingType = boundForm.bindFromRequest().field("sellingType").value();

            product.update();

            return redirect(routes.ProductController.getProduct(product.id));
        } catch (Exception e) {
            Logger.info("ERROR: Product update failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(editProduct.render(boundForm, product, categories));
        }
    }

    public Result viewProductsByCategory(Integer id) {
        Category category = Category.getCategoryById(id);
        List<Product> products = Product.findAllProductsByCategory(category);

        return ok(viewProductsByCategory.render(products, category));
    }


    public Result searchProduct(){

        Form<Product> boundForm = productForm.bindFromRequest();
        String term = boundForm.bindFromRequest().field("search").value();

        List<Product> products = Product.searchProductByName(term);

        return ok(searchProduct.render(products));
    }



    public Result autocompleteSearch() {
        Form<Product> boundForm = productForm.bindFromRequest();

        String term = boundForm.bindFromRequest().field("search").value();

        List<Product> products = Product.searchProductByName(term);

        List<String> names = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            names.add(products.get(i).name);
        }

        JsonNode object = Json.toJson(names);

        return ok(object);
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormProduct() {
        Form<Product> binded = productForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

    public Result productRating() {
        DynamicForm form = Form.form().bindFromRequest();
        String productId = form.get("productId");
        Integer id = Integer.parseInt(productId);
        Product product = Product.getProductById(id);
        User user = SessionHelper.currentUser();
        String numOfStars = form.get("rating");
        Integer intRatingValue = Integer.parseInt(numOfStars);


        if(Rating.hasRated(product)) {
            Logger.info(product.name);
            Rating rating = Rating.getRating(product);
            rating.rate = intRatingValue;
           // Logger.info(rating.rate.toString());
            rating.update();
        } else {
            Rating rating = new Rating(user, product, intRatingValue);
            rating.save();
        }
        JsonNode object = Json.toJson(numOfStars);
        return ok(object);
    }

}
