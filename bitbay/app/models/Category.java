package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adnan on 8.9.2015.
 */
@Entity
public class Category extends Model {

    // Declaring constant that represents 'Other' category id.
    public static final int OTHER = 1;

    @Id
    public Integer id;
    @Constraints.MaxLength(255)
    @Constraints.MinLength(value = 0, message = "Category name can't be empty string.")
    @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "Category name can't contain diggits.")
    @Constraints.Required(message = "Please input category name.")
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
        List<Category> categories = finder.orderBy("name asc").where().ne("id", 1).findList();
        return categories;
    }


    /**
     * Validates the admin login form and returns all errors that occur during administrator login.
     * @return Errors that have occur during administrator login.
     */
    public List<ValidationError> validate() {
        // Declaring the list of errors.
        List<ValidationError> errors = new ArrayList<>();
        // Checking if there is another category in database with same name.
        if (finder.where().eq("name", this.name).findRowCount() > 0) {
            errors.add(new ValidationError("name", "Category already exists."));
        }
        return errors.isEmpty() ? null : errors;
    }

}
