package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
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

    public Integer quantity;
    public Double price;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date cancelationDueDate;

    public PurchaseItem(){}

    /**
     * Constructor for a new purchaseItem object
     * @param product
     * @param user
     * @param purchase
     * @param quantity
     */
    public PurchaseItem(Product product, User user, Purchase purchase, Integer quantity){
        this.product = product;
        this.purchase = purchase;
        this.user = user;
        this.quantity = quantity;
        this.price = product.price*quantity;
    }

    private static Finder<String, PurchaseItem> finder = new Finder<String, PurchaseItem>(PurchaseItem.class);

    /**
     * This method is used to find all purchaseItem objects
     * @return - List of purchaseItem
     */
    public static List<PurchaseItem> findAll() {
        List<PurchaseItem> purchaseItems = finder.all();
        return purchaseItems;
    }

    /**
     * Method for finding a single purchaseItem
     * @param id - PurchaseItem ID
     * @return - PurchaseItem by ID
     */
    public static PurchaseItem getPurchaseById(Integer id) {
        PurchaseItem purchaseItem = PurchaseItem.finder.where().eq("id", id).findUnique();
        return purchaseItem;
    }

    public static List<PurchaseItem> getPurchasedItemsByUser(User user){
        return PurchaseItem.finder.where().eq("user", user).findList();
    }

}
