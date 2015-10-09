package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static org.junit.Assert.assertNotNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Senadin on 9.10.2015.
 */
public class RecommendationTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newRecommendationTest(){
        User u = new User();
        u.firstName = "Enver";
        Category c = new Category("Water");
        Recommendation r = new Recommendation(u, c);
        r.save();
        assertNotNull(r);

    }
}
