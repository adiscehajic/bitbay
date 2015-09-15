package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;
import javax.persistence.Entity;
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

    @ManyToMany
    public List<Product> products;

    private static Finder<String, Cart> finder = new Finder<String, Cart>(Cart.class);

    public Cart(){

    }

    public Cart (User user, List<Product> products){
        this.user = user;
        this.products = products;
    }


    public static Cart findCartByUser(User user){
        Cart cart = Cart.finder.where().eq("user", user).findUnique();

        return cart;
    }

}
