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
import play.data.Form;
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
        Product product = Product.getProductById(id);
        String path = Image.getImagePath(product);

        Logger.info("Product id je: " + product.id);

        List<Comment> comments = Comment.sortCommentByDate(product);
        List<Comment> topComments = Thumb.getMostLikedComment(product);

        if (topComments.size() > 0) {
            for (int i = 0; i < topComments.size(); i++) {
                comments.remove(topComments.get(i));
            }
        }

        return ok(productProfile.render(product, path, comments, topComments));
    }

    /**
     * Renders page where new product can be inputed. When inputing new product all required fields must be inputed,
     * otherwise warning message occurs. Only user that is seller can add new products.
     *
     * @return Page where new product can be inputed.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result newProduct() {
        List<Category> categories = Category.findAll();
        return ok(newProduct.render(categories));
    }

    /**
     * Deletes selected product. Only user that has added the selected product can delete selected product.
     *
     * @param id - Id of the product that user wants to delete.
     * @return If the deleting of the product was successful renders page where all product of user are listed.
     */
    @Security.Authenticated(CurrentSeller.class)
    public Result deleteProduct(Integer id) {
        Product product = Product.getProductById(id);

        User user = SessionHelper.currentUser();

        if (user.id == product.user.id) {
            product.delete();
        }

        List<Product> products = Product.findAllProductsByUser(user);
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


    @Security.Authenticated(CurrentSeller.class)
    public Result saveProduct() {
        Form<Product> boundForm = productForm.bindFromRequest();

        if (boundForm.hasErrors()) {
            return badRequest(newProduct.render(Category.findAll()));

        }

        Image.cloudinary = new Cloudinary("cloudinary://" + Play.application().configuration().getString("cloudinary.string"));
        User user = User.getUserByEmail(session().get("email"));
        //String name = boundForm.bindFromRequest().field("name").value();
        //String description = boundForm.bindFromRequest().field("description").value();
        //String manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
        String categoryValue = boundForm.bindFromRequest().field("category").value();
        //String price = boundForm.bindFromRequest().field("price").value();
        //String quantity = boundForm.bindFromRequest().field("quantity").value();
        //String sellingType = boundForm.bindFromRequest().field("sellingType").value();

        Category category = Category.getCategoryByName(categoryValue);
        List<Category> categories = Category.findAll();

        /*if (name.isEmpty()) {
            flash("saveProductNameError", "Please enter product name.");
            return badRequest(newProduct.render(categories));
        }



        if (Integer.parseInt(price) <= 0) {
            flash("saveCategoryLowPriceError", "Price can't be 0 or lower.");
            return badRequest(newProduct.render(categories));

        }

        if (price.isEmpty()) {
            flash("saveProductEmptyPriceError", "Please enter price.");
            return badRequest(newProduct.render(categories));
        }

        if (quantity.isEmpty()) {
            flash("saveProductEmptyQuantityError", "Please enter product quantity.");
            return badRequest(newProduct.render(categories));
        }

        if (categoryValue == null) {
            flash("saveCategoryEmptyCategoryError", "Please select category.");
            return badRequest(newProduct.render(categories));
        }

        if (sellingType == null) {
            flash("saveCategoryEmptySellingTypeyError", "Please select selling type.");
            return badRequest(newProduct.render(categories));
        }*/

        Product product = boundForm.get();
        product.user = user;
        product.category = category;
        //Product product = new Product(user, name, description, manufacturer, category, Double.parseDouble(price), Integer.parseInt(quantity), sellingType);
        product.save();

        //Ebean.save(product);

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

        Form<Product> filledForm = productForm.fill(product);
        return ok(editProduct.render(product));
    }

    @Security.Authenticated(CurrentSeller.class)
    public Result updateProduct(Integer id) {
        Product product = Product.getProductById(id);
        Form<Product> boundForm = productForm.bindFromRequest();

        String name = boundForm.bindFromRequest().field("name").value();
        String description = boundForm.bindFromRequest().field("description").value();
        String manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
        String price = boundForm.bindFromRequest().field("price").value();
        String quantity = boundForm.bindFromRequest().field("quantity").value();
        String sellingType = boundForm.bindFromRequest().field("type").value();

        List<Category> categories = Category.findAll();

        if (name.isEmpty()) {
            flash("editProductNameError", "Please enter product name.");
            return badRequest(editProduct.render(product));
        }

        if (Double.parseDouble(price) <= 0) {
            flash("editCategoryLowPriceError", "Price can't be 0 or lower.");
            return badRequest(editProduct.render(product));

        }

        if (price.isEmpty()) {
            flash("editProductEmptyPriceError", "Please enter price.");
            return badRequest(editProduct.render(product));
        }

        if (Integer.parseInt(quantity) < 0) {
            flash("editProductEmptyQuantityError", "Quantity can't be 0 or lower.");
            return badRequest(editProduct.render(product));
        }

        if (sellingType == null) {
            flash("editCategoryEmptySellingTypeyError", "Please select selling type.");
            return badRequest(editProduct.render(product));
        }

        product.name = name;
        product.description = description;
        product.manufacturer = manufacturer;
        product.price = Double.parseDouble(price);
        product.quantity = Integer.parseInt(quantity);
        product.sellingType = sellingType;

        product.update();

        return redirect(routes.ProductController.getProduct(product.id));
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

        Logger.info(term);
        List<Product> products = Product.searchProductByName(term);

        List<String> names = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
            names.add(products.get(i).name);
        }

        JsonNode object = Json.toJson(names);

        return ok(object);
    }

    /**
     * This will just validate the form for the AJAX call
     *
     * @return ok if there are no errors or a JSON object representing the errors
     */
    public Result validateFormProduct() {
        Form<Product> binded = productForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

}
