package controllers;

import helpers.SessionHelper;
import models.Comment;
import models.Product;
import models.Thumb;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * Created by Narena Ibrisimovic on 9/15/15.
 */
public class CommentController extends Controller {

    // Declaring comment form.
    private static Form<Comment> commentForm = Form.form(Comment.class);

    /**
     * Enables user to create new comment. Only user that are buyers can add comment. When creating new comment user
     * needs to input comment name and the content of a comment. If the inputed comment name and content are not in
     * the rigth format warning message occurs.
     *
     * @param id - Id of the product on which user wants to add a comment.
     * @return If create of the new comment is successful renders product profil page where added comment can be seen,
     * othervise warning message occurs.
     */
    public Result saveComment(Integer id){
        // Declaring category form.
        Form<Comment> boundForm = commentForm.bindFromRequest();
        // Declaring string variables that represents inputed name and content of the comment.
        String title = boundForm.bindFromRequest().field("title").value();
        String text = boundForm.bindFromRequest().field("comment").value();
        // Declaring product and user.
        Product product = Product.getProductById(id);
        User user = SessionHelper.currentUser();
        // Checking if a name or content of a comment are empty.
        try{
            if(title.isEmpty()){
                flash("saveCommentEmptyTitleError", "Please enter comment title.");
                throw new Exception();
            }
            if(text.isEmpty()){
                flash("saveCommentEmptyTextError", "Please enter your comment");
                throw new Exception();
            }
        }catch (Exception e){
            Logger.info("ERROR: Registration failed.\n" + e.getStackTrace() + " -- Msg: " + e.getMessage());
            return redirect(routes.ProductController.getProduct(id));
        }
        // Creating new comment and saving it into database.
        Comment comment = new Comment(title, text, user, product);
        comment.save();
        // Redirecting to the product profile page.
        return redirect(routes.ProductController.getProduct(id));
    }

    /**
     * Enables user to like or dislike the comment. User can only one time like or dislike comment. If the first time
     * user likes comment, next time he can only dislike comment.
     *
     * @return Number 1 if the user has never liked or disliked comment, and number 2 if user has already liked or
     * disliked comment.
     */
    public Result saveThumbUp() {
        // Declaring dynamic form.
        DynamicForm form = Form.form().bindFromRequest();
        // Declaring string variables that represent comment id and thumb. If the thumb is true, comment is liked and
        // if it is false comment is disliked.
        String commentId = form.data().get("comm");
        String thumbString = form.data().get("thumb");
        // Checking if comment exists.
        if (commentId != null) {
            // Declaring variables.
            Comment comment = Comment.getCommentById(Integer.parseInt(commentId));
            User user = SessionHelper.currentUser();
            Boolean thumb = Boolean.parseBoolean(thumbString);
            // Checking if user exists.
            if (user != null) {
                // Declaring like or dislike.
                Thumb tmb = Thumb.getThumbByUserAndComment(user, comment);
                // Checking if thumb already exists.
                if (tmb != null) {
                    // If thumb exists than updating existing thumb.
                    tmb.isUp = thumb;
                    tmb.update();
                    return ok("2");
                } else {
                    // If thumb does not exist, creating new thumb and saving it into database.
                    Thumb t = new Thumb(comment, user, thumb);
                    t.save();
                    return ok("1");
                }
            }
        }
        return ok("1");
    }
}