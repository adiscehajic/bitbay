package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.javafx.fxml.expression.Expression;
import controllers.Users;
import helpers.SessionHelper;
import jdk.nashorn.internal.parser.Token;
import org.mindrot.jbcrypt.BCrypt;
import play.Logger;
import play.data.validation.Constraints;

import java.nio.MappedByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import play.data.format.Formats;
import com.avaje.ebean.Model.Finder;
import play.data.validation.ValidationError;
import play.filters.csrf.CSRF;
import scala.collection.immutable.StreamViewLike;
import play.filters.csrf.AddCSRFToken;

import javax.persistence.*;

import java.util.UUID;

import java.lang.String;
import java.lang.Integer;
import java.util.List;


/**
 * Created by Adis Cehajic on 02/09/15.
 */
@Entity
public class User extends Model {

    // Declaring properties of User model.
    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    @Constraints.Required(message = "Please insert first name.")
    @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "First name can't contain diggits.")
    public String firstName;

    @Constraints.MaxLength(255)
    @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "Last name can't contain diggits.")
    @Constraints.Required(message = "Please insert last name.")
    public String lastName;

    @Column(unique = true)
    @Constraints.MaxLength(255)
    //@Constraints.Required(message = "Please insert email.")
    @Constraints.Email(message = "Valid email is required.")
    public String email;

    @Constraints.MaxLength(255)
    @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
    public String password;

    @Transient
    @Constraints.MaxLength(255)
    @Constraints.MinLength(value = 8, message = "Minimum 8 characters are required.")
    public String confirmPassword;

    @Constraints.MaxLength(15)
    public String phoneNumber;

    @ManyToOne
    @JsonBackReference
    public UserType userType;

    @ManyToOne
    @JsonBackReference
    public Country country;

    @Constraints.MaxLength(255)
    public String city;

    public Integer zip;

    @Constraints.MaxLength(255)
    public String address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Product> products;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<CartItem> cartItems;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Thumb> thumbs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Recommendation> recommendations;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    public List<Message> senderMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    public List<Message> receiverMessages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Rating> ratings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Bid> bids;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public List<Purchase> purchases;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public Cart cart;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public Image image;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date registration = new Date();

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date updated = new Date();

    public String token;

    public Boolean validated;

    // Declaring finder.
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
        User user = finder.where().eq("email", email).findUnique();
        // Checking if the loaded user exists and if his password corresponds to the inputed password.
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * This method search in DB user with selected email.
     * @param email - User email
     * @return - User with selected email
     */
    public static User getUserByEmail(String email) {
        User user = finder.where().eq("email", email).findUnique();
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * This method goes through DB and find user with given ID.
     * @param id - User ID
     * @return - User with given ID
     */
    public static User findById(Integer id){
        User user = finder.where().eq("id", id).findUnique();
        if(user != null){
            return user;
        }else{
            return null;
        }
    }

    /**
     * This method return list of all users from DB.
     * @return
     */
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

    /**
     * Validates the admin login form and returns all errors that occur during user login.
     * @return Errors that have occur during user login.
     */
    public List<ValidationError> validate() {
        // Declaring the list of errors.
        List<ValidationError> errors = new ArrayList<>();
        // Checking are the inputed password and confirm password equal.
        if (!this.confirmPassword.equals(this.password)) {
            errors.add(new ValidationError("confirmPassword", "Confirm password must match with password."));
        }
        return errors.isEmpty() ? null : errors;
    }

    /**
     * This method find user and all his sold products. Than it goes through list of products and
     * summ all ratings from products, and result will be displayed as users average grade on his profile.
     */
    public static String getAverageUserRating(User user) {
        //List of all users products
        List<Product> userProducts = Product.findAllProductsByUser(user);
        Double average = 0.0;
        Double ratedProducts = 0.0;

        if (userProducts != null && userProducts.size() > 0) {
            for (int i = 0; i < userProducts.size(); i++) {
                //Calculating sum off  all products rating
                Double rating = Double.parseDouble(Rating.getAverageRating(userProducts.get(i).id));
                if (rating != 0.0) {
                    ratedProducts = ratedProducts + 1;
                }
                average = average + rating;
            }

            //Calculating average grade of user
            if (average != 0) {
                Double result = average / ratedProducts;
                DecimalFormat df = new DecimalFormat("#.0");

                return df.format(result);
            } else {
                return "0.0";
            }
        } else {
            return "0.0";
        }
    }

    /**
     *This static method is used to find user by token.
     */
    public static User findUserByToken(String token) {
        return finder.where().eq("token", token).findUnique();
    }

    /**
     * Seting validated atribute to True or False
     * @param validated
     */
    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    /**
     * This method finds user in database by email that we passed into this method.
     * @param email
     * @return
     */
    public static User findUserByEmail(String email) { return finder.where().eq("email", email).findUnique(); }

    /**
     * Method is used to validate user after verification email was sent to users email address.
     * After verification user token is deleted and verified field is set to true.
     * @return - True or False
     */
    public static Boolean validateUser(User user) {
        if (user == null) {
            return false;
        }else {
            user.token = null;
            user.setValidated(true);
            user.update();
        }
        return true;
    }

}
