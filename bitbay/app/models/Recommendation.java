package models;

import com.avaje.ebean.Model;
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
 */
@Entity
public class Recommendation extends Model {

    // Declaring recommendation properties.
    @Id
    public Integer id;
    @ManyToOne
    public User user;

    @ManyToOne
    public Product product;

    @ManyToOne
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
     * Goes trough table recommendation in database and finds recommendation with inputed user and category. If
     * recommendation does not exist returns null.
     *
     * @param user - Inputed user.
     * @param category - Inputed category.
     * @return If recommendation exist returns recommendation, otherwise returns null.
     */

   /*
    public static Recommendation getRecommendationByUserAndCategory(User user, Category category) {
        return finder.where().eq("user", user).where().eq("category", category).findUnique();
    }
*/
    /**
     * Returns the category whose products users had most viewed.
     *
     * @return Category that is most viewed.
     */
  /*  public static Category getTopCategory() {
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
*/
    /**
     * Returns all recommendations from categories which products current user had viewed and ordering them by number
     * of viewes.
     *
     * @param user - Current user.
     * @return All recommendations from categories which products current user had viewed.
     */
/*    public static List<Recommendation> getTopCategoriesByUser(User user) {
        return finder.where().eq("user", user).orderBy().desc("count").findList();
    }
*/


    public static List<Product> getRecommendations() {
        List<Recommendation> recommendations = finder.all();

        List<Product> products = new ArrayList<>();
        for (Recommendation r : recommendations) {
            if (r.product.id <= 96) {
                products.add(r.product);
            }
        }
        Collections.shuffle(products);

        return products.subList(0, 4);
    }

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
     * Goes trough table recommendation in database and finds recommendation with inputed user and category. If
     * recommendation does not exist returns null.
     *
     * @param user - Inputed user.
     * @param product - Inputed category.
     * @return If recommendation exist returns recommendation, otherwise returns null.
     */
    public static Recommendation getRecommendationByUserAndProduct(User user, Product product) {
        Recommendation recommendation = finder.where().eq("user", user).where().eq("product", product).findUnique();

        if (recommendation != null) {
            return finder.where().eq("user", user).where().eq("product", product).findList().get(0);
        }
        return null;
    }

    public static List<Recommendation> getViewedProductByUser(User user, Product product) {
        return finder.where().eq("user", user).where().eq("category", product.category).findList();
    }

    public static List<Recommendation> getAllProductViews(Product product){
        User user = SessionHelper.currentUser();
        if (user != null) {
            return finder.where().eq("product", product).where().ne("user", user).findList();
        }
        return finder.where().eq("product", product).findList();
    }

    public static List<Product> getRecommendedProducts(Product product) {
        // Find list of all lines in table recommendation for inputed product.
        List<Recommendation> commonRecommend = getAllProductViews(product);
        // Declaration of the list of objects that contain product and number of views of that product.
        List<ProductCount> commonProducts = findSimilarProducts(commonRecommend, product);
        return calculateSimilarProducts(commonProducts, product);
    }

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
                ProductCount productCount = new ProductCount(recommendations.get(j).product);
                if (ProductCount.doesContain(commonProducts, productCount)) {
                    ProductCount.increaseCount(productCount);
                } else {
                    commonProducts.add(productCount);
                }
            }
        }

        return commonProducts;
    }

    public static List<Product> calculateSimilarProducts(List<ProductCount> commonProducts, Product product) {

        List<ProductCount> mostViewed = new ArrayList<>();

        for (int i = 0; i < commonProducts.size(); i++) {
            Product other = commonProducts.get(i).product;
            Double index = calculateCommonalityIndex(product, other);
            mostViewed.add(new ProductCount(other, index));
        }

        ProductCount.sortByIndex(mostViewed);

        List<Product> recommended = new ArrayList<>();

        for (int i = 0; i < mostViewed.size(); i++) {
            if (!mostViewed.get(i).product.equals(product)) {
               recommended.add(mostViewed.get(i).product);
            }
        }

        if (recommended.size() < 5) {
            return recommended;
        }
        return recommended.subList(0, 4);
    }

    public static Integer getProductsCommonViewCount(Product p1, Product p2) {
        List<Recommendation> productRecommendationsOne = finder.where().eq("product", p1).findList();
        List<Recommendation> productRecommendationsTwo = finder.where().eq("product", p2).findList();

        Integer commonCount = 0;

        for (int i = 0; i < productRecommendationsOne.size(); i++) {
            for (int j = 0; j < productRecommendationsTwo.size(); j++) {
                if (productRecommendationsOne.get(i).user.equals(productRecommendationsTwo.get(j).user)) {
                    commonCount++;
                }
            }
        }

        return commonCount;
    }

    public static Integer getProductViewCount(Product product) {
        return finder.where().eq("product", product).findRowCount();
    }

    public static Double calculateCommonalityIndex(Product p1, Product p2) {
        // commonalityIndex (p1, p2) = Ncommon / sqrt(Np1 * Np2)

        Integer commonNumber = getProductsCommonViewCount(p1, p2);
        Integer productOneCount = getProductViewCount(p1);
        Integer productTwoCount = getProductViewCount(p2);

        Double commonalityIndex = commonNumber / Math.sqrt(productOneCount * productTwoCount);

        return commonalityIndex;
    }


    public static class ProductCount {

        public Product product;

        public Double commonalityIndex;

        public Integer count;

        public ProductCount(Product product, Double commonalityIndex) {
            this.product = product;
            this.commonalityIndex = commonalityIndex;
        }

        public ProductCount(Product product) {
            this.product = product;
            this.count = 1;
        }

        public static void increaseCount(ProductCount productCount) {
            productCount.count = productCount.count + 1;
        }

        public static Boolean doesContain(List<ProductCount> list, ProductCount productCount) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).product.equals(productCount.product)) {
                    return true;
                }
            }
            return false;
        }

        public static void sortByIndex(List<ProductCount> commonProductIndexes) {
            Collections.sort(commonProductIndexes, new Comparator<ProductCount>() {
                @Override
                public int compare(ProductCount cpi1, ProductCount cpi2) {
                    return cpi2.commonalityIndex.compareTo(cpi1.commonalityIndex);
                }
            });
        }

        public String toString() {
            return String.format("%s - %.2f", product.name, commonalityIndex);
        }

    }



}