package controllers;

import play.mvc.Result;
import play.mvc.Controller;

/**
 * Created by Adnan on 8.9.2015.
 */
public class Categories extends Controller {

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


        return TODO;
    }
}