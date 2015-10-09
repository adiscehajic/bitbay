package controllers;

import com.avaje.ebean.Ebean;
import helpers.CurrentBuyerSeller;
import helpers.CurrentUser;
import helpers.SessionHelper;
import models.Message;
import models.User;
import models.UserType;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.adminViewMessage;
import views.html.message.newMessage;
import views.html.message.replyMessage;
import views.html.message.allMessages;
import views.html.admin.adminAllMessages;
import views.html.admin.adminNewMessage;
import views.html.user.userMessages;
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
     * othervise warning message occurs.
     */
    @RequireCSRFCheck
    public Result sendMessage(){
        // Declaring message form.
        Form<Message> boundForm = messageForm.bindFromRequest();
        // Finding current user and declaring him as sender.
        User sender = SessionHelper.currentUser();
        // Checking if form has errors.
        if (boundForm.hasErrors()) {
            if(sender.userType.id == UserType.ADMIN) {
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
        if(sender.userType.id == UserType.ADMIN) {
            return redirect(routes.AdminController.adminNewMessage(""));
        }
        // If the user is buyer or seller rendering application page where sent message is shown.
        return redirect(routes.MessageController.getReceivedMessages());
    }

    @RequireCSRFCheck
    public Result respondMessage(){
        DynamicForm boundForm = Form.form().bindFromRequest();
        String receiverEmail = boundForm.get("receiverEmail");
        String senderEmail = SessionHelper.currentUser().email;
        String message = boundForm.get("messageText");
        String title = boundForm.get("title");
        User receiver = User.getUserByEmail(receiverEmail);
        User sender = User.getUserByEmail(senderEmail);
        User currentUser = SessionHelper.currentUser();

        Message msg = new Message(sender,receiver, title, message);
        Ebean.save(msg);

        List<Message> messages = Message.getReceivedMessages();

        if(currentUser.userType.id == UserType.ADMIN) {
            return ok(adminAllMessages.render(messages));
        } else {
            return ok(allMessages.render(messages));
        }
    }
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result getReceivedMessages(){
     List<Message> recievedMessages = Message.getReceivedMessages();
        return ok(allMessages.render(recievedMessages));
    }

    @Security.Authenticated(CurrentUser.class)
    public Result getSentMessages(){
        List<Message> sentMesages = Message.getSentMessages();
        return ok(allMessages.render(sentMesages));
    }

    @RequireCSRFCheck
    public Result deleteMessage(Integer id){
        Message message = Message.getMessageById(id);
        User currentUser = SessionHelper.currentUser();


        if(currentUser.userType.id == UserType.ADMIN) {
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

    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result newMessage(String email) {
        return ok(newMessage.render(email, messageForm));
    }

    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result replyMessage(Integer id) {
        User user = SessionHelper.currentUser();

        Message message = Message.getMessageById(id);
        message.isRead = true;
        message.update();

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
