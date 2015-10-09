package models;

import org.junit.Before;
import org.junit.Test;
import play.test.WithApplication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;

/**
 * Created by adnan.lapendic on 15.09.2015..
 */
public class CategoryTest extends WithApplication{

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newtCategoryTest() {
    Category c = new Category("c1");
        c.save();
        assertNotNull(c);
    }

    @Test
    public void editCategoryTest(){
    Category c = new Category("c");

        c.save();

        Category c1 = Category.getCategoryById(c.id);

        c1.name = "newCategory";
        c1.save();
        assertNotNull(c1);
    }

    @Test
    public void findNonExistingCategoryTest(){
        Category c = Category.getCategoryById(149);
        assertNull(c);

    }


    @Test
    public void findCountry(){
        Country country = Country.findCountryByName("Sweden");
        assertNull(country);
    }

    @Test
    public void deleteCategory(){
        Category category = new Category("Water");
        category.save();
        Category.getCategoryById(12);
        assertNotNull(category);
        category.delete();
    }

}
