package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

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
    public String chategory;
    @Constraints.Required
    public Double price;
    @Constraints.Min(1)
    @Constraints.Required
    public Integer quantity;
    public String sellingType;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    public Product(User user, String name, String description, String manufacturer, String chategory, Double price, Integer quantity, String sellingType) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.chategory = chategory;
        this.price = price;
        this.quantity = quantity;
        this.sellingType = sellingType;
    }
}
