package models;

import com.avaje.ebean.Model;
import helpers.CommonHelpers;
import play.Logger;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Adis Cehajic on 10/12/2015.
 */
@Entity
public class Auction extends Model {

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

    public Auction(){}

    public static Auction getAuctionByProduct(Product product) {
        return finder.where().eq("product", product).findUnique();
    }

    public static String getAuctionEndingDate(Product product) {
        Auction auction = finder.where().eq("product", product).findUnique();
        Date endingDate = null;
        if (auction != null) {
            endingDate = auction.endingDate;
            return CommonHelpers.getOnlyDate(endingDate);
        }
        return CommonHelpers.getOnlyDate(new Date());
    }

    public static String getAuctionEndingTime(Product product) {
        Auction auction = finder.where().eq("product", product).findUnique();

        if (auction != null) {
            Date endingDate = auction.endingDate;
            return CommonHelpers.getTimeFromDate(endingDate);
        }
        return CommonHelpers.getTimeFromDate(new Date());
    }

    public static void checkAuctionOutcome() {
        List<Auction> auctions = finder.all();

        Logger.info("Number of auctions: " + auctions.size());

        Date currentDate = new Date();

        for (int i = 0; i < auctions.size(); i++) {
            Date auctionEndingDate = auctions.get(i).endingDate;
            if (auctions.get(i).isActive && auctionEndingDate.before(currentDate)) {
                Logger.info(currentDate.toString());
                Logger.info(auctionEndingDate.toString());
                List<Bid> bids = Bid.getAuctionBids(auctions.get(i));
                Bid highestBid = bids.get(0);
                Logger.info(highestBid.user.email);

                String winningMessage = "Congratulations!!! You won bitBay auction for item #" + auctions.get(i).product.id + " - " + auctions.get(i).product.name +
                        ".\n\r \n\r To proceed with transaction please contact: " + auctions.get(i).product.user.email;

                Message message = new Message(User.getUserByEmail("bitbayservice@gmail.com"), highestBid.user, "Auction Winning", winningMessage);
                message.save();

                auctions.get(i).isActive = false;
                auctions.get(i).update();
            }
        }


    }

}
