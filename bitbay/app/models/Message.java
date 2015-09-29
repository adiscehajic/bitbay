package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
     * @param reiciver - User
     * @return - List of messages
     */
    public static List<Message> getAllMessages(User receiver){
        List<Message> messages = finder.where().eq("receiver", receiver).findList();
        return messages;
    }


}
