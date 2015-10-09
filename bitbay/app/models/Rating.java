package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Adnan on 08.10.2015..
 */
@Entity
public class Rating extends Model {
    @Id
    public Integer id;
    @ManyToOne
    public User user;
    @ManyToOne
    public Product product;
    public Integer rate;

    private static Finder<String, Rating> finder = new Finder<>(Rating.class);

    /**
     * Empty constructor for Rating
     */
    public Rating(){}

    /**
     * Constructor for Rating
     * @param user - User who rated product
     * @param product - Rated product
     * @param rate - Rate
     */
    public Rating (User user, Product product, Integer rate){
        this.user = user;
        this.product = product;
        this.rate = rate;
    }

    /**
     * This method checks if user had already rated given product
     * @param product - Rated product
     * @return
     */
    public static Boolean hasRated(Product product){
        User user = SessionHelper.currentUser();
        Rating rating = finder.where().eq("user", user).where().eq("product", product).findUnique();

        if(rating == null) {
            return false;
        }
        return true;
    }

    /**
     * This method returns rating that user from session left on given product
     * @param product - Rated product
     * @return
     */
    public static Rating getRating(Product product) {
        User user = SessionHelper.currentUser();

        return finder.where().eq("product", product).where().eq("user", user).findUnique();
    }

    /**
     * Method gets all rates on this product and calculates average rating and returns decimal number
     * with one diggit behind point after its converted to string
     * @param id - Product ID
     * @return - Average rating as string
     */
    public static String getAverageRating(Integer id) {
        Product product = Product.getProductById(id);
        List<Rating> ratings = finder.where().eq("product", product).findList();
        int rating = 0;
        double average = 0.0;

        if (ratings != null && ratings.size() > 0) {
            for (int i = 0; i < ratings.size(); i++) {
                rating = rating + ratings.get(i).rate;
            }
            average = (double)rating / ratings.size();

            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(average);
            //return average + "";
        } else {
            return "0.0";
        }
    }
}
