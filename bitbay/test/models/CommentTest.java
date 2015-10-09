package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Adnan on 18.09.2015..
 */
public class CommentTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

   @Test
    public void newCommentTest(){
       User u = new User();
       Product p = new Product();
       Comment c = new Comment("newComment","Comment text", u, p);
       c.save();
   }


    @Test
    public void findCommentByIdTest(){
        User u = new User();
        Product p = new Product();
        Comment c = new Comment("newComment","Comment text", u, p);
        c.id = 1;
        c.save();

        Comment c2 = Comment.getCommentById(c.id);
        c2.save();

    }

}
