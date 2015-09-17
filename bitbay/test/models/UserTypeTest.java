package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import models.UserType;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Created by Adnan on 18.09.2015..
 */
public class UserTypeTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newUserTypeTest(){
        UserType u = new UserType();
        u.id = 1;
        u.name = "Admin";
        UserType ut = UserType.getUserTypeById(u.id);

    }
}
