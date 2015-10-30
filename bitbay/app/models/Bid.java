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

    // Declaring bid properties.
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

    /**
     * Constructor.
     */
    public Bid(){}

    /**
     * Constructor that creates new bid with inputed user, auction and amount of bid.
     * @param user - User that placed the bid.
     * @param auction - Auction in which is the bid placed.
     * @param amount - Amount of the bid.
     */
    public Bid(User user, Auction auction, Double amount) {
        this.user = user;
        this.auction = auction;
        this.amount = amount;
    }

    /**
     * Returns the count of bids for inputed auction.
     * @param auction - Auction for which is count of bids returned.
     * @return Count of bids.
     */
    public static Integer getNumberOfAuctionBids(Auction auction) {
        return finder.where().eq("auction", auction).findRowCount();
    }

    /**
     * Returns the amount of the highest bid for inputed auction.
     * @param auction - Auction for which is highest bid returned.
     * @return
     */
    public static Double getAmountOfHighestBid(Auction auction) {
        try {
            return finder.where().eq("auction", auction).orderBy("amount desc").findList().get(0).amount;
        } catch (IndexOutOfBoundsException e) {
            Logger.error(e.getMessage());
            return auction.startingPrice;
        }
    }

    /**
     * Returns the list of all bids for inputed auction.
     * @param auction - Inputed auction.
     * @return List of all bids.
     */
    public static List<Bid> getAuctionBids(Auction auction) {
        return finder.where().eq("auction", auction).orderBy("amount desc").findList();
    }

    /**
     * Prints the information about the bid.
     * @return
     */
    public String toString() {
        return String.format("%d) %s - %f", id, user.email, amount);
    }

}
