package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by neo on 9/17/15.
 */
@Entity
public class CartItem extends Model {

    @Id
    public Integer id;
    @ManyToOne
    public User user;
    @ManyToOne
    public Product product;
    @ManyToOne
    public Cart cart;
    public Integer quantity;
    public Double price;

    public CartItem(){
    }

    public CartItem(Product product, User user, Cart cart){
        this.product = product;
        this.user = user;
        this.cart = cart;
        this.quantity = 1;
        this.price = product.price*quantity;
    }


    private static Finder<String, CartItem> finder = new Finder<String, CartItem>(CartItem.class);

    /**
     * This method is used to find all items that user has placed to its cart
     * @return - List of items
     */
    public static List<CartItem> findAll() {
        List<CartItem> cartItems = finder.all();
        return cartItems;
    }

    /**
     * Method for finding a single item in Cart
     * @param id - Item ID
     * @return - Item by ID
     */
    public static CartItem getCartItemById(Integer id) {
        CartItem cartItem = CartItem.finder.where().eq("id", id).findUnique();
        return cartItem;
    }

    /**
     *This method puts a product in cart as car item
     * @param product
     * @return
     */
    public static CartItem getCartItemByProductAndUser(Product product, User user) {
        CartItem cartItem = CartItem.finder.where().eq("product", product).where().eq("user", user).findUnique();
        return cartItem;
    }

}
