package helpers;

import play.Play;

/**
 * This class is user to store all constant variables from this project on one place.
 * Created by Adnan on 28.10.2015..
 */
public class ConstantsHelper {
    public static final int ADMIN = 1;
    public static final int BUYER = 2;
    public static final int SELLER = 3;
    public static final int CATEGORY_OTHER = 1;
    public static final String PAY_PAL_CLIENT_ID = Play.application().configuration()
            .getString("clientid");
    public static final String PAY_PAL_CLIENT_SECRET = Play.application().configuration()
            .getString("secret");
    public static final String SMS_ACCOUNT_SID = "AC14660cacd6b25d31819abb8e55c3ee50";
    public static final String SMS_AUTH_TOKEN = "f8fa0ca08497e56caf6b81255a13260f";
    public static final String BIT_BAY = Play.application().configuration().getString("BIT_BAY");
    public static final String EMAIL_USERNAME = Play.application().configuration().getString("EMAIL_USERNAME_ENV");
    public static final String EMAIL_PASSWORD = Play.application().configuration().getString("EMAIL_PASSWORD_ENV");
}
