package controllers;

import helpers.CurrentBuyer;
import helpers.SessionHelper;
import models.Auction;
import models.Bid;
import models.Product;
import models.User;
import play.Logger;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Adis Cehajic on 10/15/2015.
 */
public class BidController extends Controller {

    // Declaring variable.
    private static Form<Bid> bidForm = Form.form(Bid.class);

    /**
     * Reads value that is inputed in form where user can input his auction bid. Inputed value must be higher than
     * current highest bid. If the inputed value is not higher it reloads page, and the bid is not saved. If the bid
     * is higher it sets new current highest bit to inputed bid value.
     *
     * @param id Id of the product where auction is performed.
     * @return Product profile page with selected id.
     */
    @RequireCSRFCheck
    @Security.Authenticated(CurrentBuyer.class)
    public Result placeBidForProduct(Integer id) {
        // Connecting with form where user can place a bid.
        Form<Bid> boundForm = bidForm.bindFromRequest();
        // Checking if the form has errors. If the form has errors reloads the product profile page.
        if (boundForm.hasErrors()) {
            return redirect(routes.ProductController.getProduct(id));
        }
        // Finding current user.
        User user = SessionHelper.currentUser();
        // Finding the auction of selected product.
        Auction auction = Auction.getAuctionByProduct(Product.getProductById(id));
        // Declaring the variable that contain amount of placed bid.
        String amount = boundForm.data().get("amount");
        try {
            // Checking if the inputed bid is higher than current highest bid. If the bid is lower it renders product
            // profile page and the inputed bid is not saved.
            if (Double.compare(Double.parseDouble(amount), Bid.getAmountOfHighestBid(auction)) < 0) {
                return redirect(routes.ProductController.getProduct(id));
            }
            // Declaring new bid and saving it into database.
            Bid bid = new Bid(user, auction, Double.parseDouble(amount));
            bid.save();
        } catch(NumberFormatException e) {
            Logger.error(e.getMessage());
            return redirect(routes.ProductController.getProduct(id));
        }
        // Rendering product profile page.
        return redirect(routes.ProductController.getProduct(id));
    }
}
