package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;

import java.lang.String;
import java.util.List;

/**
 * Created by Adnan on 8.9.2015.
 */
@Entity
public class Category extends Model {

    @Id
    public Integer id;
    @Constraints.MaxLength(255)
    @Constraints.Required
    public String name;

    @OneToMany
    private List<Product> products;

    private static Finder<String, Category> finder =
            new Finder<>(Category.class);




    public Category(String name) {
        this.name = name;
    }

    public static Category getCategoryByName(String name) {
        Category c = Category.finder.where().eq("name", name).findUnique();
        return c;
    }

    public static Category getCategoryById(Integer id) {
        Category c = Category.finder.where().eq("id", id).findUnique();
        return c;
    }

    public static List<Category> findAll() {
        List<Category> categories = finder.orderBy("name asc").findList();
        return categories;
    }

}
