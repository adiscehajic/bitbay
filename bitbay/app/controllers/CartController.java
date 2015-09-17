package controllers;

import helpers.SessionHelper;
import models.*;
import play.Logger;
import play.mvc.Controller;
import com.avaje.ebean.Ebean;
import models.CartItem;
import play.mvc.Result;
import views.html.user.userCart;

import java.util.List;

/**
 * Created by neo on 9/15/15.
 */
public class CartController extends Controller {


    public Result addToCart(Integer productId){
        Product product = Product.getProductById(productId);
        CartItem cartItem = new CartItem(product);


        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        if(cart != null) {
            for (CartItem item : cart.cartItems){
                if(item.id == cartItem.id){
                    cartItem.quantity++;
                }else{
                    cart.cartItems.add(cartItem);
                }
            }

            cart.update();
        } else {
            cart = new Cart();
            cart.user = user;
            cart.cartItems.add(cartItem);
            cart.save();
        }
        return redirect(routes.Users.index());
    }

    public Result getCart(){
        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        if(cart != null) {
            return ok(userCart.render(cart.cartItems, user));
        } else {
            return ok(userCart.render(null, user));
        }

    }



    public Result removeFromCart(Integer itemId){
        CartItem cartItem = CartItem.getCartItemById(itemId);


        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        cart.cartItems.remove(cartItem);
        cart.update();
        return ok(userCart.render(cart.cartItems, user));
    }


}
