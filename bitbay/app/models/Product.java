package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Adis Cehajic on 08/09/15.
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
    @Column(columnDefinition = "TEXT")
    public String description;
    public String manufacturer;
    @ManyToOne
    @Constraints.Required(message = "Product category is required.")
    public Category category;
//    @Constraints.Min(value = 1, message = "Product price must be larger than 0.")
//    @Constraints.Required(message = "Product price is required.")
    public Double price;
//    @Constraints.Min(value = 1, message = "Product quantity must be larger than 0.")
//    @Constraints.Required(message = "Product quantity is required.")
    public Integer quantity;
    @Constraints.Required(message = "Product selling type is required.")
    public String sellingType;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    public List<Image> images;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Comment> comments;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    public List<CartItem> cartItems;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    public List<Rating> ratings;
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    public Auction auction;

    public int cancelation;

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
        cancelation = 0;
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

    public static Product getOneRandomProduct(List<Product> products) {
        Random rand = new Random();
        return products.get(rand.nextInt(products.size()));
    }

    public static List<Product> getTwoRandomProducts(List<Product> products) {
        List<Product> list = new LinkedList<>(products);
        Collections.shuffle(list);
        if (list.size() > 1) {
            return list.subList(0, 2);
        }
        return list.subList(0, list.size());
    }

    public static List<Product> getFourRandomProducts(List<Product> products) {
        List<Product> list = new LinkedList<>(products);
        Collections.shuffle(list);
        if (list.size() > 3) {
            return list.subList(0, 4);
        }
        return list.subList(0, list.size());
    }

    public static Integer cancelationHourToDay(Integer hours){
        return hours/24;
    }
}