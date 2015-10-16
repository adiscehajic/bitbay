package models;

import com.avaje.ebean.Model;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.*;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adis Cehajic on 10/14/2015.
 */
@Entity
public class Bid extends Model {

    @Id
    public Integer id;

    @ManyToOne
    public User user;

    @ManyToOne
    public Auction auction;

    //@Pattern(regexp="^1-9/,$", message="Enter a valid amount for your bid.")
    public Double amount;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date bidingDate = new Date();

    private static Finder<String, Bid> finder = new Finder<>(Bid.class);

    public Bid(){}

    public Bid(User user, Auction auction, Double amount) {
        this.user = user;
        this.auction = auction;
        this.amount = amount;
    }

    public static Integer getNumberOfAuctionBids(Auction auction) {
        return finder.where().eq("auction", auction).findRowCount();
    }

    public static Double getAmountOfHighestBid(Auction auction) {

        try {
            return finder.where().eq("auction", auction).orderBy("amount desc").findList().get(0).amount;
        } catch (IndexOutOfBoundsException e) {
            Logger.error(e.getMessage());
            return auction.startingPrice;
        }
    }

    public static List<Bid> getAuctionBids(Auction auction) {
        return finder.where().eq("auction", auction).orderBy("amount desc").findList();
    }

    public String toString() {
        return String.format("%d) %s - %f", id, user.email, amount);
    }

    /**
     * Validates the bid form and returns all errors that occur during user product biding.
     * @return Errors that have occur during user product biding.
     */
   /* public List<ValidationError> validate() {
        // Declaring the list of errors.
        List<ValidationError> errors = new ArrayList<>();
        // Checking are the inputed amount is higher than highest bid.
        if (Double.compare(this.amount, Bid.getAmountOfHighestBid(auction)) >= 0) {
            errors.add(new ValidationError("amount", "You have to bid more than " + Bid.getAmountOfHighestBid(auction) + " KM."));
        }
        return errors.isEmpty() ? null : errors;
    }*/
}
