package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    public Comment comment;
    @ManyToOne
    @JsonBackReference
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

    public static List<Integer> getAllThumbsUpDown(Comment comment) {

        List<Integer> allThumbs = new ArrayList<>();

        Integer thumbsUp = getNumberThumbsUp(comment);
        Integer thumbsDown = getNumberThumbsDown(comment);

        allThumbs.add(thumbsUp);
        allThumbs.add(thumbsDown);

        return allThumbs;
    }

    public static List<Comment> getMostLikedComment(Product product) {

        List<Comment> commentList = Comment.findAllCommentByProduct(product);
        int maxFirst = 0;
        Comment commentFirst = null;
        int maxSecond = 0;
        Comment commentSecond = null;

        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
            List<Thumb> comments = Thumb.finder.where().eq("comment", comment).where().eq("isUp", true).findList();
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
