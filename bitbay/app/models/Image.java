package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by Denis Cehajic on 9/10/2015.
 */
@Entity
public class Image extends Model {

    // Declaring variable.
    private static Finder<String, Image> finder =
            new Finder<>(Image.class);

    @Id
    public Integer id;
    public String path;
    @ManyToOne
    public Product product;

    public Image(String path, Product product) {
        this.path = path;
        this.product = product;
    }

    public static String getImagePath(Product product) {
        List<Image> image = Image.finder.where().eq("product", product).findList();
        if (image.size() > 0) {
            return image.get(0).path;
        } else {
            return "http://placehold.it/450x600";
        }
    }

}
