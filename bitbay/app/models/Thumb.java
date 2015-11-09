package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

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

    /**
     * Constructor for Thumb
     */
    public Thumb(Comment comment, User user, Boolean isUp) {
        this.comment = comment;
        this.user = user;
        this.isUp = isUp;
    }

    /**
     * Method that finds number of positive impressions on the given comment
     * @return - Number of likes
     */
    public static Integer getNumberThumbsUp(Comment comment) {
        List<Thumb> thumbsUp = Thumb.finder.where().eq("comment", comment).where().eq("isUp", true).findList();

        return thumbsUp.size();
    }

    /**
     * Method that finds number of negative impressions on the given comment
     * @return - Number of negative likes
     */
    public static Integer getNumberThumbsDown(Comment comment) {
        List<Thumb> thumbsDown = Thumb.finder.where().eq("comment", comment).where().eq("isUp", false).findList();

        return thumbsDown.size();
    }

    /**
     * This method find users impression on a comment. like/dislike
     * @return
     */
    public static Thumb getThumbByUserAndComment(User user, Comment comment) {
        Thumb thumb = Thumb.finder.where().eq("user", user).where().eq("comment", comment).findUnique();
        return thumb;
    }

    /**
     * Method that return a list of all impressions on a single comment
     * @return
     */
    public static List<Integer> getAllThumbsUpDown(Comment comment) {

        List<Integer> allThumbs = new ArrayList<>();

        Integer thumbsUp = getNumberThumbsUp(comment);
        Integer thumbsDown = getNumberThumbsDown(comment);

        allThumbs.add(thumbsUp);
        allThumbs.add(thumbsDown);

        return allThumbs;
    }

    /**
     * This method goes through DB and finds all comments on a single product.
     * List of comments is sorted by positive impressions(likes) and two top comments are
     * displayed above all other comments
     * @return - Top two comments
     */
    public static List<Comment> getMostLikedComment(Product product) {

        List<Comment> commentList = Comment.findAllCommentByProduct(product);
        int maxFirst = 0;
        Comment commentFirst = null;
        int maxSecond = 0;
        Comment commentSecond = null;

        //Sorting comments by likes
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

        //Creating new list of top comments
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
