package models;

import com.avaje.ebean.Model.Finder;
import play.data.validation.Constraints;
import javax.persistence.*;
import java.util.*;
import java.lang.String;
/**
 * Created by dinko.hodzic on 08/09/15.
 */
@Entity
public class UserType {

    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    public String name;

    private static Finder<String, UserType> finder = new Finder<>(UserType.class);

    /**
     * Method that finds user by ID and return its user type.
     * @param id - User ID
     */
    public static UserType getUserTypeById(Integer id) {
        UserType ut = UserType.finder.where().eq("id", id).findUnique();
        return ut;
    }

    /**
     * Method that search in DB user with selected name, and return what type of user is it.
     * @param name - User name
     */
    public static UserType getUserTypeByName(String name) {
        return UserType.finder.where().eq("name", name).findUnique();
    }

    /**
     * Method goes through database and finds all user types and return it as a list.
     */
    public static List<UserType> getAllUserTypes() {
        return UserType.finder.where().ne("id", 1).findList();
    }
}
