package controllers;

<<<<<<< HEAD
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import java.lang.*;

import com.avaje.ebean.Ebean;
import models.*;

=======
import play.mvc.Result;
import play.mvc.Controller;
>>>>>>> 89e26e0d4eb85f455eac3c0f54dc325cea0857db

/**
 * Created by Adnan on 8.9.2015.
 */
<<<<<<< HEAD
public class Categories extends Controller{
=======
public class Categories extends Controller {
>>>>>>> 89e26e0d4eb85f455eac3c0f54dc325cea0857db

    /**
     * Method for creating new Category
     * @return
     */
    public Result newCategory(){

        return TODO;
    }

    /**
     * Method for makeing changes on single Category
     * @param id - ID of selected Category
     * @return
     */
    public Result editCategory(Integer id){

        return TODO;
    }

    /**
     * This method delete selected Category from database
     * @param id - ID of a selected Category
     * @return
     */
    public Result deleteCategory(Integer id){

        return TODO;
    }

    /**
     * This method is used to save new category to database
     */
    public Result saveCategory(){

        return TODO;
    }

    /**
     * This method is used to list all users from database
     * @return - List of users
     */
    public Result list(){

<<<<<<< HEAD
        return TODO;
    }
}
=======

        return TODO;
    }
}
>>>>>>> 89e26e0d4eb85f455eac3c0f54dc325cea0857db
