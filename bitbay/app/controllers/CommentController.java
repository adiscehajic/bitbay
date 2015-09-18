package controllers;

import models.Comment;
import models.Product;
import models.Thumb;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import com.avaje.ebean.Ebean;

import play.mvc.Result;


/**
 * Created by ibrisimovicnarena on 9/15/15.
 */
public class CommentController extends Controller {
    private static Form<Comment> commentForm = Form.form(Comment.class);

    public Result saveComment(Integer id){
        Form<Comment> boundForm = commentForm.bindFromRequest();

        String title = boundForm.bindFromRequest().field("title").value();
        String text = boundForm.bindFromRequest().field("comment").value();
        Product product = Product.getProductById(id);
        User user = User.getUserByEmail(session("email"));

        Comment c = new Comment(title,text, user, product);
        Ebean.save(c);

        return redirect(routes.ProductController.getProduct(id));

    }


    public Result saveThumbUp() {
        DynamicForm form = Form.form().bindFromRequest();

        String commentId = form.data().get("comm");
        String thumbString = form.data().get("thumb");

        Logger.info(commentId);

        if (commentId != null) {
            Comment comment = Comment.getCommentById(Integer.parseInt(commentId));
            User user = User.getUserByEmail(session().get("email"));
            Boolean thumb = Boolean.parseBoolean(thumbString);

            Thumb tmb = Thumb.getThumbByUserAndComment(user, comment);

            if (tmb != null){
                tmb.isUp = thumb;
                tmb.update();
                return ok("2");
            }else{
                Thumb t = new Thumb(comment, user, thumb);
                t.save();
                return ok("1");
            }
        }
        return ok("1");
    }



}
