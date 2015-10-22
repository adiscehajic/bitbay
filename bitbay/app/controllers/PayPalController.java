package controllers;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import helpers.CurrentBuyer;
import helpers.PayPalManager;
import helpers.SessionHelper;
import models.*;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.purchase.purchaseResult;
import views.html.user.userCart;

import java.util.*;

/**
 * Created by Medina Banjic on 12/10/15.
 */
@Security.Authenticated(CurrentBuyer.class)
public class PayPalController extends Controller {

    /**
     * PayPal configurated constants
     */
    public static final String CLIENT_ID = Play.application().configuration()
            .getString("clientid");
    public static final String CLIENT_SECRET = Play.application().configuration()
            .getString("secret");

    public static String token;

    public static Amount amount;

    public static Payer payer;

    public static RedirectUrls redirects;

    public static APIContext context;

    public static Map<String, String> config;

    private static User currentUser = SessionHelper.currentUser();

    private static double price;
    private static double totalPrice;

    private static String priceString;
    private static String productString;
    private static String desc = "";

    private static PurchaseItem purchaseItem;
    private static Purchase purchase = new Purchase();

    private static PaymentExecution paymentExecution;

    private static Integer quantity;

    private static List<Transaction> transactionList;
    private static Transaction transaction;

    private static Payment payment;
    private static Payment madePayments;
    private static String paymentID = "";

    private static List<String> details;


    private static Cart currentUserCart = Cart.findCartByUser(currentUser);
    private static List<CartItem> cartItems = CartItem.findCartItemsByCart(currentUserCart);
    private static List<PurchaseItem> purchaseItems = new ArrayList<>();


    /**
     * This method configurates PayPal and PayPal payment information
     * Redirects to payment approval
     *
     * @return approve_url
     */
    @Security.Authenticated(CurrentBuyer.class)
    @RequireCSRFCheck
    public Result purchaseProcessing() {
        // Process cart/payment information
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItemI = cartItems.get(i);
            price = cartItemI.price;
            totalPrice += price;
            quantity = cartItemI.quantity;
            productString = cartItemI.product.name;
            priceString = String.format("%1.2f", price);
            desc += "\nProduct: " + productString + " --- Quantity: " + quantity;

            purchaseItem = new PurchaseItem(cartItemI.product, cartItemI.user, purchase, quantity);
            // Adding the purchase item to the purchaseItems list
            purchaseItems.add(purchaseItem);
        }

        priceString = String.format("%1.2f", totalPrice);

        desc += "\nTotal amount: " + priceString;

        /* details to render in the success view */
        details = new ArrayList<String>();
        details.add("Total amount " + priceString);

        //Configuration PayPal
        madePayments = PayPalManager.configure(priceString, desc, token, config, context, amount, transactionList, transaction, payer, payment, redirects, CLIENT_ID, CLIENT_SECRET, madePayments);

       /*Iterating through the url lists received from the paypal response
         * and checking if we got a approval_url
         * If a approval url is found, we can redirect the client to the
         * paypal checkout page*/
        Iterator<Links> it = madePayments.getLinks().iterator();
        while (it.hasNext()) {
            Links link = it.next();

            if (link.getRel().equals("approval_url")) {
                return redirect(link.getHref());
            }
        }
        return redirect("/");
    }


    /**
     * This is the method that is processed if the "success" return url is used,
     * in other words, if the user continues to approve transaction during the
     * paypal checkout
     *
     * @return
     */
    public Result purchaseSuccess() {
        DynamicForm paypalReturn = Form.form().bindFromRequest();

        //These datas are generated in the return_url
        paymentID = paypalReturn.get("paymentId");
        String payerID = paypalReturn.get("PayerID");
        String token = paypalReturn.get("token");


        try {
            String accessToken = new OAuthTokenCredential(CLIENT_ID,
                    CLIENT_SECRET).getAccessToken();
            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");
            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);
            payment = Payment.get(accessToken, paymentID);
            paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerID);
            String sale_id = payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();


            //Executes a payment
            Payment newPayment = payment.execute(context, paymentExecution);
            //Creates new Purchase and saves it into database
            purchase = Purchase.createPurchase(paymentID, sale_id, totalPrice, token, currentUser, purchaseItems, cartItems, purchase);

            flash("info");
        } catch (Exception e) {
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return redirect("/");
        }
        /**when the payment is built, the client is redirected to the
         purchaseResult view*/
        return ok(purchaseResult.render(currentUser, purchase, details));
    }

    /**
     * Method renders userCart and shows a flash notification
     * message if the transaction is canceled during the procedure on the
     * paypal page. It's called with the "fail" return url
     *
     * @return
     */
    public Result purchaseFail() {


        flash("error");
        return ok(userCart.render(cartItems, currentUser));
    }


    /**
     * Method saves purchase in a database
     *
     * @return render index page with a flash message
     */
    public Result approveTransaction() {
        //TODO
        List<Product> products = Product.findAll();
        List<Product> recommendations = Recommendation.getRecommendations();
        flash("success");
        return ok(index.render(products, recommendations));
    }


}

