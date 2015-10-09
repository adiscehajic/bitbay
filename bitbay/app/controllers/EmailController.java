package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.F;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by banjic on 05/10/15.
 */
public class EmailController  extends Controller{

    /**
     * This method gets inputs from contact us form, verifies recaptcha and sends email to
     * bitbayservice@gmail.com from userfeedback.bitbay@gmail.com
     */
    @Inject WSClient ws;
    @RequireCSRFCheck
    public Result sendEmail() {
        DynamicForm form = Form.form().bindFromRequest();


        String username = form.get("name");
        String mail = form.get("email");
        String subject = form.get("subject");
        String message = form.get("message");

        String grecaptcha = request().body().asFormUrlEncoded().get("g-recaptcha-response")[0];
        WSRequest rq = ws.url("https://www.google.com/recaptcha/api/siteverify");
        rq.setQueryParameter("secret", "6LfUKg4TAAAAAASFERHrMiHR0quZWg0oein3DsUu");
        rq.setQueryParameter("response", grecaptcha);

        F.Promise<JsonNode> responsePromise = rq.get().map(response -> {
            return response.asJson();
        });
        JsonNode returnedJson = responsePromise.get(3000);

        if(returnedJson.findValue("success").asBoolean() == true && !form.hasErrors()){
            SimpleEmail email = new SimpleEmail();
            email.setHostName(Play.application().configuration().getString("smtp.host"));
            email.setSmtpPort(587);

            try {
                email.setFrom(Play.application().configuration().getString("mailFrom"));
                email.setAuthentication(Play.application().configuration().getString("mailFromPass"), Play.application().configuration().getString("mail.smtp.pass"));
                email.setStartTLSEnabled(true);
                email.setDebug(true);
                email.addTo(Play.application().configuration().getString("mail.smtp.user"));
                email.setSubject(subject);
                email.setMsg("Name: " + username + "\n" + "E-mail address: " + mail + "\n\n" + message);
                email.send();

            } catch (EmailException e) {
                Logger.info("Error");e.printStackTrace();
                return redirect(routes.EmailController.sendEmail());
            }
            Logger.info("Message successfully sent");
            flash("success", "Message sent");
            return redirect(routes.ApplicationController.index());
        }
        else{
            flash("error", "You need to verify that you are not a robot!");
            return redirect(routes.EmailController.sendEmail());
        }
    }

}
