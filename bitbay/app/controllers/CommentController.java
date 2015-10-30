package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.SessionHelper;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.filters.csrf.RequireCSRFCheck;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Adis Cehajic on 9/15/15.
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
    @RequireCSRFCheck
    public Result saveComment(Integer id){
        // Declaring category form.
        Form<Comment> boundForm = commentForm.bindFromRequest();
        if (boundForm.hasErrors()) {
            return redirect(routes.ProductController.getProduct(id));
        }
        // Creating new comment and saving it into database.
        Comment comment = boundForm.get();
        Product product = Product.getProductById(id);
        User user = SessionHelper.currentUser();
        comment.product = product;
        comment.user = user;
        comment.commentDate = new Date();
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
                    // Declaring a list of the likes and dislikes.
                    List<Integer> list = Thumb.getAllThumbsUpDown(comment);
                    // Converting list into a JSON
                    JsonNode object = Json.toJson(list);
                    // Returning JSON object.
                    return ok(object);
                } else {
                    // If thumb does not exist, creating new thumb and saving it into database.
                    Thumb t = new Thumb(comment, user, thumb);
                    t.save();
                    // Declaring a list of the likes and dislikes.
                    List<Integer> list = Thumb.getAllThumbsUpDown(comment);
                    // Converting list into a JSON
                    JsonNode object = Json.toJson(list);
                    // Returning JSON object.
                    return ok(object);
                }
            }
        }
        return ok();
    }

    /**
     * Validates the form when the AJAX calls it. If the form has errors returns the JSON object that represents all
     * errors that occurs. If there is no errors returns ok.
     *
     * @return JSON object that represents all errors that occurs, otherwise returns ok.
     */
    public Result validateFormComment() {
        Form<Comment> binded = commentForm.bindFromRequest();
        if (binded.hasErrors()) {
            return badRequest(binded.errorsAsJson());
        } else {
            return ok("Validation successful.");
        }
    }

}