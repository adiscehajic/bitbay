package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Medina Banjic on 14/10/15.
 */
@Entity
public class Purchase extends Model {

    @Id
    public Integer id;

    public String payment_id;

    @ManyToOne
    public User user;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    public List<PurchaseItem> purchaseItems;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date purchaseDate = new Date();

    private static Finder<String, Purchase> finder = new Finder<String, Purchase>(Purchase.class);

    public Purchase(){
    }

    /**
     * Constructor for new purchase.
     * @param buyer - current user
     * @param purchaseItem - list of purchased items

     */
    public Purchase (User user, List<PurchaseItem> purchaseItems){
        this.user = user;
        this.purchaseItems = purchaseItems;
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

