package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by Adnan on 18.09.2015..
 */
public class CountryTest extends WithApplication {

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newCountryTest(){
        Country c = new Country();
        c.name = "BiH";
        c.save();

        Country c2 = Country.findCountryByName(c.name);
        c2.save();
    }
}
