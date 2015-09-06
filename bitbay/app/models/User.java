package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;


/**
 * Created by adis.cehajic on 02/09/15.
 */
@Entity
public class User extends Model {

    // Declaring properties of User model.
    public Integer id;
    public String firstName;
    public String lastName;
    public String email;
    public String password;

    // Declaring variable.
    private static Finder<String, User> finder =
            new Finder<>(String.class, User.class);

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
    public User(Integer id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
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

    /**
     * Prints the information about user first name and last name.
     * @return
     */
    public String toString () {
        return String.format("%s %s", firstName, lastName);
    }
}
