package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by adis.cehajic on 08/09/15.
 */
@Entity
public class Product extends Model {

    @Id
    public Integer id;
    @Constraints.MaxLength(255)
    @Constraints.Required
    public String name;
    public String description;
    public String manufacturer;
    public String chategory;
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


}
