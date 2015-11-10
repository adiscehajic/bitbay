package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonBackReference;
import helpers.SessionHelper;
import play.Logger;
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
    @JsonBackReference
    public User user;

    @ManyToOne
    @JsonBackReference
    public Product product;

    @ManyToOne
    @JsonBackReference
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

    /**
     * This method is checking if product was bought by curent user
     */
    public static Boolean hasPurchesedProduct(Product product){
        return (finder.where().eq("product", product).where().eq("user", SessionHelper.currentUser()).where().eq("isRefunded", 0).findList().size() > 0) ? true : false;
    }

    /**
     * Finds the top 10 products that are most purchased in the last 7 days. It goes through purchase_item table and
     * calculates how many times is the each product purchased and sorts the count of product purchases.
     *
     * Top 10 products are shown on slider that is on main page od the application. The products that are shown on the
     * slider are every day different and they depend on user product purchase.
     *
     * @return The list of top 10 most purchased products in the last 7 days.
     */
    public static List<Product> getMostSellingPurchaseItems() {
        // Declaring the list that will contain the top 10 products.
        List<Product> mostPurchased = new ArrayList<>();
        // Declaring the sql query that will find top 10 products.
        String sql = "SELECT product_id FROM purchase_item INNER JOIN purchase ON purchase_item.purchase_id = " +
                "purchase.id WHERE purchase.purchase_date > :purchaseDate GROUP BY product_id ORDER BY " +
                "COUNT(product_id) DESC LIMIT 10;";

        // Declaring variable that represent the current date.
        Date purchaseDate = new Date();
        // Setting the date variable to date 7 days before current date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(purchaseDate);
        calendar.add(Calendar.DATE, -7);
        purchaseDate = calendar.getTime();
        // Declaring the list that contain the SqlRows that sql query has returned.
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).setParameter("purchaseDate", purchaseDate).findList();
        // Going trough every SqlRow and adding returned product to the top 10 product list.
        for (SqlRow sqlRow : sqlRows) {
            Product product  = Product.getProductById(sqlRow.getInteger("product_id"));
            mostPurchased.add(product);
        }
        // Returning the list of the top 10 most purchased products.
        return mostPurchased;
    }
}