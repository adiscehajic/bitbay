package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;
import views.html.user.userProducts;
import org.apache.commons.io.FileUtils;

import javax.persistence.*;
import java.util.*;

/**
 * Created by adis.cehajic on 08/09/15.
 */
@Entity
public class Product extends Model {

    @Id
    public Integer id;
    @ManyToOne
    public User user;
    @Constraints.MaxLength(255)
    @Constraints.Required
    public String name;
    public String description;
    public String manufacturer;
    @ManyToOne
    public Category category;
    @Constraints.Required
    public Double price;
    @Constraints.Min(1)
    @Constraints.Required
    public Integer quantity;
    public String sellingType;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Image> images;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    private static Finder<String, Product> finder = new Finder<String, Product>(Product.class);

    public Product () {}

    public Product(User user, String name, String description, String manufacturer, Category category, Double price, Integer quantity, String sellingType) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.sellingType = sellingType;
    }

    public static Product getProductById(Integer id) {
        Product product = Product.finder.where().eq("id", id).findUnique();
        return product;
    }

    public static List<Product> findAll() {
        List<Product> products = finder.all();
        return products;
    }

    public static List<Product> findAllProductsByUser(User user) {

        List<Product> products = Product.finder.where().eq("user", user).findList();

        return products;
    }

    public static List<Product> findAllProductsByCategory(Category category) {
        List<Product> products = Product.finder.where().eq("category", category).findList();

        return products;
    }

    /**
     * This method searches the database by a given String
     * @param term The string we want to find
     * @return A list containing all products containing that String
     */
    public static List<Product> searchProductByName(String term){

        List<Product>productNames = Product.finder.where().contains("name", term).findList();
        List<Product>productDescription = Product.finder.where().contains("description", term).findList();

        List<Product> products = new ArrayList<>();
        products.addAll(productNames);

        for (int i = 0; i < productDescription.size(); i++) {
            if (!products.contains(productDescription.get(i))) {
                products.add(productDescription.get(i));
            }
        }

        return products;
    }


}
