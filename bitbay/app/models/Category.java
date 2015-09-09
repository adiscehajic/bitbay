package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.format.Formats;

import javax.persistence.*;

import java.lang.String;

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

//    public Integer parent_id;
//    public Integer status_id;

    private static Finder<String, Category> finder =
            new Finder<>(Category.class);


//    public Category(String name, Integer parent_id, Integer status_id){
//
//        this.name = name;
//        this.parent_id = parent_id;
//        this.status_id = status_id;
//    }


    public static Category getCategoryById(Integer id) {
        Category c = Category.finder.where().eq("id", id).findUnique();
        return c;
    }
}
