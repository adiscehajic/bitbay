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
public class MessageTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void createMessage(){
        Message m = new Message();
        m.title = "Job";
        m.message = "Worker needed";
        m.save();
        assertNotNull(m);

    }

    @Test
    public  void deleteMessage(){
        Message m1 = new Message();
        m1.title = "Job";
        m1.message = "Worker needed";
        m1.save();
        assertNotNull(m1);
        m1.delete();
        assertNotNull(m1);
    }
}
