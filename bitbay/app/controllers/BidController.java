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

    private static Form<Bid> bidForm = Form.form(Bid.class);

    @RequireCSRFCheck
    @Security.Authenticated(CurrentBuyer.class)
    public Result placeBidForProduct(Integer id) {

        Form<Bid> boundForm = bidForm.bindFromRequest();

        if (boundForm.hasErrors()) {
            return redirect(routes.ProductController.getProduct(id));
        }

        User user = SessionHelper.currentUser();

        Auction auction = Auction.getAuctionByProduct(Product.getProductById(id));

        String amount = boundForm.data().get("amount");

        try {
            if (Double.compare(Double.parseDouble(amount), Bid.getAmountOfHighestBid(auction)) < 0) {
                return redirect(routes.ProductController.getProduct(id));
            }

            Bid bid = new Bid(user, auction, Double.parseDouble(amount));
            bid.save();
        } catch(NumberFormatException e) {
            Logger.error(e.getMessage());
            return redirect(routes.ProductController.getProduct(id));
        }

        return redirect(routes.ProductController.getProduct(id));

    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormBid() {
        Form<Bid> binded = bidForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

}
