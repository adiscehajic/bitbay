package helpers;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import play.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by banjic on 22/10/15.
 */
public class PayPalManager {
    public static Payment configure(String priceString, String desc, String token, Map<String, String> config, APIContext context, Amount amount,
                                 List<Transaction> transactionList, Transaction transaction, Payer payer, Payment payment, RedirectUrls redirects, String CLIENT_ID, String CLIENT_SECRET, Payment madePayments){
        try {
            token = new OAuthTokenCredential(CLIENT_ID, CLIENT_SECRET).getAccessToken();

            config = new HashMap<>();
            config.put("mode", "sandbox");

            context = new APIContext(token);
            context.setConfigurationMap(config);

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
            redirects.setCancelUrl("http://localhost:9000/");
            redirects.setReturnUrl("http://localhost:9000/purchasesuccess");
            payment.setRedirectUrls(redirects);

            madePayments = payment.create(context);

        } catch (PayPalRESTException e) {
            Logger.warn("PayPal Exception");
        }

        return madePayments;
    }

}
