package controllers;

import com.avaje.ebean.Ebean;
import helpers.CurrentSeller;
import models.*;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.product.newProduct;
import views.html.product.editProduct;
import views.html.product.productProfile;
import views.html.user.userProducts;
import java.io.File;
import java.io.IOException;
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
        return redirect(routes.Users.index());
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
        String sellingType = boundForm.bindFromRequest().field("selling-type").value();

        product.name = name;
        product.description = description;
        product.manufacturer = manufacturer;
        product.price = Double.parseDouble(price);
        product.quantity = Integer.parseInt(quantity);
        product.sellingType = sellingType;

        Ebean.update(product);

        return redirect(routes.ProductController.getProduct(product.id));
    }

}
