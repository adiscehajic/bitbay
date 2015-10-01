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

    public Boolean receiverVisible;
    public Boolean senderVisible;

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
        this.receiverVisible = true;
        this.senderVisible = true;
    }

    /**
     * This method finds message by received Id and returns that message.
     * @param id
     * @return - Message
     */
    public static Message getMessageById(Integer id) {
        Message message = Message.finder.where().eq("id", id).findUnique();
        return message;
    }

    /**
     * This method gets user from session, gets all messages from database, where this user is reciever and only messages
     * that are visible to him (messages that havent been already deleted from his "Inbox"/list of received messages on
     * his profile. This method also returns that List of messages.
     * @return - List of messages
     */
    public static List<Message> getReceivedMessages(){
        User user = SessionHelper.currentUser();
        List<Message> receivedMessages = finder.where().eq("receiver", user).eq("receiverVisible", true).findList();

        return receivedMessages;
    }

    public String toString() {
        return String.format("Sender: %s -- Receiver: %s", sender.email, receiver.email);
    }

    /**
     * This method receives message id, finds message by that received id, get two users, receiver and sender, from that message. Gets list
     * of messages between those two users from database (sender/receiver) and returns that list.
     * @param id
     * @return
     */
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


    /**
     * This method gets user from session, and get list of messages from database where he is sender, but only messages
     * that are visible to him (that havent been deleted from its "Sent"/List of sent messages on his profile).
     * This method returns that list of messages.
     * @return
     */
    public static List<Message> getSentMessages(){
        User user = SessionHelper.currentUser();
        List<Message> sentMessages = Message.finder.where().eq("sender", user).eq("senderVisible", true).findList();

        return sentMessages;
    }



}
