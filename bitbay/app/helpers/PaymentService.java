package helpers;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import helpers.ConstantsHelper;
import helpers.CurrentBuyer;
import helpers.MailHelper;
import helpers.SessionHelper;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import com.paypal.api.payments.Links;
import views.html.index;
import views.html.purchase.purchaseResult;
import views.html.user.userCart;

import javax.swing.text.html.HTML;
import java.util.*;

import static play.mvc.Controller.flash;
import static play.mvc.Results.redirect;


/**
 * This is a class for configuration of a payment service
 *
 * Created by Medina Banjic on 11/12/15.
 */
public class PaymentService {


    /**
     * Fields and variables declarations
     */
    private static User currentUser = SessionHelper.currentUser();

    private static double price;
    private static double totalPrice;

    private static String priceString;
    private static String productString;
    private static String desc;

    private static String token;

    private static PurchaseItem purchaseItem;
    private static Purchase purchase;

    private static PaymentExecution paymentExecution;

    private static Integer quantity;

    private static Amount amount;

    private static List<Transaction> transactionList;
    private static Transaction transaction;

    private static Payer payer;
    private static String payerID;

    private static Payment payment;
    private static Payment madePayments;
    private static String paymentID;
    private static String saleID;

    private static RedirectUrls redirects;

    private static List<String> details;

    private static APIContext context;

    private static List<CartItem> cartItems;
    private static List<PurchaseItem> purchaseItems;

    private static Map<String, String> config;

    private static Integer purchaseId;
    private static String messageInvoice;
    public static Links link;

    //A constructor for a payment service
    public PaymentService() {
    }

    /**
     * This is a method which configurates Payment Service which is PayPal
     * @param cartId
     * @return
     */
    public static boolean purchaseProcessing(Integer cartId) {
        try {
            //list of items of current user's cart
            cartItems = Cart.findCartById(cartId).cartItems;
            //purchased items list
            purchaseItems = new ArrayList<>();

            //Configuration PayPal
            //security token, required, String
            token = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID, ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();
            //configuration of mode (sandbox or live)
            config = new HashMap<>();
            config.put("mode", "sandbox");
            //forwarding generated token and config
            context = new APIContext(token);
            context.setConfigurationMap(config);
            //initializing description and total price which we send to PayPal
            desc = "";
            totalPrice = 0;

            //Process cart/payment information
            //This is a loop which iterates through cart and gets attributes of every cart item
            //so that we can create desc, total price and also to save it to purchased items list as a new purchaseItem
            for (int i = 0; i < cartItems.size(); i++){
                CartItem cartItemFromCart = cartItems.get(i);
                Logger.info("Cart item details: " + cartItemFromCart);
                price = cartItemFromCart.price;
                //adding an item price to total price
                totalPrice += price;
                quantity = cartItemFromCart.quantity;
                productString = cartItemFromCart.product.name;
                //creating a price string for a purchased item
                priceString = String.format("%1.2f", price);
                //creating a description for a purchase
                desc += "Product: " + productString + "  -   Quantity: " + quantity + " --- ";
                //creating new purchase item
                purchaseItem = new PurchaseItem(cartItemFromCart.product, cartItemFromCart.user, purchase, quantity);
                // Adding the purchase item to the purchaseItems list
                purchaseItems.add(purchaseItem);
                Logger.info("Purchase item details: " + purchaseItem.toString());
            }
            Logger.info("Izasao iz loopa");
            //creating a new purchase for a current user, with parameters purchased items list,
            //total price and generated security token
            purchase = new Purchase(currentUser,purchaseItems,totalPrice,token);
            purchase.save();
            Logger.info("snimio purchase");

            //purchase id from a database
            purchaseId = purchase.id;

            //price string for a total amount for PayPal
            priceString = String.format("%1.2f", totalPrice);

            desc += " >> Total amount: " + priceString;

            //string for an invoice mail context
            messageInvoice = desc;

            //details to render in the success view
            details = new ArrayList<String>();
            details.add("Total price " + priceString);

            // Configure payment
            //Amount object for PayPal
            amount = new Amount();
            amount.setTotal(priceString);
            amount.setCurrency("USD");

            //transaction object for PayPal
            transactionList = new ArrayList<>();
            transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDescription(desc);
            transactionList.add(transaction);

            //payer object for PayPal
            payer = new Payer();
            payer.setPaymentMethod("paypal");

            //payment object
            payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactionList);

            //Redirect urls
            redirects = new RedirectUrls();
            redirects.setCancelUrl(ConstantsHelper.BIT_BAY + "/purchasefail");
            redirects.setReturnUrl(ConstantsHelper.BIT_BAY + "/purchasesuccess");
            payment.setRedirectUrls(redirects);

            //creating payment
            madePayments = payment.create(context);

            //Iterating through the url lists received from the paypal response
            //and checking if we got an approval_url
            //If an approval url is found, we can redirect the client to the
            //paypal checkout page
            Iterator<Links> it = madePayments.getLinks().iterator();
            while(it.hasNext()) {
                link = it.next();

                if(link.getRel().equals("approval_url")) {
                    return true;
                }
            }
        } catch(PayPalRESTException e){
            Logger.warn("PayPal Exception: Could not redirect to PayPal");
            return false;
        }
        return true;
    }


    /**
     * This is the method which executes the payment
     *
     * @return purchaseResult page
     */
    public static boolean purchaseSuccess() {
        DynamicForm paypalReturn = Form.form().bindFromRequest();

        //This data is generated in the return_url
        paymentID = paypalReturn.get("paymentId");
        payerID = paypalReturn.get("PayerID");

        try {
            //configuration
            String accessToken = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID, ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();
            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");
            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);

            //Payment which has been approved
            payment = Payment.get(accessToken, paymentID);
            //execution of a purchase
            paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerID);

            //Executes a payment
            Payment newPayment = payment.execute(context, paymentExecution);
            //getting a sale id
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
        } catch (Exception e) {
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * Method renders userCart and shows a flash notification
     * message if the transaction is canceled during the procedure on the
     * paypal page. It's called with the "fail" return url
     *
     * @return
     */
    public static boolean purchaseFail() {

        //This loop iterates through purchased items list created in a database and deletes them
        //Purchase is also deleted
        for (int i = 0; i < purchaseItems.size(); i++){
            purchaseItems.get(i).delete();
        }
        purchase.delete();
        return true;
    }


    /**
     * This method saves data to tables purchaseItem and purchase,
     * and also deletes cartItems of a cart and also a cart of the current user
     * @param purchaseItems
     * @param purchase
     * @param cartItems
     */
    public static void savePurchaseItemToDatabase(List<PurchaseItem> purchaseItems, Purchase purchase, List<CartItem> cartItems){
        Product product;
        Cart cart = null;

        //This loop iterates through purchased items list and saves every purschseItem to database
        //Cancellation time is created here, depending on a product attribute cancelation which is set by seller
        //Product quantity is decreased

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
            cart = cartItemI.cart;
        }
        if (cart != null)
            cart.delete();
    }


    /**
     * Method which increases product quantity
     */
    public static boolean increaseProductQuantity(Integer purchaseItem_id) {

        //purchasedItem found by the given id
        PurchaseItem purchaseItem = PurchaseItem.getPurchaseItemById(purchaseItem_id);
        Product product = Product.getProductById(purchaseItem.product.id);
        product.quantity += purchaseItem.quantity;
        product.update();
        return true;
    }

    public static boolean executeRefund(Integer purchaseItem_id) {

        try {
            //Configuration
            String accessToken = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID,
                    ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();
            Logger.info("Access token generated");

            Map<String, String> sdkConfig = new HashMap<String, String>();
            sdkConfig.put("mode", "sandbox");
            Logger.info("sdkConfig mode");

            context = new APIContext(accessToken);
            context.setConfigurationMap(sdkConfig);
            Logger.info("context configuration map sdk");

            //Declaring and initializing purchaseItem, sale object and refund object
            PurchaseItem purchaseItem = PurchaseItem.getPurchaseItemById(purchaseItem_id);
            Sale sale = Sale.get(context, purchaseItem.purchase.sale_id);
            Refund refund = new Refund();
            Logger.info("purchasedItem: " + purchaseItem.toString());

            //
            if(purchaseItem.purchase.sale_id != null && purchaseItem.isRefunded == 0) {

                //creating amount object
                totalPrice = purchaseItem.price;
                priceString = String.format("%1.2f", totalPrice);
                Logger.info("Price: " + priceString);

                Amount amount = new Amount();
                amount.setCurrency("USD");
                amount.setTotal(priceString);

                refund.setAmount(amount);
                Logger.info("Amount: " + refund.getAmount());
                Logger.info("Refund: " + refund.getDescription());
                //Executing refund of a sale
                Refund newrefund = sale.refund(context, refund);
                Logger.info("Refund: " + refund.getState());

                //changes purchaseItem attribute to 1
                purchaseItem.isRefunded = 1;
                purchaseItem.update();
                Logger.info("Purchase item is refunded? " + purchaseItem.isRefunded);
            }
            if(purchaseItem.purchase.sale_id == null || purchaseItem.isRefunded == 1) {
                purchaseItem.isRefunded = 1;
                purchaseItem.update();
            }
        } catch (PayPalRESTException e) {
            Logger.info("Error at refund processing: " + e.getMessage());
            return false;

        }
        return true;
    }

    /**
     * Getters and Setters
     */
    public static double getPrice() {
        return price;
    }

    public static double getTotalPrice() {
        return totalPrice;
    }

    public static String getPriceString() {
        return priceString;
    }

    public static String getProductString() {
        return productString;
    }

    public static String getDesc() {
        return desc;
    }

    public static PurchaseItem getPurchaseItem() {
        return purchaseItem;
    }

    public static Purchase getPurchase() {
        return purchase;
    }

    public static Integer getQuantity() {
        return quantity;
    }

    public static List<String> getDetails() {
        return details;
    }

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static List<PurchaseItem> getPurchaseItems() {
        return purchaseItems;
    }

    public static String getMessageInvoice() {
        return messageInvoice;
    }

    public static Integer getPurchaseId() {
        return purchaseId;
    }
}


