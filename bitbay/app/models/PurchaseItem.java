package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by Medina Banjic on 13/10/15.
 */
@Entity
public class PurchaseItem extends Model {

    @Id
    public Integer id;

    @ManyToOne
    public User user;

    @ManyToOne
    public Product product;

    @ManyToOne
    public Purchase purchase;
    @ManyToOne
    public Cart cart;
    public Integer quantity;
    public Double price;

    public PurchaseItem(){}

    public PurchaseItem(Product product, User user, Cart cart, Purchase purchase, Integer quantity){
        this.product = product;
        this.purchase = purchase;
        this.user = user;
        this.cart = cart;
        this.quantity = quantity;
        this.price = product.price*quantity;
    }

    private static Finder<String, PurchaseItem> finder = new Finder<String, PurchaseItem>(PurchaseItem.class);

    /**
     * This method is used to find all purchases
     * @return - List of purchases
     */
    public static List<PurchaseItem> findAll() {
        List<PurchaseItem> purchaseItems = finder.all();
        return purchaseItems;
    }

    /**
     * Method for finding a single purchase
     * @param id - PurchaseItem ID
     * @return - PurchaseItem by ID
     */
    public static PurchaseItem getPurchaseById(Integer id) {
        PurchaseItem purchaseItem = PurchaseItem.finder.where().eq("id", id).findUnique();
        return purchaseItem;
    }

}
