package models;

import com.avaje.ebean.Model;
import helpers.CommonHelpers;
import play.data.format.Formats;
import javax.persistence.*;
import java.util.*;

/**
 * Created by Adis Cehajic on 10/12/2015.
 */
@Entity
public class Auction extends Model {

    // Declaring auction properties.
    @Id
    public Integer id;

    @OneToOne
    public Product product;

    public Double startingPrice;

    public Boolean isActive = true;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date startingDate;

    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date endingDate;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    public List<Bid> bids;

    private static Finder<String, Auction> finder = new Finder<>(Auction.class);

    /**
     * Constructor.
     */
    public Auction(){}

    /**
     * Returns the auction of inputed product.
     * @param product
     * @return Auction of inputed product.
     */
    public static Auction getAuctionByProduct(Product product) {
        return finder.where().eq("product", product).findUnique();
    }

    /**
     * Returns the end date of auction.
     * @param product
     * @return
     */
    public static String getAuctionEndingDate(Product product) {
        // Finding the auction of inputed product.
        Auction auction = getAuctionByProduct(product);
        // Checking if there is auction for inputed product.
        if (auction != null) {
            // Finding and returning auction end date.
            Date endingDate = auction.endingDate;
            return CommonHelpers.getOnlyDate(endingDate);
        }
        return CommonHelpers.getOnlyDate(new Date());
    }

    /**
     * Returns the end time of auction.
     * @param product
     * @return
     */
    public static String getAuctionEndingTime(Product product) {
        // Finding the auction of inputed product.
        Auction auction = getAuctionByProduct(product);
        // Checking if there is auction for inputed product.
        if (auction != null) {
            // Finding and returning auction end time.
            Date endingDate = auction.endingDate;
            return CommonHelpers.getTimeFromDate(endingDate);
        }
        return CommonHelpers.getTimeFromDate(new Date());
    }

    /**
     * Goes trough the table auction and checks which auctions are over. If the auction is over, finds all bids for
     * that auction, selects the highest bid and sends winning message, also sends to all other users message that
     * auction is over.
     */
    public static void checkAuctionOutcome() {
        // Declaring list of all auctions.
        List<Auction> auctions = finder.all();
        // Declaring variable that represents current date.
        Date currentDate = new Date();
        // Declaring bitbay service user as message sender.
        User sender = User.getUserByEmail("bitbayservice@gmail.com");
        // Going trough all auctions.
        for (int i = 0; i < auctions.size(); i++) {
            // Declaring variable that represents auction ending date.
            Date auctionEndingDate = auctions.get(i).endingDate;
            // Checking if the auction is active and if the auction ending date is before current date.
            if (auctions.get(i).isActive && auctionEndingDate.before(currentDate)) {
                // Finding all auction bids.
                List<Bid> bids = Bid.getAuctionBids(auctions.get(i));
                // Declaring variable that represents highest bid.
                Bid highestBid = bids.get(0);
                // Declaring string variable that represents winning message.
                String winningMessage = "Congratulations!!! You won bitBay auction for item #" + auctions.get(i).product.id + " - " + auctions.get(i).product.name +
                        ".\n\r \n\r To proceed with transaction please contact: " + auctions.get(i).product.user.email;
                // Declaring and saving winning message.
                Message message = new Message(sender, highestBid.user, "Auction Winning", winningMessage);
                message.save();
                // Declaring string set that will contain emails of all users that have not had highest bid.
                Set<String> bidUsers = new HashSet<>();
                // Adding all user emails to the set.
                for (int j = 0; j < bids.size(); j++) {
                    bidUsers.add(bids.get(j).user.email);
                }
                // Removing email of highest bit user from the set.
                bidUsers.remove(highestBid.user.email);
                // Declaring string variable that represents losing message.
                String losingMessage = "Biding for item #" + auctions.get(i).product.id + " - " + auctions.get(i).product.name +
                        " is over.\n\r \n\r We are sorry to inform you that your bid was not enough.";
                // Declaring iterator list of all user emails.
                Iterator<String> iter = bidUsers.iterator();
                // Going trough all emails and sending message.
                while (iter.hasNext()) {
                    User receiver = User.getUserByEmail(iter.next());
                    Message msg = new Message(sender, receiver, "Auction results", losingMessage);
                    msg.save();
                }
                // Setting auction status to inactive.
                auctions.get(i).isActive = false;
                auctions.get(i).update();
            }
        }
    }
}
