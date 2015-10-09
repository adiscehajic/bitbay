package models;

import play.test.*;
import org.junit.*;

import java.util.List;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

/**
 * Created by Denis Cehajic on 9/15/2015.
 */
public class ProductTest extends WithApplication {

    @Before
    public void configureDatabase() {
        fakeApplication(inMemoryDatabase());
    }

    @Test
    public void testDatabase() {
        List<Product> products = Product.findAll();

        assertNotNull(products);
    }

    @Test
    public void createNewProduct() {
        Product p = new Product(new User(), "Bag", "Small", "Alma Ras", new Category("Fashion"), 15.0, 15, "Auction");

        p.save();
    }


    @Test
    public void testLoadingProduct() {

        Product p = new Product();

        p.id = 1;
        p.user = new User();
        p.category = new Category("Electronics");
        p.name = "3310";
        p.price = 200.00;
        p.quantity = 2;

        p.save();

        Product loaded = Product.getProductById(1);

        assertNotNull(loaded);
    }

    @Test
    public void testProductSearchByUserEmail() {

        User u = new User();

        u.id = 1;
        u.firstName = "Adis";
        u.lastName = "Cehajic";
        u.email = "adis@bitcamp.ba";
        u.password = "12345678";
        u.userType = new UserType();

        List<Product> founded = Product.findAllProductsByUser(u);

        assertNotNull(founded);
    }

    @Test
    public void testProductsSearchByCategory() {

        Category c = new Category("Electronics");

        Product p = new Product();

        p.id = 1;
        p.user = new User();
        p.category = c;
        p.name = "3310";
        p.price = 200.00;
        p.quantity = 2;

        p.save();

        List<Product> founded = Product.findAllProductsByCategory(c);

        assertNotNull(founded);
    }

    @Test
    public void testFindNonExistingProduct(){
        Product p = Product.getProductById(123);
        assertNull(p);
    }


}
