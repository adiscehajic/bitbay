package controllers;

import helpers.SessionHelper;
import play.Logger;
import play.mvc.Controller;
import com.avaje.ebean.Ebean;
import models.Cart;
import models.Product;
import models.User;
import play.mvc.Result;
import views.html.user.userCart;

import java.util.List;

/**
 * Created by neo on 9/15/15.
 */
public class CartController extends Controller {


    public Result addToCart(Integer productId){
        Product product = Product.getProductById(productId);


        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        if(cart != null) {
            cart.products.add(product);
            Ebean.update(cart);
        } else {
            cart = new Cart();
            cart.user = user;
            cart.products.add(product);
            Ebean.save(cart);
        }
        return redirect(routes.Users.index());
    }

    public Result getCart(){
        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        return ok(userCart.render(cart.products, user));
    }



    public Result removeFromCart(Integer productId){
        Product product = Product.getProductById(productId);


        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        cart.products.remove(product);
        Ebean.update(cart);
        return ok(userCart.render(cart.products, user));
    }


}
