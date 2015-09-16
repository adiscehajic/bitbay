package controllers;

import models.Comment;
import models.Product;
import models.User;
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



}
