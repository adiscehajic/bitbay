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
    private List<Image> images;
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    private static Finder<String, Product> finder = new Finder<String, Product>(Product.class);

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

    public static List<Product> findAllProductsByUser(String email) {

        User user = User.getUserByEmail(email);
        List<Product> products = Product.finder.where().eq("user", user).findList();

        return products;
    }
}
