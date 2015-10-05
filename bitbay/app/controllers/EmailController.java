package controllers;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by banjic on 05/10/15.
 */
public class EmailController  extends Controller{

    @Inject WSClient ws;
    @RequireCSRFCheck
    public Result sendEmail() {
        DynamicForm form = Form.form().bindFromRequest();


        String user_name = form.get("name");
        String mail = form.get("email");
        String subject = form.get("subject");
        String message = form.get("message");
        Logger.info(user_name + "----" + mail + "-----" + subject + "----" +  message);
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
            email.setMsg(user_name + "\n" + mail + "\n\n"+subject +"\n" + message);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return redirect(routes.ApplicationController.index());
    }
}
