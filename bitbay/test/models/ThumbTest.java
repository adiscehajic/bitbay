package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Adnan on 18.09.2015..
 */
public class ThumbTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newThumbsTest(){
        User u = new User();
        Product p = new Product();
        Comment c = new Comment("newComment","Comment text", u, p);

        Thumb t = new Thumb(c,u, true);

        t.save();
    }
}
