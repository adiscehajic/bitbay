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

    public static final int ADMIN = 1;
    public static final int BUYER = 2;
    public static final int SELLER = 3;

    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    public String name;

    private static Finder<String, UserType> finder = new Finder<>(UserType.class);

    public static UserType getUserTypeById(Integer id) {
        UserType ut = UserType.finder.where().eq("id", id).findUnique();
        return ut;
    }

    public static UserType getUserTypeByName(String name) {
        return UserType.finder.where().eq("name", name).findUnique();
    }

    public static List<UserType> getAllUserTypes() {
        return UserType.finder.where().ne("id", 1).findList();
    }
}
