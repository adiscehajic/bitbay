package controllers.rest;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import controllers.PayPalController;
import helpers.ConstantsHelper;
import helpers.SessionHelper;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.*;

/**
 * Created by Kerim on 4.11.2015.
 */
public class ApiPayPalController extends Controller {

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

    private static final Form<User> userForm = Form.form(User.class);

    public Result purchaseProcessing(Integer productId) {
        DynamicForm boundForm = Form.form().bindFromRequest();

        List<PurchaseItem> purchaseItems = new ArrayList<>();
        Product p = Product.getProductById(productId);

      //  Form<User> boundForm = userForm.bindFromRequest();
        Integer userId = Integer.parseInt(boundForm.field("userId").value());
        String ipAddress = boundForm.field("ipAddress").value();

        try {

            config = new HashMap<>();
            config.put("mode", "sandbox");

            token = new OAuthTokenCredential(ConstantsHelper.PAY_PAL_CLIENT_ID, ConstantsHelper.PAY_PAL_CLIENT_SECRET).getAccessToken();

            context = new APIContext(token);
            context.setConfigurationMap(config);

            desc = "";
            totalPrice = 0;

            // Process cart/payment information

                price = p.price;
                totalPrice += price;
                quantity = 1;
                productString = p.name;
                priceString = String.format("%1.2f", price);
                desc += "Product: " + productString + "  -   Quantity: " + quantity + " --- ";
                purchaseItem = new PurchaseItem(p, p.user, purchase, quantity);
                // Adding the purchase item to the purchaseItems list
                purchaseItems.add(purchaseItem);


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
            redirects.setCancelUrl(ipAddress + "/api/purchasefail");
            redirects.setReturnUrl(ipAddress + "/api/purchasesuccess");
//
//            redirects.setCancelUrl("http://localhost:9000/api/purchasefail");
//            redirects.setReturnUrl("http://localhost:9000/api/purchasesuccess");

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

        return redirect("/");
    }

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

            PayPalController.savePurchaseItemToDatabase(purchaseItems, purchase, cartItems);

            flash("info");
        } catch (Exception e) {
            flash("error");
            Logger.debug("Error at purchaseSucess: " + e.getMessage(), e);
            return redirect("/");
        }
        /**when the payment is built, the client is redirected to the
         purchaseResult view*/
        return ok();
    }

    public Result purchaseFail() {

        return ok();
    }
}
