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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Adis Cehajic on 08/09/15.
 */
public class ProductController extends Controller {

    // Declaring product form.
    private static Form<Product> productForm = Form.form(Product.class);
    private static Form<Auction> auctionForm = Form.form(Auction.class);

    /**
     * Renders page where all product informations of selected product are shown.
     *
     * @param id - Id of product that user wants to see.
     * @return Page where all product informations are shown.
     */
    public Result getProduct(Integer id) {
        // Getting the average rating of the product.
        String averageRating = Rating.getAverageRating(id);
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
        // Getting current user from session.
        User user = SessionHelper.currentUser();
        // Finding category of selected product.
        Category category = Category.getCategoryById(product.category.id);
        // Saving that user has viewed product.
        if (user != null) {
            Recommendation.savingProductView(user, product);
        }
        // Finding the list of recommended products.
        List<Product> recommendedProducts = Recommendation.getRecommendedProducts(product);
        // Rendering product profile page.
        return ok(productProfile.render(product, path, comments, topComments, recommendedProducts, averageRating));
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
        // Finding selected product.
        Product product = Product.getProductById(id);
        // Deleting selected product.
        product.delete();
        // Rendering the page where the list of current user product are listed.
        return redirect(routes.AdminController.adminProducts());
    }

    /**
     * Enables user to input new product that he wants to sell. When inputing new product user needs to input all
     * required fields. Also user can input 5 pictures ot the product that he wants to sell. If the inputed required
     * fields have errors warning message occurs. Only after user had correct mistakes can new product be inputed.
     *
     * @return If create of the new product is successful renders page where all user products are listed, othervise
     * warning message occurs.
     */
    @RequireCSRFCheck
    @Security.Authenticated(CurrentSeller.class)
    public Result saveProduct() {
        // Declaring product form.
        Form<Product> boundForm = productForm.bindFromRequest();
        // Checking if form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(newProduct.render(boundForm, Category.findAll()));
        }
        // Finding current user.
        User user = SessionHelper.currentUser();
        try {
            // Initializing cloudinery object.
            Image.cloudinary = new Cloudinary("cloudinary://" + Play.application().configuration().getString("cloudinary.string"));
            // Declaring string variable for the selected product category.
            String categoryValue = boundForm.bindFromRequest().field("categoryId").value();
            // Declaring selected category.
            Category category = Category.getCategoryByName(categoryValue);
            // Creating new product and adding selected values.
            //finding cancelation duration selected by seller
            Product product = boundForm.get();
            product.user = user;
            product.category = category;
            // Saving product into database.
            product.save();
            // Getting selected images
            Http.MultipartFormData body = request().body().asMultipartFormData();
            List<Http.MultipartFormData.FilePart> fileParts = body.getFiles();
            // Uploading selected images on cloudinery and saving image path into database.
            if (fileParts != null) {
                for (Http.MultipartFormData.FilePart filePart : fileParts) {
                    File file = filePart.getFile();
                    Image image = Image.create(file, product.id);
                    image.save();
                }
            }
            // Checking if the selling type of the product is by auction.
            if (product.sellingType.equals("2")) {
                // Connecting with auction form.
                Form<Auction> auctionBoundForm = auctionForm.bindFromRequest();
                // Creating new auction.
                Auction auction = auctionBoundForm.get();
                // Declaring the variable that represents how long is the duration of the auction.
                String days = auctionBoundForm.data().get("auction-duration");
                //String buyItNowPrice = auctionBoundForm.data().get("buy-it-now-price");
                // Updating the product price and quantity.
                product.price = auction.startingPrice;
                product.quantity = 1;
                product.update();
                // Setting the starting and ending date of the auction and saving the auction.
                auction.startingDate = new Date();
                auction.product = product;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, Integer.parseInt(days));
                auction.endingDate = calendar.getTime();
                auction.save();
            }
        }catch(RuntimeException e) {
            Logger.info("Uploaded file is not image file.");
            return badRequest(newProduct.render(boundForm, Category.findAll()));
        }
        // Redirecting to the main application page.
        return redirect(routes.Users.getAllUserProducts(user.email));
    }

    /**
     * Renders page where user can edit selected product. When editing user needs to input correct values, otherwise
     * warning message occurs. User can also select new images for selected product. If user selects new images, old
     * images will be deleted.
     *
     * @param id Id of selected product.
     * @return Page where user can edit selected product.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result editProduct(Integer id) {
        // Finding selected product.
        Product product = Product.getProductById(id);
        // Finding all categories.
        List<Category> categories = Category.getAllSubcategories(Category.getParentCategory(product.category.parent));
        // Removing category of the product from the list of categories.
        categories.remove(product.category);
        // Creating product form that is filled with product information.
        Form<Product> filledForm = productForm.fill(product);
        // Rendering product edit page.
        return ok(editProduct.render(filledForm, product, categories));
    }

    /**
     * Enables user to edit selected product. If all inputed values are correct update of the selected product will be
     * successgful, otherwise warning message occurs.
     *
     * @param id - Id of the seleced product.
     * @return If the edit is successful renders product profile page where all product information are shown, othervise
     *         warning message occurs.
     */
    @RequireCSRFCheck
    @Security.Authenticated(CurrentSeller.class)
    public Result updateProduct(Integer id) {
        // Finding selected product.
        Product product = Product.getProductById(id);
        // Declaring product form.
        Form<Product> boundForm = productForm.bindFromRequest();
        // Finding all categories.
        List<Category> categories = Category.findAll();
        // Removing category of the product from the list of categories.
        categories.remove(product.category);
        // Checking if form has errors.
        if (boundForm.hasErrors()) {
            return badRequest(editProduct.render(boundForm, product, categories));
        }

        try {
            // Getting all inputed values and updating product into database.
            String categoryValue = boundForm.bindFromRequest().field("categoryId").value();
            Category category = Category.getCategoryByName(categoryValue);

            product.category = category;
            product.name = boundForm.bindFromRequest().field("name").value();
            product.description = boundForm.bindFromRequest().field("description").value();
            product.sellingType = boundForm.bindFromRequest().field("sellingType").value();
            product.manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
            // If the product selling type is not auction user can update product selling price and quantity.
            if (product.auction == null) {
                product.price = Double.parseDouble(boundForm.bindFromRequest().field("price").value());
                product.quantity = Integer.parseInt(boundForm.bindFromRequest().field("quantity").value());
            }
            // Saving all changes into database.
            product.update();
            // Getting selected images
            Http.MultipartFormData body = request().body().asMultipartFormData();
            List<Http.MultipartFormData.FilePart> fileParts = body.getFiles();
            // Checking if the product already has uploaded images. If product has images, first deletes all old images
            // and then saves new images.
            if (product.images.size() > 0 && fileParts.size() > 0) {
                for (int i = 0; i < product.images.size(); i++) {
                    product.images.get(i).deleteImage();
                    product.images.get(i).delete();
                }
            }
            // Uploading selected images on cloudinery and saving image path into database.
            if (fileParts != null) {
                for (Http.MultipartFormData.FilePart filePart : fileParts) {
                    File file = filePart.getFile();
                    Image image = Image.create(file, product.id);
                    image.save();
                }
            }
            // Rendering product profile page.
            return redirect(routes.ProductController.getProduct(product.id));
        } catch (Exception e) {
            Logger.info("ERROR: Product update failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return badRequest(editProduct.render(boundForm, product, categories));
        }
    }

    /**
     * Renders page where all products of the selected category are listed.
     *
     * @param id - Id of the selected category.
     * @return Page where all products of the selected category are listed.
     */
    public Result viewProductsByCategory(Integer id) {
        // Finding selected category.
        Category category = Category.getCategoryById(id);
        // Finding all products from selected category.
        List<Product> products = Product.findAllProductsByCategory(category);
        // Rendering page where all products of the selected category are listed.
        return ok(viewProductsByCategory.render(products, category));
    }

    /**
     * Renders page where all searched products are listed. List of searched products depends on inputed values in the
     * search field. User can search product by name and description. List of searched products can be sorted by name,
     * price and date of selling.
     *
     * @return Page where all searched products are listed.
     */
    public Result searchProduct(){
        // Declaring product form.
        Form<Product> boundForm = productForm.bindFromRequest();
        // Getting inputed searched term.
        String term = boundForm.bindFromRequest().field("search").value();
        // Finding all products that have in name or description searched tarm.
        List<Product> products = null;
        if(!"".equals(term)) {
            products = Product.searchProductByName(term);
            // Rendering page where all searched products are listed.
            return ok(searchProduct.render(products));
        }
        return redirect(routes.ApplicationController.index());
    }

    /**
     * Returns the list of names of all products, that contain searched term, as Json.
     *
     * @return The list of names of products.
     */
    public Result autocompleteSearch() {
        // Declaring product form.
        Form<Product> boundForm = productForm.bindFromRequest();
        // Getting inputed searched term.
        String term = boundForm.bindFromRequest().field("search").value();
        // Finding all products that have in name or description searched tarm.
        List<Product> products = Product.searchProductByName(term);
        // Declaring the list of strings.
        List<String> names = new ArrayList<>();
        // Adding into list all names of searched products.
        for (int i = 0; i < products.size(); i++) {
            names.add(products.get(i).name);
        }
        // Converting list of names into Json.
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

    /**
     * Saves the rating of the product that current user has selected. If the user has rated the product multiple times
     * it always saves the last rate. Only users that had bought product can rate the product.
     *
     * @return The JSON object that contains the rate of the product.
     */
    public Result productRating() {
        // Connecting with the form.
        DynamicForm form = Form.form().bindFromRequest();
        // Declaring the variable that contains the product id.
        String productId = form.get("productId");
        // Finding the selected product.
        Integer id = Integer.parseInt(productId);
        Product product = Product.getProductById(id);
        User user = SessionHelper.currentUser();
        // Declaring the variable that contain the user rate of the product.
        String numOfStars = form.get("rating");
        Integer intRatingValue = Integer.parseInt(numOfStars);
        // Checking has the user rated the product. If the user has rated the product multiple times it always saves
        // the last rate, otherwise saves the new rate.
        if(Rating.hasRated(product)) {
            Rating rating = Rating.getRating(product);
            rating.rate = intRatingValue;
            rating.update();
        } else {
            Rating rating = new Rating(user, product, intRatingValue);
            rating.save();
        }
        // Declaring and returning JSON object.
        JsonNode object = Json.toJson(numOfStars);
        return ok(object);
    }
}