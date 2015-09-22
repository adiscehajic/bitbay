package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import java.util.Date;
import play.data.format.Formats;
import com.avaje.ebean.Model.Finder;

import javax.persistence.*;

import java.lang.String;
import java.lang.Integer;
import java.util.List;


/**
 * Created by adis.cehajic on 02/09/15.
 */
@Entity
public class User extends Model {

    // Declaring properties of User model.
    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    @Constraints.Required
    @Constraints.Pattern("[a-z A-Z]")
    public String firstName;

    @Constraints.MaxLength(255)
    @Constraints.Required
    public String lastName;

    @Column(unique = true)
    @Constraints.MaxLength(255)
    @Constraints.Required()
    @Constraints.Email
    public String email;

    @Constraints.MaxLength(255)
    @Constraints.MinLength(8)
    @Constraints.Required()
    public String password;

    @ManyToOne
    @Constraints.Required
    public UserType userType;

    @ManyToOne
    public Country country;

    @Constraints.MaxLength(255)
    public String city;

    public Integer zip;

    @Constraints.MaxLength(255)
    public String address;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> products;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    public String token;

    // Declaring variable.
    private static Finder<String, User> finder =
            new Finder<>(User.class);

    /**
     * Constructor.
     */
    public User () {}

    /**
     * Constructor that creates new user.
     * @param id - Id of the user.
     * @param firstName - First name of the user.
     * @param lastName - Last name of the user.
     * @param email - Email of the user.
     * @param password - Password of the user.
     */
    public User(Integer id, String firstName, String lastName, String email, String password, UserType userType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.userType = userType;
    }

    /**
     * Method that enables authorisation of the email and password which user has inputed during login.
     * @param email - Inputed email.
     * @param password - Inputed password.
     * @return If the inputed email and password are correct returns User, otherwise returns null.
     */
    public static User authenticate(String email, String password) {
        // Loading user from database which has inputed email.
        User user = User.finder.where().eq("email", email).findUnique();
        // Checking if the loaded user exists and if his password corresponds to the inputed password.
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    public static User getUserByEmail(String email) {
        User user = User.finder.where().eq("email", email).findUnique();
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    public static User findById(Integer id){
        User user = User.finder.where().eq("id", id).findUnique();
        if(user != null){
            return user;
        }else{
            return null;
        }
    }

    //TODO
//    public static Result deleteUser(Integer id){
//       // User user = findById(id);
//       // if(user != null){
//       //     return redirect("/") ;
//       // }else{
//
//       // }
//        return TODO;
//    }
    public static List<User> findAll() {
        List<User> users = finder.where().ne("userType.id", 1).findList();
        return users;
    }

    /**
     * Prints the information about user first name and last name.
     * @return
     */
    public String toString () {
        return String.format("%s %s", firstName, lastName);
    }
}
