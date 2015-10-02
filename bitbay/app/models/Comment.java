package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.SqlQuery;
import helpers.SessionHelper;
import play.data.format.Formats;
import play.data.validation.Constraints;

/**
 * Created by Narena Ibrisimovic on 9/15/15.
 */
@Entity
public class Comment extends Model{

    @Id
    public Integer id;
    @Constraints.MaxLength(255)
    @Constraints.Required(message = "Please insert comment title.")
    public String title;
    @Column(columnDefinition = "TEXT")
    @Constraints.Required(message = "Please insert message content.")
    public String text;
    @ManyToOne
    public User user;
    @ManyToOne
    public Product product;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Thumb> thumbs;
    @Formats.DateTime(pattern = "dd/MM/yyyy")
    @Column(columnDefinition = "datetime")
    public Date commentDate = new Date();

    private  static Finder<String, Comment>finder = new Finder<String, Comment>(Comment.class);

    public Comment(String title, String text, User user, Product product){
        this.title = title;
        this.text = text;
        this.user = user;
        this.product = product;
    }


   public static List<Comment> findAllCommentByProduct(Product product){
       List<Comment> commentList = Comment.finder.where().eq("product", product).findList();
       return commentList;
   }

    public static List<Comment> sortCommentByDate(Product product){
        List<Comment> sortedByDate = Comment.finder.where().eq("product", product).orderBy("commentDate desc").findList();
        return sortedByDate;
    }

    public static Comment getCommentById(Integer id) {
        Comment comment = Comment.finder.where().eq("id", id).findUnique();
        return comment;
    }

    public static Boolean hasUserCommented(Product product) {
        return Comment.finder.where().eq("user", SessionHelper.currentUser()).where().eq("product", product).findRowCount() == 0;
    }

}
