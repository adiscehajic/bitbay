package controllers;

import helpers.SessionHelper;
import models.Message;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
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

    public Result getMessage(Integer id){
        Message message = Message.getMessageById(id);
        return TODO;
    }

    public Result getAllReceivedMessages(){
       // User currentUser = SessionHelper.currentUser();
       // List<Message> mesages = Message.getAllMessages(currentUser);
        return ok(allMessages.render());
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

    public Result replyMessage() {
        return ok(replyMessage.render());
    }
}
