package controllers.rest;

import models.Product;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;


/**
 * Created by Kerim on 28.10.2015.
 */
public class ApiProductController extends Controller {

    public Result getProductsList() {
        List<Product> products = Product.findAll();
        return ok(Json.toJson(products));
    }

    public Result getProductById(Integer id) {
        Product product = Product.getProductById(id);
        return ok(Json.toJson(product));
    }
}

