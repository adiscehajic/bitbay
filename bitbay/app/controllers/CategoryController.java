package controllers;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.signup;
import views.html.signIn;
import java.lang.*;
import java.util.List;

import com.avaje.ebean.Ebean;
import models.*;


/**
 * Created by Adnan on 8.9.2015.
 */


public class CategoryController extends Controller {

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
        List<Category> categories = Category.findAll();
        return TODO;
    }
}

