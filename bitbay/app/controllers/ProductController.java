package controllers;

import com.avaje.ebean.Ebean;
import models.Category;
import models.Product;
import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.product.product;
import views.html.product.newProduct;

import java.util.List;

/**
 * Created by adis.cehajic on 08/09/15.
 */
public class ProductController extends Controller {

    private static Form<Product> productForm = Form.form(Product.class);

    public Result getProduct(Integer id) {
        return ok(product.render(id));
    }

    public Result newProduct() {
        List<Category> categories = Category.findAll();
        return ok(newProduct.render(categories));
    }

    public Result deleteProduct(Integer id) {
        return TODO;
    }


    public Result saveProduct() {
        Form<Product> boundForm = productForm.bindFromRequest();

        User user = User.getUserByEmail(session().get("email"));
        String name = boundForm.bindFromRequest().field("name").value();
        String description = boundForm.bindFromRequest().field("description").value();
        String manufacturer = boundForm.bindFromRequest().field("manufacturer").value();
        String categoryValue = boundForm.bindFromRequest().field("category").value();
        String price = boundForm.bindFromRequest().field("price").value();
        String quantity = boundForm.bindFromRequest().field("quantity").value();
        String sellingType = boundForm.bindFromRequest().field("selling-type").value();
        Logger.info(categoryValue);

        Category category = Category.getCategoryByName(categoryValue);

        Product product = new Product(user, name, description, manufacturer, category, Double.parseDouble(price), Integer.parseInt(quantity), sellingType);

        Ebean.save(product);

        return ok(index.render("Hello"));

    }

}
