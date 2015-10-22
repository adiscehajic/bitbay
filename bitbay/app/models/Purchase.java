package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Medina Banjic on 14/10/15.
 */
@Entity
public class Purchase extends Model {

    @Id
    public Integer id;

    public String payment_id;
    public String bitPayment_id;
    public String sale_id;
    public double totalPrice;
    public String token; //token from paypalReturn

    @ManyToOne
    public User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    public List<PurchaseItem> purchaseItems;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date purchaseDate = new Date();

    public int isRefunded;

    private static Finder<String, Purchase> finder = new Finder<String, Purchase>(Purchase.class);

    public Purchase(){
    }

    /**
     * Constructor for new purchase.
     *
     * @param buyer        - current user
     * @param purchaseItem - list of purchased items
     */
    public Purchase(User user, List<PurchaseItem> purchaseItems, String payment_id, String sale_id, double totalPrice, String token) {
        this.user = user;
        this.purchaseItems = purchaseItems;
        this.payment_id = payment_id;
        this.sale_id = sale_id;
        this.bitPayment_id = "bit" + UUID.randomUUID().toString().substring(0, 7);
        this.totalPrice = totalPrice;
        this.token = token;
        this.isRefunded = 0;
    }


    /**
     * This method saves data to tables purchaseItem and purchase, and also deletes cartItems from cart of the current user
     *
     * @param payment_id String
     * @param token      String
     * @param buyer      User
     * @return id of the created purchase ...
     */
    public static Purchase createPurchase(String payment_id, String saleId, double totalPrice,
                                          String token, User buyer, List<PurchaseItem> purchaseItems, List<CartItem> cartItems, Purchase purchase) {

        Product product;
        purchase.user = buyer;
        purchase.purchaseItems = purchaseItems;
        purchase.payment_id = payment_id;
        purchase.sale_id = saleId;
        purchase.totalPrice = totalPrice;
        purchase.token = token;
        purchase.isRefunded = 0;
        purchase.save();
        PurchaseItem item;

        for (int i = 0; i < purchaseItems.size(); i++) {
            item = purchaseItems.get(i);
            product = Product.getProductById(item.product.id);
            item.purchase = purchase;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(purchase.purchaseDate);
            calendar.add(Calendar.HOUR, product.cancelation);
            item.cancelationDueDate = calendar.getTime();
            item.save();

            product.quantity -= item.quantity;
            product.update();
        }

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItemI = cartItems.get(i);
            cartItemI.delete();
        }

        return purchase;

    }



    /**
     * Finds purchase by the given id
     * @param user
     * @return
     */
    public static Purchase findPurchaseByUser(User user){
        Purchase purchase = Purchase.finder.where().eq("user", user).findUnique();

        return purchase;
    }

    /**
     * Calculates whole purchase price amount
     * @param user - buyer
     * @return amount
     */
    public static Double purchaseAmount(User user) {
        Purchase purchase = Purchase.findPurchaseByUser(user);
        Double amount = 0.0;

        for(PurchaseItem p: purchase.purchaseItems) {
            amount += p.price;
        }
        return amount;
    }

    /**
     * Finds if there is an item with the given id in a purchaseItem list of a current user
     * @param id - item id
     * @return boolean
     */
    public static Boolean doesContainItem(Integer id) {
        User user = SessionHelper.currentUser();
        Purchase purchase = Purchase.findPurchaseByUser(user);

        if (user != null && user.userType.id == UserType.BUYER)
            try {
                List<PurchaseItem> items = purchase.purchaseItems;

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

