package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;

import javax.persistence.*;
import java.util.List;


/**
 * Created by neo on 9/15/15.
 */
@Entity
public class Cart extends Model{

    @Id
    public Integer id;

    @OneToOne
    public User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    public List<CartItem> cartItems;

    private static Finder<String, Cart> finder = new Finder<String, Cart>(Cart.class);

    public Cart(){
    }

    public Cart (User user, List<CartItem> cartItems){
        this.user = user;
        this.cartItems = cartItems;
    }


    public static Cart findCartByUser(User user){
        Cart cart = Cart.finder.where().eq("user", user).findUnique();

        return cart;
    }

    public static Double cartAmount(User user) {
        Cart cart = Cart.findCartByUser(user);
        Double amount = 0.0;

        for(CartItem c: cart.cartItems) {
            amount += c.price;
        }
        return amount;
    }

    public static Boolean doesContainItem(Integer id) {
        User user = SessionHelper.currentUser();
        Cart cart = Cart.findCartByUser(user);

        if (user != null && user.userType.id == UserType.BUYER)
            try {
                List<CartItem> items = cart.cartItems;

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).product.id == id) {
                        return true;
                    }
                }
            } catch(NullPointerException e) {
        }
     return false;
    }
}
