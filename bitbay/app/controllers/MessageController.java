package controllers;

import com.avaje.ebean.Ebean;
import helpers.SessionHelper;
import models.Message;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.message.newMessage;
import views.html.message.replyMessage;
import views.html.message.allMessages;
import views.html.user.userMessages;
import java.util.List;

/**
 * Created by Adnan on 28.09.2015..
 */
public class MessageController extends Controller {

    private static final Form<Message> messageForm = Form.form(Message.class);


    public Result sendMessage(){
        Form<Message> boundForm = messageForm.bindFromRequest();
        User sender = SessionHelper.currentUser();
        String email = boundForm.bindFromRequest().field("receiver").value();
        User receiver = User.getUserByEmail(email);
        String title = boundForm.bindFromRequest().field("title").value();
        String content = boundForm.bindFromRequest().field("message").value();
        Message message = new Message(sender,receiver,title,content);

        if(receiver != null) {
            Ebean.save(message);
        }else{
            flash("receiverDontExistError", "Invalid Email.");
        }
        return redirect(routes.MessageController.getReceivedMessages());
    }

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

    public Result getMessage(Integer id){
        Message message = Message.getMessageById(id);
        return TODO;
    }




    public Result getReceivedMessages(){
     List<Message> recievedMessages = Message.getReceivedMessages();
        return ok(allMessages.render(recievedMessages));
    }

    public Result getSentMessages(){
        List<Message> sentMesages = Message.getSentMessages();
        return ok(allMessages.render(sentMesages));
    }



    public Result deleteMessage(Integer id){
        Message message = Message.getMessageById(id);
        message.delete();

        return  TODO;
        //TODO
    }

    public Result newMessage() {
        return ok(newMessage.render());
    }

    public Result replyMessage(Integer id) {
        List<Message> mess = Message.getConversation(id);
        return ok(replyMessage.render(mess));
    }


}
