package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Created by Adis Cehajic on 9/16/2015.
 */
@Entity
public class Thumb extends Model {

    @Id
    public Integer id;
    @ManyToOne
    public Comment comment;
    @ManyToOne
    public User user;

    public Boolean isUp;

    private static Finder<String, Thumb> finder = new Finder<>(Thumb.class);

    public Thumb() {}

    public Thumb(Comment comment, User user, Boolean isUp) {
        this.comment = comment;
        this.user = user;
        this.isUp = isUp;
    }

    public static Integer getNumberThumbsUp(Comment comment) {
        List<Thumb> thumbsUp = Thumb.finder.where().eq("comment", comment).where().eq("isUp", true).findList();

        return thumbsUp.size();
    }

    public static Integer getNumberThumbsDown(Comment comment) {
        List<Thumb> thumbsDown = Thumb.finder.where().eq("comment", comment).where().eq("isUp", false).findList();

        return thumbsDown.size();
    }

    public static Thumb getThumbByUserAndComment(User user, Comment comment) {
        Thumb thumb = Thumb.finder.where().eq("user", user).where().eq("comment", comment).findUnique();
        return thumb;
    }

    public String toString() {
        return String.format("%d - %s - %s", id, user.firstName, comment.title);
    }


}
