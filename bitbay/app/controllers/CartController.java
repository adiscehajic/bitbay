package controllers;

import helpers.CurrentBuyer;
import helpers.SessionHelper;
import models.*;
import org.h2.engine.Session;
import play.Logger;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import models.CartItem;
import play.mvc.Result;
import play.mvc.Security;
import views.html.user.userCart;

import java.util.ArrayList;

/**
 * Created by Dinko Hodzic on 9/15/15.
 */
@Security.Authenticated(CurrentBuyer.class)
public class CartController extends Controller {

    // Declaring cart item form.
    private static Form<CartItem> itemForm = Form.form(CartItem.class);

    /**
     * Inputs new product into user cart. If the product already exists in the cart it increases the number of the
     * products for the selected value. One product can be in many carts, depending on the number of the products in
     * the database.
     *
     * @param productId - Id of the product that is inputed into the cart.
     * @return Page that represents the cart with all products of the current user.
     */
    @RequireCSRFCheck
    public Result addToCart(Integer productId){
        // Finding current user.
        User user = SessionHelper.currentUser();
        // Finding the cart of the current user.
        Cart cart = Cart.findCartByUser(user);
        // Declaring product and cart item.
        Product product = Product.getProductById(productId);
        CartItem cartItem = CartItem.getCartItemByProductAndUser(product, user);
        // Checking if the cart item is already in the cart and if not creating new cart item.
        if (cartItem == null) {
            cartItem = new CartItem(product, user, cart);
        }
        // Checking if the cart of current user exists and if there is cart items in the cart.
        if(cart != null && cart.cartItems.size() > 0) {
            // If the user clicks again on the button ADD TO CART increasing amount of product in the cart for one.
            for (int i = 0; i < cart.cartItems.size(); i++){
                CartItem item = cart.cartItems.get(i);
                if(item.id == cartItem.id){
                    if(item.quantity < item.product.quantity) {
                        item.quantity = item.quantity + 1;
                        item.price = item.product.price * item.quantity;
                        // Updating the item and cart.
                        item.update();
                        cart.update();
                    }
                    return redirect(routes.CartController.getCart());
                }
            }
            // Adding the cart item to the cart and updating the cart.
            cart.cartItems.add(cartItem);
            cart.update();
            // Checking if the cart of current user exists and if there is no cart item in the cart.
        } else if(cart != null && cart.cartItems.size() == 0)  {
            // Adding cart item to the cart and updating cart.
            cart.cartItems.add(cartItem);
            cart.update();
        } else {
            // If the current user does not have cart, saving the cart item into database, creating new cart of current
            // user and adding the cart item into the cart.
            cart = new Cart();
            cart.user = user;
            cart.cartItems = new ArrayList<>();
            cart.cartItems.add(cartItem);
            // Saving the cart into database.
            cart.save();
            cartItem.cart = cart;
            cartItem.save();
        }
        return redirect(routes.CartController.getCart());
    }

    /**
     * Renders the page that represent cart of current user. If the current user has already added cart items into cart
     * it lists all cart items that are added. In the cart user can increase or decrease the amount of cart items that
     * he wants to buy. Also user can see the amount that he needs to pay for cart items into the cart.
     *
     * @return Page that contains all cart items that current user has added into the cart.
     */
    public Result getCart(){
        User user = SessionHelper.currentUser();
        Cart cart = Cart.findCartByUser(user);

        if(cart != null) {
            return ok(userCart.render(cart.cartItems, user));
        } else {
            return ok(userCart.render(null, user));
        }
    }

    /**
     * Removes the cart item from the cart.
     *
     * @param itemId - Id of the cart item that user wants to delete.
     * @return Page that contains all cart items that current user has added into the cart.
     */
    public Result removeFromCart(Integer itemId){
        // Declaring cart item.
        CartItem cartItem = CartItem.getCartItemById(itemId);

        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);
        // Removing foung cart item from the list of cart items and updating the cart.
        cart.cartItems.remove(cartItem);
        //cart.update();
        // Deleting the cart item from the database.
        cartItem.delete();
        return ok(userCart.render(cart.cartItems, user));
    }

    /**
     * Enables user to increase or decrease the amount of the cart items that he wants to buy. The minimum amount of
     * cart item is one and the maximum depends on the status of the cart item in the database.
     *
     * @param id - Id of the cart item which amount is changed.
     * @return Page that contains all cart items that current user has added into the cart.
     */
    @RequireCSRFCheck
    public Result updateItemQuantity(Integer id) {
        // Declaring cart item form.
        Form<CartItem> boundForm = itemForm.bindFromRequest();
        // Getting inputed quantity value.
        String quantity = boundForm.field("quantity").value();
        // Declaring cart item.
        CartItem cartItem = CartItem.getCartItemById(id);
        User user = User.getUserByEmail(session().get("email"));
        Cart cart = Cart.findCartByUser(user);
        // Calculating the new price of the cart items in the cart ad saving them into database.
        cartItem.quantity = Integer.parseInt(quantity);
        cartItem.price = cartItem.product.price * cartItem.quantity;
        cartItem.save();

        return redirect(routes.CartController.getCart());
    }

}
