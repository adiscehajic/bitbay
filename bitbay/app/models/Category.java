package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adnan Lapendic on 8.9.2015.
 */
@Entity
public class Category extends Model {

    // Declaring constant that represents 'Other' category id.

    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    @Constraints.MinLength(value = 1, message = "Category name can't be empty string.")
    @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "Category name can't contain diggits.")
    @Constraints.Required(message = "Please input category name.")
    public String name;

    @ManyToOne
    @JsonBackReference
    public Category parent;

    @Transient
    @Constraints.Required(message = "Please select category.")
    public String parentName;

    @OneToMany
    public List<Product> products;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public List<Category> categories;

    //Finder for class Category
    private static Finder<String, Category> finder =
            new Finder<>(Category.class);

    /**
     * Constructor for Category
     * @param name - Category name
     */
    public Category(String name, Category category) {
        this.name = name;
        this.parent = category;
    }

    /**
     * This method is used to find single category by name
     * @param name - Category name
     * @return - Category by name
     */
    public static Category getCategoryByName(String name) {
        Category c = Category.finder.where().eq("name", name).findUnique();
        return c;
    }

    /**
     * This method is used to find a category by ID
     * @param id - ID of a category
     * @return - Category by ID
     */
    public static Category getCategoryById(Integer id) {
        Category c = Category.finder.where().eq("id", id).findUnique();
        return c;
    }

    /**
     * This method is used to list all categories and category other.
     * @return - List of all categories
     */
    public static List<Category> findAllAll(){
        List<Category> allCategories = finder.orderBy("name asc").where().eq("parent", null).findList();
        return allCategories;
    }

    /**
     * This method is used to find all categories without category "other"
     * @return - List of categories without category "other"
     */
    public static List<Category> findAll() {
        List<Category> categories = finder.orderBy("name asc").where().ne("id", 1).where().eq("parent", null).findList();
        return categories;
    }

    /**
     * Finds all subcategories of inputed category.
     * @param category Inputed category.
     * @return List of all subcategories of inputed category.
     */
    public static List<Category> getAllSubcategories(Category category) {
        return finder.orderBy("name asc").where().eq("parent", category).findList();
    }

    public static Category getParentCategory(Category category) {
        return finder.where().eq("id", category.id).findUnique();
    }


    /**
     * Validates creating new category and returns all errors that occur during new category input.
     * @return Errors that have occur during new category input.
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
