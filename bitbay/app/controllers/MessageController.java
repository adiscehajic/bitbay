package controllers;

import com.avaje.ebean.Ebean;
import helpers.ConstantsHelper;
import helpers.CurrentBuyerSeller;
import helpers.CurrentUser;
import helpers.SessionHelper;
import models.Message;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.message.newMessage;
import views.html.message.replyMessage;
import views.html.message.allMessages;
import views.html.admin.adminAllMessages;
import views.html.admin.adminNewMessage;

import java.util.List;

/**
 * Created by Adnan Lapendic on 28.09.2015..
 */
public class MessageController extends Controller {

    private static final Form<Message> messageForm = Form.form(Message.class);

    /**
     * This method should collect all data from our request, where should be included: email of user that receives
     * message, title of message, and content of message. From all collected data we create Message object into base.
     * If there is no receiver, throw different errors if sender is admin or user. If all required data is collected
     * message will be saved into database and page will redirect us on another page, depending are we admin or just a
     * user.
     *
     * @return If the sending message is successful renders page where sent message and other reply messages are shown,
     * otherwise warning message occurs.
     */
    @RequireCSRFCheck
    public Result sendMessage(){
        // Declaring message form.
        Form<Message> boundForm = messageForm.bindFromRequest();
        // Finding current user and declaring him as sender.
        User sender = SessionHelper.currentUser();
        // Checking if form has errors.
        if (boundForm.hasErrors()) {
            if(sender.userType.id == ConstantsHelper.ADMIN) {
                return badRequest(adminNewMessage.render("", boundForm));
            }
            return badRequest(newMessage.render("", boundForm));
        }
        // Finding receiver in database.
        User receiver = User.getUserByEmail(boundForm.data().get("receiverEmail"));
        // Creating new message and adding values.
        Message message = boundForm.get();
        message.sender = sender;
        message.receiver = receiver;
        message.isRead = false;
        message.receiverVisible = true;
        message.senderVisible = true;
        // Saving message into database.
        message.save();
        // If the user is administrator rendering administrator panel page where sent message is shown.
        if(sender.userType.id == ConstantsHelper.ADMIN) {
            return redirect(routes.AdminController.adminNewMessage(""));
        }
        // If the user is buyer or seller rendering application page where sent message is shown.
        return redirect(routes.MessageController.getReceivedMessages());
    }

    /**
     * Renders page where all received user messages are listed. User can view and delete all messages that are in
     * inbox.
     *
      * @return Page where all user received messages are listed.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result getReceivedMessages(){
        // Finding all received messages.
        List<Message> recievedMessages = Message.getReceivedMessages();
        return ok(allMessages.render(recievedMessages));
    }

    /**
     * Renders page where all sent user messages are listed. User can view and delete all messages that are in
     * sentbox.
     *
     * @return Page where all user sent messages are listed.
     */
    @Security.Authenticated(CurrentUser.class)
    public Result getSentMessages(){
        // Finding all sent messages.
        List<Message> sentMesages = Message.getSentMessages();
        return ok(allMessages.render(sentMesages));
    }

    /**
     * Deletes selected message from database.
     *
     * @param id Id of the selected message that user wants to delete.
     * @return Page where all messages are shown.
     */
    @RequireCSRFCheck
    public Result deleteMessage(Integer id){
        Message message = Message.getMessageById(id);
        User currentUser = SessionHelper.currentUser();

        if(currentUser.userType.id == ConstantsHelper.ADMIN) {
            if (message.receiver.id == currentUser.id && message.receiverVisible == true) {
                message.receiverVisible = false;
                message.update();

                List<Message> messages = Message.getReceivedMessages();
                return ok(adminAllMessages.render(messages));
            } else {
                message.senderVisible = false;
                message.update();

                List<Message> messages = Message.getSentMessages();
                return ok(adminAllMessages.render(messages));
            }
        } else {
            if (message.receiver.id == currentUser.id && message.receiverVisible == true) {
                message.receiverVisible = false;
                message.update();

                List<Message> messages = Message.getReceivedMessages();
                return ok(allMessages.render(messages));
            } else {
                message.senderVisible = false;
                message.update();

                List<Message> messages = Message.getSentMessages();
                return ok(allMessages.render(messages));
            }
        }
    }

    /**
     * Renders page where user can send new message to the other users. User needs to input receivers email and
     * content of the message. If the email and content are not inputed, prints error message.
     *
     * @param email Receivers email.
     * @return Page where user can send other user message.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result newMessage(String email) {
        return ok(newMessage.render(email, messageForm));
    }

    /**
     * Renders page where message and all information about message are shown. If the message is received user can
     * reply to received message.
     *
     * @param id Id of the selected message.
     * @return Page where selected message is shown.
     */
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result replyMessage(Integer id) {
        // Finding current user.
        User user = SessionHelper.currentUser();
        // Finding selected message.
        Message message = Message.getMessageById(id);
        // Checking if the user is sender and updating message to read.
        if (!message.sender.equals(user)) {
            message.isRead = true;
            message.update();
        }
        return ok(replyMessage.render(message));
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormMessage() {
        Form<Message> binded = messageForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }
}