package models;

import java.util.*;

import controllers.CategoryController;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.Category;
import models.User;
import org.junit.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.Logger;
import play.Play;
import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static org.junit.Assert.assertNull;
import static play.test.Helpers.*;
import static org.junit.Assert.*;

/**
 * Created by adnan.lapendic on 15.09.2015..
 */
public class CategoryTest extends WithApplication{

    @Before
    public void configureDatabase(){
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void newCategoryTest() {
    Category c = new Category("c1", null);
        c.save();
        assertNotNull(c);
    }

    @Test
    public void editCategoryTest(){
    Category c = new Category("c", null);

        c.save();

        Category c1 = Category.getCategoryById(c.id);

        c1.name = "newCategory";
        c1.save();
        assertNotNull(c1);
    }

/*
    @Test
    public void deleteCategoryTest(){
        Category c = new Category("Moto");
       c.save();

       c.delete();
       assertNull(c);
    }
    */
}
