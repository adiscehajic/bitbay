package models;

import play.test.*;
import org.junit.*;
import models.User;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;
/**
 * Created by neo on 9/15/15.
 */
public class UserTest extends WithApplication {

    @Before
    public void createDatabase() {
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testNullPointer() {
        String s = "John";
        assertNotNull(s);
    }

    @Test
    public void createSaveUser() {
        User u1 = new User();
        u1.firstName = "John";
        u1.lastName = "Doe";
        u1.email = "jd@bitcamp.ba";
        u1.password = "123456789";
        u1.city = "Sarajevo";
        u1.address = "Titova bb";
        u1.zip = 71000;
        u1.id = 10;
        u1.save();
    }

    @Test
    public void testNoneExistingUser() {
        User u = User.findById(99);
        assertNull(u);
    }

    @Test
    public void testNoneExistingEmail(){
        User u = User.getUserByEmail("Someone@bitcamp.ba");
        assertNull(u);
    }

    @Test
    public void testDatabase() {
        List<User> userList = User.findAll();
        assertNotNull(userList);
    }


}
