package controllers;

import helpers.CurrentAdmin;
import models.FAQ;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.adminCreateFaq;
import views.html.admin.adminEditFaq;

import java.util.List;

/**
 * Created by Senadin Botic on 17.10.2015.
 */
@Security.Authenticated(CurrentAdmin.class)
public class FAQController extends Controller {

    // Declaring variable.
    private static final Form<FAQ> FAQForm = Form.form(FAQ.class);

    /**
     * Renders page where administrator can create new FAQ. To create new FAQ user needs to input question and answer.
     *
     * @return Page where admin user can input new FAQ.
     */
    public Result newFAQ() {
        return ok(adminCreateFaq.render(FAQForm));
    }

    /**
     * Renders page where admin user can edit selected FAQ.
     *
     * @param id - Id of the selected FAQ that admin user wants to edit.
     * @return Page where admin user can edit selected FAQ.
     */
    public Result editFAQ(Integer id) {
        FAQ faq = FAQ.getFAQByID(id);
        Form<FAQ> fillForm = FAQForm.fill(faq);
        return ok(adminEditFaq.render(fillForm, faq));
    }

    /**
     * Updates the selected FAQ.
     *
     * @param id - Id of the selected FAQ that admin user wants to edit.
     * @return If the edit is successful renders administrator page whew the list of all FAQs is listed, othervise
     * warning message occurs.
     */
    @RequireCSRFCheck
    public Result updateFAQ(Integer id) {
        // Connecting with FAQ form.
        Form<FAQ> boundForm = FAQForm.bindFromRequest();
        // Checking if the form has errors. If the form has errors renders page where user can edit FAQ, otherwise
        // updates the FAQ with inputed values.
        if (boundForm.hasErrors()) {
            return badRequest(adminCreateFaq.render(boundForm));
        } else {
            // Finding and updating selected FAQ.
            FAQ faq = FAQ.getFAQByID(id);
            faq.question = boundForm.bindFromRequest().field("question").value();
            faq.answer = boundForm.bindFromRequest().field("answer").value();
            faq.update();
        }
        return redirect(routes.AdminController.adminFAQs());
    }

    /**
     * Deletes selected FAQ from database.
     *
     * @param id - Id of the selected FAQ that admin user wants do delete.
     * @return Administrator panel page where all FAQs are listed.
     */
    public Result deleteFAQ(Integer id){
        // Finding and deleting selected FAQ.
        FAQ faq = FAQ.getFAQByID(id);
        faq.delete();
        return redirect(routes.AdminController.adminFAQs());
    }

    /**
     * Enbales administrator user to create new faq.  When creating new faq administrator user needs to input
     * question and answer.
     *
     * @return If creating new faq is successful renders administrator panel where all faq are listed, othervise
     * warning message occurs.
     */
    @RequireCSRFCheck
    public Result saveFAQ(){
        // Connecting with FAQ form.
        Form<FAQ> boundForm = FAQForm.bindFromRequest();
        // Checking if the form has errors. If the form has errors renders page where user input new FAQ, otherwise
        // saves new FAQ with inputed values.
        if(boundForm.hasErrors()) {
            return badRequest(adminCreateFaq.render(boundForm));
        } else {
            FAQ faq = boundForm.get();
            faq.save();
        }
        return redirect(routes.AdminController.adminFAQs());
    }

    /**
     * Validates the form when calls it. If the form has errors returns the JSON that represents all errors that occurs.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */

     public Result validateFormFAQ() {
         Form<FAQ> binded = FAQForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful");
        }
    }
}
