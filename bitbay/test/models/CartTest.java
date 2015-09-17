package models;

import com.avaje.ebean.Ebean;
import controllers.routes;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static org.junit.Assert.*;

/**
 * Created by Adnan on 17.09.2015..
 */
public class CartTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void findCartByUserTest(){
        UserType t = new UserType();
        t.id = 1;
        t.name = "Seller";

        User u = new User(1, "Jon", "Snow", "got@north.ba", "WinterIsComing",t);

        Cart c = Cart.findCartByUser(u);
        assertNull(c);
    }

}
