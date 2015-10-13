package helpers;

        import models.User;
        import org.apache.commons.mail.DefaultAuthenticator;
        import org.apache.commons.mail.HtmlEmail;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import play.Play;

/**
 * Created by Adnan on 12.10.2015..
 */

public class MailHelper {


    /**
     * This method is used to sent verification email to email address that user wrote on registration form.
     * Email contains greeting by first and last name, and link to verify email.
     */
    public static void send(String email, String message) {

        try {
            User user = SessionHelper.currentUser();
            HtmlEmail mail = new HtmlEmail();
            mail.setSubject("bitBay Welcome");
            mail.setFrom("bitBay <bitbayservice@gmail.com>");
            mail.addTo("Contact <bitbayservice@gmail.com>");
            mail.addTo(email);
            mail.setMsg(message);
            mail.setHtmlMsg(String
                    .format("<html><body><h3>Welcome %s %s</h3><strong> %s </strong> <p> %s </p> <p> %s </p> </body></html>",
                            user.firstName, user.lastName,
                            "Thank you for signing up to bitBay!",
                            "Please confirm your email address.", message));
            mail.setHostName("smtp.gmail.com");
            mail.setStartTLSEnabled(true);
            mail.setSSLOnConnect(true);
            mail.setAuthenticator(new DefaultAuthenticator(
                    Play.application().configuration().getString("EMAIL_USERNAME_ENV"),
                    Play.application().configuration().getString("EMAIL_PASSWORD_ENV")
            ));
            mail.send();
        } catch (Exception e) {

        }
    }
}
