package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.data.validation.Constraints;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.F;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.contactUs;

import javax.inject.Inject;

/**
 * Created by Medina Banjic on 05/10/15.
 */
public class EmailController  extends Controller{

    // Declaring variable that will represent contact form.
    private static final Form<Contact> contactForm = Form.form(Contact.class);

    /**
     * Renders contactUs.html page with a form for sending a message from user to application administrator.
     *
     * @return ContactUs page of the application.
     */
    public Result contactUs() {
        return ok(contactUs.render(contactForm));
    }

    /**
     * This method gets inputs from contact us form, verifies recaptcha and sends email to bitbayservice@gmail.com
     * from userfeedback.bitbay@gmail.com.
     *
     * @return
     */
    @Inject WSClient ws;
    @RequireCSRFCheck
    public Result sendEmail() {
        // Connecting with contact us form.
        Form<Contact> boundForm = contactForm.bindFromRequest();
        // Checking if the form has errors. If the form has errors renders contact us page.
        if(boundForm.hasErrors()){
            return badRequest(contactUs.render(boundForm));
        }
        // Getting all values inputed into fields and creating new contact.
        Contact newContact = boundForm.get();
        // Declaring and checking recaptcha.
        String grecaptcha = request().body().asFormUrlEncoded().get("g-recaptcha-response")[0];
        WSRequest rq = ws.url("https://www.google.com/recaptcha/api/siteverify");
        rq.setQueryParameter("secret", "6LcoOxETAAAAAPKT55QwSa8RbN9A0keCQhxZFBiN");
        rq.setQueryParameter("response", grecaptcha);

        F.Promise<JsonNode> responsePromise = rq.get().map(response -> {
            return response.asJson();
        });
        JsonNode returnedJson = responsePromise.get(3000);
        // If the recaptcha is correct creating and sending new mail.
        if(returnedJson.findValue("success").asBoolean() == true){
            // Declaring and initializing new email.
            SimpleEmail email = new SimpleEmail();
            email.setHostName(Play.application().configuration().getString("smtp.host"));
            email.setSmtpPort(587);

            try {
                email.setFrom(Play.application().configuration().getString("mailFrom"));
                email.setAuthentication(Play.application().configuration().getString("mailFromPass"), Play.application().configuration().getString("mail.smtp.pass"));
                email.setStartTLSEnabled(true);
                email.setDebug(true);
                email.addTo(Play.application().configuration().getString("mail.smtp.user"));
                email.setSubject(newContact.subject);
                email.setMsg("Name: " + newContact.name + "\n" + "E-mail address: " + newContact.email + "\n\n" + newContact.message);
                email.send();

            } catch (EmailException e) {
                Logger.info("Error");
                e.printStackTrace();
                return redirect(routes.EmailController.sendEmail());
            }
            Logger.info("Message successfully sent");
            flash("success", "Message sent");
            return redirect(routes.EmailController.sendEmail());
        }
        else{
            flash("error", "You need to verify that you are not a robot!");
            return badRequest(contactUs.render(boundForm));
        }
    }

    /**
     * Created by Senadin Botic 12/10/15.
     *
     * Inner class with four variables that are required.
     */
    public static class Contact {
        // Declaring properties.
        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert your name.")
        @Constraints.Pattern(value = "^[a-z A-Z]+$", message = "Name can't contain digits.")
        public String name;

        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert your email.")
        @Constraints.Email(message = "Valid email is required.")
        public String email;

        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert subject.")
        public String subject;

        @Constraints.MaxLength(255)
        @Constraints.Required(message = "Please insert your message.")
        public String message;

    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormContactUs() {
        Form<Contact> binded = contactForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }
}
