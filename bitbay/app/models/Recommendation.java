package models;

import com.avaje.ebean.Model;
import helpers.SessionHelper;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adis Cehajic on 10/7/2015.
 */
@Entity
public class Recommendation extends Model {

    // Declaring recommendation properties.
    @Id
    public Integer id;
    @ManyToOne
    public User user;
    @ManyToOne
    public Category category;
    public Integer count;
    // Declaring finder.
    public static Finder<String, Recommendation> finder = new Finder<>(Recommendation.class);

    /**
     * Constructor that creates new recommendation which has count set on one.
     *
     * @param user - Current user that had viewed product.
     * @param category - Category of viewed product.
     */
    public Recommendation(User user, Category category) {
        this.user = user;
        this.category = category;
        this.count = 1;
    }

    /**
     * Goes trough table recommendation in database and finds recommendation with inputed user and category. If
     * recommendation does not exist returns null.
     *
     * @param user - Inputed user.
     * @param category - Inputed category.
     * @return If recommendation exist returns recommendation, otherwise returns null.
     */
    public static Recommendation getRecommendationByUserAndCategory(User user, Category category) {
        return finder.where().eq("user", user).where().eq("category", category).findUnique();
    }

    /**
     * Returns the category whose products users had most viewed.
     *
     * @return Category that is most viewed.
     */
    public static Category getTopCategory() {
        // Finding the list of all categories.
        List<Category> categories = Category.findAll();
        int max = 0;
        Category topCategory = null;
        // Going trough all categories and checking how many times have users clicked on its products.
        for (int i = 0; i < categories.size(); i++) {
            int count = finder.where().eq("category", categories.get(i)).findRowCount();
            // Checking if the number of clicks is higher than max.
            if (count > max) {
                max = count;
                topCategory = categories.get(i);
            }
        }
        // Returning top category.
        return topCategory;
    }

    /**
     * Returns all recommendations from categories which products current user had viewed and ordering them by number
     * of viewes.
     *
     * @param user - Current user.
     * @return All recommendations from categories which products current user had viewed.
     */
    public static List<Recommendation> getTopCategoriesByUser(User user) {
        return finder.where().eq("user", user).orderBy().desc("count").findList();
    }

    /**
     * Recommends 4 (four) products on main application page depending on current user interest. If the user is not
     * logged in it recommends 4 (four) products from the top category.
     *
     * @return The list that contains 4 (four) recommended products.
     */
    public static List<Product> getRecommendations() {
        // Finding current user from the session.
        User user = SessionHelper.currentUser();
        // Declaring the list of products.
        List<Product> recommendedProducts;
        // Checking if user is logged in.
        if (user != null) {
            // Declaring the list of recommendations and finding top recommended categories.
            List<Recommendation> recommendations = Recommendation.getTopCategoriesByUser(user);
            // Checking if recommended list had any recommendations for current user.
            if (recommendations.size() != 0) {
                // Checking if current has only one category that is recommended.
                if (recommendations.size() == 1) {
                    // Declaring the list of products from recommended category.
                    List<Product> categoryProducts = Product.findAllProductsByCategory(recommendations.get(0).category);
                    // Adding 4 (four) random products to the list of the recommended products.
                    recommendedProducts = Product.getFourRandomProducts(categoryProducts);
                    // Checking if current has two categories that are recommended.
                } else if (recommendations.size() == 2) {
                    // Declaring the lists of products from recommended categories.
                    List<Product> categoryOneProducts = Product.findAllProductsByCategory(recommendations.get(0).category);
                    List<Product> categoryTwoProducts = Product.findAllProductsByCategory(recommendations.get(1).category);
                    recommendedProducts = new ArrayList<>();
                    // Adding 2 (two) random products from each category to the list of the recommended products.
                    recommendedProducts.addAll(Product.getTwoRandomProducts(categoryOneProducts));
                    recommendedProducts.addAll(Product.getTwoRandomProducts(categoryTwoProducts));
                } else {
                    // Declaring the lists of products from recommended categories.
                    List<Product> categoryOneProducts = Product.findAllProductsByCategory(recommendations.get(0).category);
                    List<Product> categoryTwoProducts = Product.findAllProductsByCategory(recommendations.get(1).category);
                    List<Product> categoryThreeProducts = Product.findAllProductsByCategory(recommendations.get(2).category);
                    // Adding 2 (two) random products from the top category and 1 (one) from the two remaining
                    // categories to the list of the recommended products.
                    recommendedProducts = new ArrayList<>();
                    recommendedProducts.addAll(Product.getTwoRandomProducts(categoryOneProducts));
                    recommendedProducts.add(Product.getOneRandomProduct(categoryTwoProducts));
                    recommendedProducts.add(Product.getOneRandomProduct(categoryThreeProducts));
                }
                // If current user has not viewed any products recommends 4 (four) products from top category.
            } else {
                // Finding the top category.
                Category category = Recommendation.getTopCategory();
                // Finding all products from selected category.
                List<Product> categoryProducts = Product.findAllProductsByCategory(category);
                // Finding the 4 (four) random from selected category.
                recommendedProducts = Product.getFourRandomProducts(categoryProducts);
            }
        } else {
            // Finding the top category.
            Category category = Recommendation.getTopCategory();
            // Finding all products from selected category.
            List<Product> categoryProducts = Product.findAllProductsByCategory(category);
            // Finding the 4 (four) random from selected category.
            recommendedProducts = Product.getFourRandomProducts(categoryProducts);
        }
        // Returning the recommended products.
        return recommendedProducts;
    }
}