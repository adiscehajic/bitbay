package models;

import com.avaje.ebean.Model;
import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.ArrayList;
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

    public static List<Comment> getMostLikedComment(Product product) {
     //   Integer commentNumber = Thumb.finder.where().eq("is_up", 1).orderBy("comment_id").find();

        List<Comment> commentList = Comment.findAllCommentByProduct(product);
        Logger.info("Broj comentara produkta " + product.id + " je " + commentList.size());
        int maxFirst = 0;
        Comment commentFirst = null;
        int maxSecond = 0;
        Comment commentSecond = null;

        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
            List<Thumb> comments = Thumb.finder.where().eq("comment", comment).where().eq("isUp", true).findList();
            Logger.info("Velicina niza je: " + comments.size());
            if (comments.size() > maxFirst) {
                maxFirst = comments.size();
                commentFirst = comment;
            } else if (comments.size() != maxFirst && comments.size() > maxSecond) {
                maxSecond = comments.size();
                commentSecond = comment;
            }
        }

        Logger.info("Max prvi je: " + maxFirst);
        Logger.info("Max prvi je: " + maxSecond );
        List<Comment> topComments = new ArrayList<>();
        if (commentFirst != null) {
            topComments.add(commentFirst);
        }
        if (commentSecond != null) {
            topComments.add(commentSecond);
        }

        return topComments;
    }

    public String toString() {
        return String.format("%d - %s - %s", id, user.firstName, comment.title);
    }


}
