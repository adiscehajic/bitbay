package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import helpers.SessionHelper;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adnan on 28.09.2015..
 */
@Entity
public class Message extends Model{

    @Id
    public Integer id;
    @ManyToOne
    public User sender;
    @ManyToOne
    public User receiver;
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date date = new Date();
    public String title;
    @Column(columnDefinition = "TEXT")
    public String message;

    public Boolean isRead;

    public static Finder<Integer, Message> finder = new Finder<Integer, Message>(Message.class);

    /**
     * Constructor for new message.
     * @param sender - Who is sending message
     * @param receiver - Who is receiving mesage
     * @param title - Message title
     * @param message - Message content
     */
    public Message (User sender, User receiver, String title, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.title = title;
        this.message = message;
        this.isRead = false;
    }

    /**
     * This method find return one message by ID
     * @param id
     * @return - Message
     */
    public static Message getMessageById(Integer id) {
        Message message = Message.finder.where().eq("id", id).findUnique();
        return message;
    }

    /**
     * This method returns a list of all reicived messages
     * @return - List of messages
     */
    public static List<Message> getReceivedMessages(){
        User user = SessionHelper.currentUser();
        List<Message> receivedMessages = finder.where().eq("receiver", user).findList();

        return receivedMessages;
    }

    public String toString() {
        return String.format("Sender: %s -- Receiver: %s", sender.email, receiver.email);
    }

    public static List<Message> getConversation(Integer id){
        Message message = Message.getMessageById(id);
        User sender = User.findById(message.sender.id);
        User receiver = User.findById(message.receiver.id);

        List<Message> messagesOne = Message.finder.where().eq("sender", sender).where().eq("receiver", receiver).findList();
        List<Message> messagesTwo= Message.finder.where().eq("sender", receiver).where().eq("receiver", sender).findList();

        List<Message> messages = new ArrayList<>();
        messages.addAll(messagesOne);
        messages.addAll(messagesTwo);

        return messages;

    }


    public static List<Message> getSentMessages(){
        User user = SessionHelper.currentUser();
        List<Message> sentMessages = Message.finder.where().eq("sender", user).findList();

        return sentMessages;
    }



}
