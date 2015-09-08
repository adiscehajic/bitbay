package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.product.product;

/**
 * Created by adis.cehajic on 08/09/15.
 */
public class ProductController extends Controller {

    public Result getProduct(Integer id) {
        return ok(product.render(id));
    }

    public Result newProduct() {
        return TODO;
    }

    public Result deleteProduct(Integer id) {
        return TODO;
    }

}
