package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonBackReference;
import helpers.SessionHelper;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adis Cehajic on 10/7/2015.
 *
 * This class contains the methods that represent product recommendation algorithm.
 *
 * A traditional collaborative filtering algorithm represents a customer as an N-dimensional vector of items, where
 * N is the number of distinct catalog items. The algorithm generates recommendations based on a few customers who
 * are most similar to the user.
 *
 * Rather than matching the user to similar customers, item-to-item collaborative filtering matches each of the users
 * purchased and rated items to similar items, then combines those similar items into a recommendation list. To
 * determine the most-similar match for a given item, the algorithm builds a similar-items list by finding items that
 * customers tend to purchase together.
 *
 * After the alogithm had found the list of products that costumers tend to purchase together it computes the similarity
 * between given item and other products from the list. It's possible to compute the similarity between two items in
 * various ways, but here we are calculating commonality index or similarity index between two products. A relatively
 * high commonality index for a given item and item from the list indicates that a relatively large percentage of users
 * who bought given item also bought item from the list and also a relatively low commonality index for given item and
 * item from the list indicates that a relatively small percentage of the users who bought given item also bought item
 * from the list.
 *
 * Algorithm then sorts products and their commonality index values and selects the 4 (four) items from the that have
 * the highest commonality index values.
 *
 */
@Entity
public class Recommendation extends Model {

    // Declaring recommendation properties.
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
    public Category category;
    public Integer count;

    // Declaring finder.
    public static Finder<String, Recommendation> finder = new Finder<>(Recommendation.class);

    /**
     * Constructor that creates new recommendation which has count set on one.
     *
     * @param user - Current user that had viewed product.
     * @param product - Viewed product.
     */
    public Recommendation(User user, Product product) {
        this.user = user;
        this.product = product;
        this.category = product.category;
        this.count = 1;
    }

    /**
     * Finds the 4 most viewed products and recommends them to users on the application main page.
     *
     * @return The list of most viewed products.
     */
    public static List<Product> getRecommendations() {
        // Declaring the list that will contain the most viewed products.
        List<Product> recommended = new ArrayList<>();
        // Declaring the sql query that will find top 10 products.
        String sql = "SELECT product_id, SUM(count) FROM `recommendation` GROUP BY product_id ORDER BY SUM(count) " +
                "DESC LIMIT 4;";
        // Declaring the list that contain the SqlRows that sql query has returned.
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql).findList();
        // Going trough every SqlRow and adding returned product to the most viewed product list.
        for (SqlRow sqlRow : sqlRows) {
            Product product  = Product.getProductById(sqlRow.getInteger("product_id"));
            recommended.add(product);
        }
        // Returning the list of the most viewed products.
        return recommended;
    }

    /**
     * Saves the data that user has viewed product. If the user had already viewed the selected product it increases
     * the count of product views for one. If the user has not viewed the product it creates new input into database.
     *
     * @param user User that is viewing product.
     * @param product Product which user views.
     */
    public static void savingProductView(User user, Product product) {
        Recommendation recommendation = Recommendation.getRecommendationByUserAndProduct(user, product);
        // Checking if recommendation exists.
        if (recommendation != null) {
            // If recommendation exists increase recommendation count for one and update recommendation.
            recommendation.count++;
            recommendation.update();
        } else {
            // If recommendation does not exist creating new recommendation and saving into database.
            Recommendation newRecommendation = new Recommendation(user, product);
            newRecommendation.save();
        }
    }

    /**
     * Goes through table recommendation in database and finds recommendation with inputed user and category. If
     * recommendation does not exist returns null.
     *
     * @param user - Inputed user.
     * @param product - Inputed product.
     * @return If recommendation exist returns recommendation, otherwise returns null.
     */
    public static Recommendation getRecommendationByUserAndProduct(User user, Product product) {
        // Declaring the variable that represent user product view.
        Recommendation recommendation = finder.where().eq("user", user).where().eq("product", product).findUnique();

        if (recommendation != null) {
            return finder.where().eq("user", user).where().eq("product", product).findList().get(0);
        }
        return null;
    }

    /**
     * Finds all products that inputed user had viewed from inputed category.
     *
     * @param user - Inputed user.
     * @param product - Inputed product.
     * @return The list of products that user had viewed.
     */
    public static List<Recommendation> getViewedProductByUser(User user, Product product) {
        return finder.where().eq("user", user).where().eq("category", product.category).findList();
    }

    /**
     * Finds the list of views for inputed product. If the product is viewed by logged in user then it returns the list
     * of product views except current user.
     *
     * @param product - Selected product.
     * @return The list of product views.
     */
    public static List<Recommendation> getAllProductViews(Product product){
        User user = SessionHelper.currentUser();
        // Checking if there is logged in user.
        if (user != null) {
            return finder.where().eq("product", product).where().ne("user", user).findList();
        }
        return finder.where().eq("product", product).findList();
    }

    /**
     * Matches each of the users purchased and rated items to similar items, then combines those similar items into a
     * recommendation list. To determine the most-similar match for a given item, it builds a similar-items list by
     * finding items that customers tend to purchase together.
     *
     * After the the list of products that costumers tend to purchase together is found it computes the similarity
     * between given item and other products from the list. The similarity between two items is computed by calculating
     * commonality index or similarity index between two products. A relatively high commonality index for a given item
     * and item from the list indicates that a relatively large percentage of users who bought given item also bought
     * item from the list and also a relatively low commonality index for given item and item from the list indicates
     * that a relatively small percentage of the users who bought given item also bought item from the list.
     *
     * At end sorts products and their commonality index values and selects the 4 (four) items from the that have the
     * highest commonality index values.
     *
     * @param product Selected product.
     * @return The list that contain 4 (four) recommended products.
     */
    public static List<Product> getRecommendedProducts(Product product) {
        // Find list of all views in table recommendation for inputed product.
        List<Recommendation> commonRecommend = getAllProductViews(product);
        // Declaration of the list of objects that contain product and number of views of that product.
        List<ProductCount> commonProducts = findSimilarProducts(commonRecommend, product);
        // Calling method calculateSimilarProducts() and returning the list of recommended products.
        return calculateSimilarProducts(commonProducts, product);
    }

    /**
     * Finds the list of all products that other user that had viewed selected product, have also viewed. After the
     * list of all users that had viewed product is found, it goes through the list of products of each user and
     * creates the object that contain the product and the number of product views. After creating it saves the
     * object into the list.
     *
     * @param commonRecommend - The list of product views.
     * @param product - Selected product.
     * @return The list of objects that contains of the product and the number of product views.
     */
    public static List<ProductCount> findSimilarProducts(List<Recommendation> commonRecommend, Product product) {
        // Declaration of list of users.
        List<User> commonUsers = new ArrayList<>();
        // Add all users that viewed inputed product in user list.
        for (Recommendation r : commonRecommend) {
            commonUsers.add(r.user);
        }
        // Declaration of the list of objects that contain product and number of views of that product.
        List<ProductCount> commonProducts = new ArrayList<>();
        // Find all products that each user from common user list had viewed.
        for (int i = 0; i < commonUsers.size(); i++) {
            List<Recommendation> recommendations = getViewedProductByUser(commonUsers.get(i), product);
            for (int j = 0; j < recommendations.size(); j++) {
                // Declaring the productCount object that contain product and the number of product views.
                ProductCount productCount = new ProductCount(recommendations.get(j).product);
                // Checking if the object already exists in the list. If the object already exists it increases number
                // of product views for one.
                if (ProductCount.doesContain(commonProducts, productCount)) {
                    ProductCount.increaseCount(productCount);
                } else {
                    commonProducts.add(productCount);
                }
            }
        }
        // Retutning the list of objects that contains of the product and the number of product views.
        return commonProducts;
    }

    /**
     * Goes trough the list of objects that contain product and the number of product views and computes the
     * commonality index between the selected product and each product from the list. After the commonality indexes
     * are computed it sorts the list by commonality index value and returns the 4 (four) most similar products.
     *
     * @param commonProducts - The list of objects that contain product and the number of product views.
     * @param product - Selected product.
     * @return The list that contain 4 (four) recommended products.
     */
    public static List<Product> calculateSimilarProducts(List<ProductCount> commonProducts, Product product) {
        // Declaring the list that will contain products and the commonality index value.
        List<ProductCount> mostViewed = new ArrayList<>();
        // Going trough the inputed list and calculating the commonality index for each product.
        for (int i = 0; i < commonProducts.size(); i++) {
            Product other = commonProducts.get(i).product;
            Double index = calculateCommonalityIndex(product, other);
            mostViewed.add(new ProductCount(other, index));
        }
        // Sorting the list by commonality index value.
        ProductCount.sortByIndex(mostViewed);
        // Declaring the list that will contain recommended products.
        List<Product> recommended = new ArrayList<>();
        // Adding the products to the list.
        for (int i = 0; i < mostViewed.size(); i++) {
            if (!mostViewed.get(i).product.equals(product)) {
               recommended.add(mostViewed.get(i).product);
            }
        }
        // Returning the list of recommended products.
        if (recommended.size() < 5) {
            return recommended;
        }
        return recommended.subList(0, 4);
    }

    /**
     * Calculates the number of users that have viewed both inputed products.
     *
     * @param p1 - First product.
     * @param p2 - Second product.
     * @return Number of users that have viewed both products.
     */
    public static Integer getProductsCommonViewCount(Product p1, Product p2) {
        // Finding the all views of both products.
        List<Recommendation> productRecommendationsOne = finder.where().eq("product", p1).findList();
        List<Recommendation> productRecommendationsTwo = finder.where().eq("product", p2).findList();
        // Declaring variable that will contain the number of views.
        Integer commonCount = 0;
        // Going trough both lists and comparing users.
        for (int i = 0; i < productRecommendationsOne.size(); i++) {
            for (int j = 0; j < productRecommendationsTwo.size(); j++) {
                if (productRecommendationsOne.get(i).user.equals(productRecommendationsTwo.get(j).user)) {
                    commonCount++;
                }
            }
        }
        // Returning the number of common users.
        return commonCount;
    }

    /**
     * Calculates the number user that have viewed inputed product.
     *
     * @param product - Inputed product.
     * @return Number of users that have viewed product.
     */
    public static Integer getProductViewCount(Product product) {
        return finder.where().eq("product", product).findRowCount();
    }

    /**
     * Calculates the commonality index value between two inputed products. The commonality index value is computed
     * by formula commonalityIndex(p1, p2) = Ncommon / sqrt(Np1 * Np2) where Ncommon represents the number of common
     * users that have viewed both products, Np1 represents the number of users that have viewed first product and
     * Np2 represents the number of users that have viewed second product.
     *
     * @param p1 - First product.
     * @param p2 - Second product.
     * @return Commonality index value between two products.
     */
    public static Double calculateCommonalityIndex(Product p1, Product p2) {
        // Declaring variables that represent number of common user views and number of each product views.
        Integer commonNumber = getProductsCommonViewCount(p1, p2);
        Integer productOneCount = getProductViewCount(p1);
        Integer productTwoCount = getProductViewCount(p2);
        // Calculating the commonality index.
        Double commonalityIndex = commonNumber / Math.sqrt(productOneCount * productTwoCount);
        // Returning the commonality index.
        return commonalityIndex;
    }

    /**
     * Created by Adis Cehajic on 10/7/2015.
     *
     * This class represent the class that will be used for creating object that will contain product and the number
     * of product views and the object that will contain product and the commonality index value.
     */
    public static class ProductCount {

        // Declaring the properties.
        public Product product;

        public Double commonalityIndex;

        public Integer count;

        /**
         * Constructor that creates ProductCount object that contain product and commonality index value.
         *
         * @param product - Inputed product.
         * @param commonalityIndex - Inputed commonality index value.
         */
        public ProductCount(Product product, Double commonalityIndex) {
            this.product = product;
            this.commonalityIndex = commonalityIndex;
        }

        /**
         * Constructor that creates ProductCount object that contain product and the number of product views. When
         * creating new object the number of product views is set to one.
         *
         * @param product - Inputed product.
         */
        public ProductCount(Product product) {
            this.product = product;
            this.count = 1;
        }

        /**
         * Increases the number of product views of inputed product for one.
         *
         * @param productCount - Inputed object that contains product and the number of product views.
         */
        public static void increaseCount(ProductCount productCount) {
            productCount.count = productCount.count + 1;
        }

        /**
         * Checks if the inputed list of ProductCount objects contain the inputed ProductCount object.
         *
         * @param list - The list of ProductCount objects.
         * @param productCount - The inputed ProductCount object.
         * @return True if the list contain inputed object, otherwise false.
         */
        public static Boolean doesContain(List<ProductCount> list, ProductCount productCount) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).product.equals(productCount.product)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Sorts the inputed list of objects that contain product and its commonality index value by commonality index
         * value.
         *
         * @param commonProductIndexes The list of ProductCount objects.
         */
        public static void sortByIndex(List<ProductCount> commonProductIndexes) {
            Collections.sort(commonProductIndexes, new Comparator<ProductCount>() {
                @Override
                public int compare(ProductCount cpi1, ProductCount cpi2) {
                    return cpi2.commonalityIndex.compareTo(cpi1.commonalityIndex);
                }
            });
        }
    }
}