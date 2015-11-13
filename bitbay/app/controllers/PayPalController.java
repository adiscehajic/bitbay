package controllers;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import helpers.*;
import models.*;
import org.json.simple.JSONObject;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.purchase.purchaseResult;
import views.html.user.userCart;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.*;

/**
 * Created by Medina Banjic on 12/10/15.
 */
@Security.Authenticated(CurrentBuyer.class)
public class PayPalController extends Controller {

    /**
     * PayPal configurated constants
     */
    private static User currentUser = SessionHelper.currentUser();

    private static double price;
    private static double totalPrice;

    private static String priceString;
    private static String productString;
    private static String desc = "";

    private static String token;

    private static PurchaseItem purchaseItem;
    private static Purchase purchase;

    private static PaymentExecution paymentExecution;

    private static Integer quantity;

    private static Amount amount;

    private static List<Transaction> transactionList;
    private static Transaction transaction;

    private static Payer payer;

    private static Payment payment;
    private static Payment madePayments;
    private static String paymentID = "";
    private static String saleID = "";

    private static RedirectUrls redirects;

    private static List<String> details;

    private static APIContext context;

    private static List<CartItem> cartItems;
    private static List<PurchaseItem> purchaseItems;
    private static List<Product> products;
    private static List<Product> recommendations;
    private static Map<String, String> config;

    private static Integer purchaseId;

    private static String messageInvoice;

    /**
     * This method configurates PayPal and PayPal payment information
     * Redirects to payment approval
     * @return approve_url
     */
    @Security.Authenticated(CurrentBuyer.class)
    @RequireCSRFCheck
    public Result purchaseProcessing(Integer cartId) {
        try {
            //cartItems = Cart.findCartByUser(currentUser).cartItems;

            cartItems = Cart.findCartById(cartId).cartItems;
            purchaseItems = new ArrayList<>();

            //Configuration PayPal
            token = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID, ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();

            config = new HashMap<>();
            config.put("mode", "sandbox");

            context = new APIContext(token);
            context.setConfigurationMap(config);

            desc = "";
            totalPrice = 0;

            // Process cart/payment information
            for (int i = 0; i < cartItems.size(); i++){
                CartItem cartItemI = cartItems.get(i);
                price = cartItemI.price;
                totalPrice += price;
                quantity = cartItemI.quantity;
                productString = cartItemI.product.name;
                priceString = String.format("%1.2f", price);
                desc += "Product: " + productString + "  -   Quantity: " + quantity + " --- ";
                purchaseItem = new PurchaseItem(cartItemI.product, cartItemI.user, purchase, quantity);
                // Adding the purchase item to the purchaseItems list
                purchaseItems.add(purchaseItem);
            }

            purchase = new Purchase(currentUser,purchaseItems,totalPrice,token);
            purchase.save();

            purchaseId = purchase.id;


            priceString = String.format("%1.2f", totalPrice);

            desc += " >> Total amount: " + priceString;
            messageInvoice = desc;

        /* details to render in the success view */
            details = new ArrayList<String>();
            details.add("Total price " + priceString);

            // Configure payment

            amount = new Amount();
            amount.setTotal(priceString);
            amount.setCurrency("USD");

            transactionList = new ArrayList<>();
            transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription(desc);
            transactionList.add(transaction);

            payer = new Payer();
            payer.setPaymentMethod("paypal");

            payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactionList);

            /** Redirect urls*/
            redirects = new RedirectUrls();
            redirects.setCancelUrl("http://localhost:9000/purchasefail");
            redirects.setReturnUrl("http://localhost:9000/purchasesuccess");
            payment.setRedirectUrls(redirects);

            madePayments = payment.create(context);

       /*Iterating through the url lists received from the paypal response
         * and checking if we got a approval_url
         * If a approval url is found, we can redirect the client to the
         * paypal checkout page*/
            Iterator<Links> it = madePayments.getLinks().iterator();
            while(it.hasNext()) {
                Links link = it.next();

                if(link.getRel().equals("approval_url")) {
                    return redirect(link.getHref());
                }
            }
        } catch(PayPalRESTException e){
            Logger.warn("PayPal Exception");
        }

        return redirect(routes.ApplicationController.index());
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
            String accessToken = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID,
                    ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();
            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");
            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);
            payment = Payment.get(accessToken, paymentID);
            paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerID);

            //Executes a payment
            Payment newPayment = payment.execute(context, paymentExecution);
            saleID = newPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();

            purchase = Purchase.findPurchaseById(purchaseId);
            purchase.payment_id = paymentID;
            purchase.sale_id = saleID;
            purchase.update();

            // Calling method that goes through all purchased user items and checks if there is items that are courses
            // of the bitClassroom application. If the product item is course it sends to the bitClassroom application
            // information about purchased course, otherwise connects with bitTracking application and sends for every
            // purchased product item sellers address information and buyers address information.
            PurchaseItem.managePurchasedItems(purchaseItems);

            savePurchaseItemToDatabase(purchaseItems, purchase, cartItems);

            flash("info");
        } catch (Exception e) {
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return redirect(routes.ApplicationController.index());
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
        return ok(userCart.render(cartItems,currentUser));
    }


    /**
     * Method saves purchase in a database
     *
     * @return render index page with a flash message
     */
    public Result approveTransaction() {
        products = Product.findAll();
        recommendations = Recommendation.getRecommendations();
        flash("success");
        return ok(index.render(products, recommendations));
    }




    /**
     * This method saves data to tables purchaseItem and purchase, and also deletes cartItems from cart of the current user
     * @param purchaseItems
     * @param purchase
     * @param cartItems
     */
    public static void savePurchaseItemToDatabase(List<PurchaseItem> purchaseItems, Purchase purchase, List<CartItem> cartItems){
        Product product;

        for (int i = 0; i < purchaseItems.size(); i++) {
            PurchaseItem item = purchaseItems.get(i);
            product = Product.getProductById(item.product.id);
            item.purchase = purchase;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(purchase.purchaseDate);
            calendar.add(Calendar.HOUR, product.cancelation);
            item.cancelationDueDate = calendar.getTime();
            item.save();

            product.quantity -= item.quantity;
            product.update();
        }


        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItemI = cartItems.get(i);
            cartItemI.delete();
        }
    }


    /**
     * Method for refunding
     * @return redirect to the index page
     */
    public Result refundProcessing(Integer purchaseItem_id) {
        products = Product.findAll();
        recommendations = Recommendation.getRecommendations();
        Logger.info("+++++++++++++++" + purchaseItem_id);

        if(executeRefund(purchaseItem_id)){
           Logger.info("----------------------" + purchaseItem_id);

           PurchaseItem purchaseItem = PurchaseItem.getPurchaseItemById(purchaseItem_id);
           Product product = Product.getProductById(purchaseItem.product.id);
           product.quantity += purchaseItem.quantity;
           product.update();
           return redirect(routes.Users.getUserPurchases());
       }
        return ok(index.render(products, recommendations));
    }

    public static boolean executeRefund(Integer purchaseItem_id) {

        try {
            Logger.info("111111111111111");

            String accessToken = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID,
                    ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();
            Logger.info("222222222222222");

            Map<String, String> sdkConfig = new HashMap<String, String>();
            Logger.info("333333333333333");

            sdkConfig.put("mode", "sandbox");
            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);
            Logger.info("4444444444444444");

            PurchaseItem purchaseItem = PurchaseItem.getPurchaseItemById(purchaseItem_id);
            Sale sale = new Sale();
            Refund refund = new Refund();
            Logger.info("555555555555555");

            if(purchaseItem.purchase.sale_id != null && purchaseItem.isRefunded == 0) {
                Logger.info("66666666666666");

                System.out.println(purchaseItem.purchase.token);
                totalPrice = purchaseItem.price;
                String totalPriceString = String.format("%1.2f", totalPrice);
                sale.setId(purchaseItem.purchase.sale_id);
                Logger.info("777777777777777");
                Logger.info("Sale id is: " + purchaseItem.purchase.sale_id);

                Amount amount = new Amount();
                amount.setCurrency("USD");
                amount.setTotal(totalPriceString);
                refund.setAmount(amount);
                Logger.info("888888888888888 ++++++++++ " + refund.getAmount());

                sale.refund(context, refund);
                Logger.info("9999999999999§99");

                purchaseItem.isRefunded = 1;
                purchaseItem.update();
                Logger.info("111111111111111");

            }
                if(purchaseItem.purchase.sale_id == null || purchaseItem.isRefunded == 1) {
                    purchaseItem.isRefunded = 1;
                    purchaseItem.update();
                }

            flash("success");
        } catch (PayPalRESTException e) {
            //flash("error", Messages.get("error.msg.02"));
            Logger.info("Error at purchaseProcessing: " + e.getMessage());
            return false;

        }
        return true;
    }

    /**
     * This method is checking if email user typed in email field is in DB and if user is registrated before.
     * If user is registrated in DB new unique token is generated and verification mail is sent to users email.
     * @return
     */
    public Result sendInvoice() {
        User user = SessionHelper.currentUser();

        if (user != null) {
            MailHelper.sendInvoice(user.email, PayPalController.messageInvoice);
            flash("success", "Email successfully sent");
        } else {
            flash("error", "Could not find user with that email ");
        }
        return redirect(routes.ApplicationController.index());
    }


}