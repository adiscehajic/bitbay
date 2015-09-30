package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;
import views.html.user.userProducts;
import org.apache.commons.io.FileUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    @Constraints.Required(message = "Product name is required.")
    public String name;
    public String description;
    public String manufacturer;
    @ManyToOne
    @Constraints.Required(message = "Product category is required.")
    public Category category;
    @Constraints.Min(value = 1, message = "Product price must be larger than 0.")
    @Constraints.Required(message = "Product price is required.")
    public Double price;
    @Constraints.Min(value = 1, message = "Product quantity must be larger than 0.")
    @Constraints.Required(message = "Product quantity is required.")
    public Integer quantity;
    @Constraints.Required(message = "Product selling type is required.")
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

    public Product() {
    }

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

    public static List<Product> searchProductByName(String name) {
        List<Product> product = Product.finder.where().contains("name", name).findList();

        return product;
    }

}
