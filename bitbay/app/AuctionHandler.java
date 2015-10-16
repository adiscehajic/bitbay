import models.Auction;
import play.Logger;

/**
 * Created by Adis Cehajic on 10/15/2015.
 */
public class AuctionHandler {

    // Declaring constant that will represent interval in which thread will sleep.
    private static final long CHECK_INTERVAL = 3000;

    /**
     * Creates and starts thread that will execute method that checks all auctions and, if the auction is over, find
     * highest bid and send message to the winner.
     */
    public static void handleAuctions() {
        // Declaring thread.
        Thread auctionThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        // Calling method that checks all auctions and finds the highest bid.
                        executeChecks();
                        // Sleeping thread for declared interval.
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (Exception e) {
                        Logger.error("Failure occured in Auction Handler", e);
                        break;
                    }
                }
            }
        });

        // Starting thread.
        auctionThread.setName("AuctionHandler-Thread");
        auctionThread.setDaemon(true);
        auctionThread.start();
    }

    /**
     * Calls method that checks all auctions and, if the auction is over, finds highest bid and send message to the
     * winner.
     */
    private static void executeChecks() {
        try {
            // Calling method that sends message to the winner of the auction.
            Auction.checkAuctionOutcome();
        } catch (Exception e) {
            Logger.error("Failed to execute auction checks", e);
        }
    }
}

