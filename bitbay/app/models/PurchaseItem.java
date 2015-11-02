package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.*;

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

    public int isRefunded;

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
        isRefunded = 0;
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
    public static PurchaseItem getPurchaseItemById(Integer id) {
        PurchaseItem purchaseItem = PurchaseItem.finder.where().eq("id", id).findUnique();
        return purchaseItem;
    }

    /**
     * Returns the list of all purchases of inputed user. If user has purchases that are canceled due internet or other
     * error, deletes them and returns all completed purchases.
     *
     * @param user
     * @return
     */
    public static List<PurchaseItem> getPurchasedItemsByUser(User user){
        List<PurchaseItem> purchaseItems = finder.where().eq("user", user).findList();

        Set<Purchase> purchasesSet = new HashSet<>();

        for (int i = 0; i < purchaseItems.size(); i++) {
            purchasesSet.add(purchaseItems.get(i).purchase);
        }

        Iterator<Purchase> iter = purchasesSet.iterator();

        while (iter.hasNext()) {
            Purchase p = iter.next();
            if (p.payment_id == null) {
                p.delete();
            }
        }
        return PurchaseItem.finder.where().eq("user", user).findList();
    }

    public static Boolean hasPurchesedProduct(Product product){
        return (finder.where().eq("product", product).where().eq("user", SessionHelper.currentUser()).where().eq("isRefunded", 0).findList().size() > 0) ? true : false;
    }
}
