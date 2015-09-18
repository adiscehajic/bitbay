package controllers;

import helpers.CurrentBuyer;
import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import models.CartItem;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.userCart;

import java.util.ArrayList;

/**
 * Created by neo on 9/15/15.
 */

@Security.Authenticated(CurrentBuyer.class)
public class CartController extends Controller {

    private static Form<CartItem> itemForm = Form.form(CartItem.class);

    public Result addToCart(Integer productId){

        Product product = Product.getProductById(productId);
        CartItem cartItem = CartItem.getCartItemByProduct(product);

        if (cartItem == null) {
            cartItem = new CartItem(product);
        }

        User user = User.getUserByEmail(session().get("email"));

        Cart cart = Cart.findCartByUser(user);

        if(cart != null && cart.cartItems.size() > 0) {

            for (int i = 0; i < cart.cartItems.size(); i++){
                CartItem item = cart.cartItems.get(i);
                Logger.info("ID item: " + item.id + "Item id: " + cartItem.id);
                if(item.id == cartItem.id){
                    if(item.quantity < item.product.quantity) {
                        item.quantity = item.quantity + 1;
                        item.price = item.product.price * item.quantity;
                        item.update();
                        cart.update();
                    }
                    return redirect(routes.CartController.getCart());
                }
            }
            cart.cartItems.add(cartItem);
            cart.update();
        } else if(cart != null && cart.cartItems.size() == 0)  {
            cart.cartItems.add(cartItem);
            cart.update();
        } else {
            cartItem.save();
            cart = new Cart();
            cart.user = user;
            cart.cartItems = new ArrayList<>();
            cart.cartItems.add(cartItem);
            cart.save();
        }
        return redirect(routes.CartController.getCart());
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
        cartItem.delete();
        return ok(userCart.render(cart.cartItems, user));
    }

    public Result updateItemQuantity(Integer id) {
        Form<CartItem> boundForm = itemForm.bindFromRequest();
        String quantity = boundForm.field("quantity").value();

        CartItem cartItem = CartItem.getCartItemById(id);
        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);

        cartItem.quantity = Integer.parseInt(quantity);
        cartItem.price = cartItem.product.price * cartItem.quantity;
        cartItem.save();

        return redirect(routes.CartController.getCart());
    }

}
