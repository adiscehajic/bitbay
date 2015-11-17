package controllers;


import helpers.*;
import models.*;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.purchase.purchaseResult;
import views.html.user.userCart;
import java.util.*;

/**
 * This class is the way to communicate with PayPal REST
 *
 * Created by Medina Banjic on 12/10/15.
 */
@Security.Authenticated(CurrentBuyer.class)
public class PayPalController extends Controller {

    /**
     * Redirects to payment approval page
     * @return return url(purchaseSuccess method) if approved, cancel url(purchaseFail method) if declined
     */
    @Security.Authenticated(CurrentBuyer.class)
    @RequireCSRFCheck
    public Result purchaseProcessing(Integer cartId) {

        if(PaymentService.purchaseProcessing(cartId))
            return redirect(PaymentService.link.getHref());
        else
            return redirect(routes.ApplicationController.index());
    }


    /**
     * This is the method that is processed if the "success" return url is used,
     * in other words, if the user continues to approve transaction during the
     * PayPal checkout
     *
     * @return purchaseResult page
     */
    public Result purchaseSuccess() {

        if(PaymentService.purchaseSuccess())
            /**when the payment is built, the client is redirected to the
             purchaseResult view*/
            return ok(purchaseResult.render(SessionHelper.currentUser(), PaymentService.getPurchase(),
                    PaymentService.getDetails()));
        else
            return redirect(routes.CartController.getCart());
    }

    /**
     * Method renders userCart redirects to userCart
     * if the transaction is canceled during the procedure on the
     * paypal page. It's called with the "fail" return url
     *
     * @return
     */
    public Result purchaseFail() {

        if (PaymentService.purchaseFail())
            return ok(userCart.render(PaymentService.getCartItems(),SessionHelper.currentUser()));
        else
            return redirect(routes.ApplicationController.index());
    }



    /**
     * Method for refund
     * @return redirect to the index page
     */
    public Result refundProcessing(Integer purchaseItem_id) {
        List<Product> products = Product.findAll();
        List<Product> recommendations = Recommendation.getRecommendations();

        //if refund is done product quantity will be increased by refunded quantity
        if(PaymentService.executeRefund(purchaseItem_id)){
           PurchaseItem purchaseItem = PurchaseItem.getPurchaseItemById(purchaseItem_id);
           Product product = Product.getProductById(purchaseItem.product.id);
           product.quantity += purchaseItem.quantity;
           product.update();
            return redirect(routes.Users.getUserPurchases());
       }
        return ok(index.render(products, recommendations));
    }


    /**
     * This method sends invoice to user's email address
     */
    public Result sendInvoice() {
        User user = SessionHelper.currentUser();

        //if user exists sendInvoice method will get email address and string created while making an order
        //and send a message
        if (user != null) {
            MailHelper.sendInvoice(user.email, PaymentService.getMessageInvoice());
        } else {
            flash("error", "Could not find user with the email provided.");
        }
        return redirect(routes.ApplicationController.index());
    }
}