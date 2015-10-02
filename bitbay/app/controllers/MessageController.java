package controllers;

import com.avaje.ebean.Ebean;
import helpers.CurrentBuyerSeller;
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
     * @return
     */
    @RequireCSRFCheck
    public Result sendMessage(){
        Form<Message> boundForm = messageForm.bindFromRequest();
        User sender = SessionHelper.currentUser();
        String email = boundForm.bindFromRequest().field("receiver").value();
        User receiver = User.getUserByEmail(email);
        String title = boundForm.bindFromRequest().field("title").value();
        String content = boundForm.bindFromRequest().field("message").value();
        Message message = new Message(sender,receiver,title,content);

        if(sender.userType.id == 1 && receiver == null) {
            flash("receiverDontExistError", "User does not exist.");
            return badRequest(adminNewMessage.render("", boundForm));
        }
        if(receiver == null) {
            flash("receiverDontExistError", "User does not exist.");
            return badRequest(newMessage.render("", boundForm));
        }

        flash("messageSentSuccess", "Message successfully sent.");
        message.save();

        if(sender.userType.id == 1) {
            return redirect(routes.AdminController.adminNewMessage(""));
        }

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

        Message msg = new Message(sender,receiver, title, message);
        Ebean.save(msg);

        List<Message> messages = Message.getSentMessages();
        return ok(allMessages.render(messages));
    }
    @Security.Authenticated(CurrentBuyerSeller.class)
    public Result getReceivedMessages(){
     List<Message> recievedMessages = Message.getReceivedMessages();
        return ok(allMessages.render(recievedMessages));
    }

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
        List<Message> conv = Message.getConversation(id);
        User user = SessionHelper.currentUser();

        for(int i = 0; i < conv.size(); i++) {
            if(conv.get(i).receiver.id == user.id) {
                conv.get(i).isRead = true;
                conv.get(i).update();
            }
        }

        List<Message> messages = Message.getConversation(id);
        return ok(replyMessage.render(messages));
    }

}
