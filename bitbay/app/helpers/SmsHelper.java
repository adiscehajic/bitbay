package helpers;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import play.Logger;
import java.util.ArrayList;
import java.util.List;

public class SmsHelper {

    /**
     * This method is triggered after auction ends. It will send sms notification to auction winner
     * in which user is noticed that he had won auction and on which item.
     *
     * @param smsBody - Sms content
     * @param smsTo   - User phone number
     */
    public static void sendSms(String smsBody, String smsTo) {

        TwilioRestClient client = new TwilioRestClient(ConstantsHelper.SMS_ACCOUNT_SID, ConstantsHelper.SMS_AUTH_TOKEN);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Body", smsBody));
        params.add(new BasicNameValuePair("To", smsTo));
        params.add(new BasicNameValuePair("From", "+12568874433"));
        Logger.info(smsTo);

        SmsFactory smsFactory = client.getAccount().getSmsFactory();
        Sms sms = null;
        try {
            sms = smsFactory.create(params);
        } catch (TwilioRestException e) {
            e.printStackTrace();
        }
    }
}