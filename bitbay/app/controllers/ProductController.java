package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import helpers.CurrentSeller;
import models.*;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.viewProductsByCategory;
import views.html.product.newProduct;
import views.html.product.editProduct;
import views.html.product.productProfile;
import views.html.product.searchProduct;
import views.html.user.userProducts;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adis.cehajic on 08/09/15.
 */
public class ProductController extends Controller {

    private static Form<Product> productForm = Form.form(Product.class);

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

    @Security.Authenticated(CurrentSeller.class)
    public Result newProduct() {
        List<Category> categories = Category.findAll();
        return ok(newProduct.render(categories));
    }

    @Security.Authenticated(CurrentSeller.class)
    public Result deleteProduct(Integer id) {
        Product product = Product.getProductById(id);

        if (User.getUserByEmail(session("email")).id == product.user.id) {
            Ebean.delete(product);
        }

        User user = User.getUserByEmail(session().get("email"));
        List<Product> products = Product.findAllProductsByUser(user.email);
        return ok(userProducts.render(products, user));
    }

    public Result deleteProductAdmin(Integer id) {
        Product product = Product.getProductById(id);

        Ebean.delete(product);

        return redirect(routes.AdminController.adminProducts());
    }

    @Security.Authenticated(CurrentSeller.class)
    public Result saveProduct() {
        Form<Product> boundForm = productForm.bindFromRequest();

        User user = User.getUserByEmail(session().get("email"));
        String name = boundForm.bindFromRequest().field("name").value();
        String description = boundForm.bindFromRequest().field("description").value();
        String manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
        String categoryValue = boundForm.bindFromRequest().field("category").value();
        String price = boundForm.bindFromRequest().field("price").value();
        String quantity = boundForm.bindFromRequest().field("quantity").value();
        String sellingType = boundForm.bindFromRequest().field("type").value();

        Category category = Category.getCategoryByName(categoryValue);
        List<Category> categories = Category.findAll();

        if (name.isEmpty()) {
            flash("saveProductNameError", "Please enter product name.");
            return badRequest(newProduct.render(categories));
        }

            if(!manufacturer.matches("^[a-z A-Z]*$")){
                flash("saveProductManufacturerError","Manufacturer must contain only letters.");
                return badRequest(newProduct.render(categories));
            }

        if (manufacturer.isEmpty()){
            flash("saveProductManufacturerEmptyError", "Please enter manufacturer");
            return badRequest(newProduct.render(categories));
        }

       if(Integer.parseInt(price)<=0){
           flash("saveCategoryLowPriceError", "Price can't be 0 or lower.");
           return badRequest(newProduct.render(categories));

       }

        if(price.isEmpty()){
            flash("saveProductEmptyPriceError", "Please enter price.");
            return badRequest(newProduct.render(categories));
        }

        if(quantity.isEmpty()){
            flash("saveProductEmptyQuantityError", "Please enter product quantity.");
            return badRequest(newProduct.render(categories));
        }

        if(categoryValue == null){
            flash("saveCategoryEmptyCategoryError", "Please select category.");
            return badRequest(newProduct.render(categories));
        }

        if(sellingType == null){
            flash("saveCategoryEmptySellingTypeyError", "Please select selling type.");
            return badRequest(newProduct.render(categories));
        }

        Product product = new Product(user, name, description, manufacturer, category, Double.parseDouble(price), Integer.parseInt(quantity), sellingType);

        Ebean.save(product);

        MultipartFormData body = request().body().asMultipartFormData();
        List<FilePart> pictures = body.getFiles();

        if (pictures != null) {
            for (FilePart picture : pictures) {
                String fileName = picture.getFilename();
                File file = picture.getFile();
                try {
                    FileUtils.moveFile(file, new File(Play.application().path() + "/public/images/products/" + fileName));
                    Image image = new Image(fileName, product);
                    Ebean.save(image);
                } catch (IOException e) {
                    Logger.info(e.getMessage());
                }
            }
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

        for(char ch:manufacturer.toCharArray()){
            if(Character.isDigit(ch)){
                flash("editProductManufacturerError","Manufacturer must contain only letters.");
                return badRequest(editProduct.render(product));
            }
        }

        if (manufacturer.isEmpty()){
            flash("editProductManufacturerEmptyError", "Please enter manufacturer");
            return badRequest(editProduct.render(product));
        }

        if(Double.parseDouble(price)<=0){
            flash("editCategoryLowPriceError", "Price can't be 0 or lower.");
            return badRequest(editProduct.render(product));

        }

        if(price.isEmpty()){
            flash("editProductEmptyPriceError", "Please enter price.");
            return badRequest(editProduct.render(product));
        }

        if(Integer.parseInt(quantity)<0){
            flash("editProductEmptyQuantityError", "Quantity can't be 0 or lower.");
            return badRequest(editProduct.render(product));
        }

        if(sellingType == null){
            flash("editCategoryEmptySellingTypeyError", "Please select selling type.");
            return badRequest(editProduct.render(product));
        }

        product.name = name;
        product.description = description;
        product.manufacturer = manufacturer;
        product.price = Double.parseDouble(price);
        product.quantity = Integer.parseInt(quantity);
        product.sellingType = sellingType;

        Ebean.update(product);

        return redirect(routes.ProductController.getProduct(product.id));
    }

    public Result viewProductsByCategory(Integer id) {
        Category category = Category.getCategoryById(id);
        List<Product> products = Product.findAllProductsByCategory(category);

        return ok(viewProductsByCategory.render(products, category));
    }

    public Result searchProduct(){

        Form<Product> boundForm = productForm.bindFromRequest();
        String name = boundForm.bindFromRequest().field("search").value();

        List<Product> products = Product.searchProductByName(name);

        Logger.info(products.get(0).name);

        return ok(searchProduct.render(products));
    }

    public Result autocompleteSearch() {
        Form<Product> boundForm = productForm.bindFromRequest();

        String name = boundForm.bindFromRequest().field("search").value();

        Logger.info(name);
        List<Product> products = Product.searchProductByName(name);

        List<String> names = new ArrayList<>();

        for (int i = 0; i < products.size(); i++) {
           names.add(products.get(i).name);
        }

        JsonNode object = Json.toJson(names);

        return ok(object);
    }

}
