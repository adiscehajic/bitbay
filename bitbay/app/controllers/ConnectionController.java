package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import helpers.CommonHelpers;
import helpers.ConstantsHelper;
import models.Image;
import models.Product;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by Adis Cehajic on 11/11/2015.
 */
public class ConnectionController extends Controller {

    /**
     * Saves the course received from bitClassroom web application. The information about the course are received and
     * saved as JSON. After the JSON object is received, it creates and saves new course into database. If the course
     * has image it uploads image on the cloudinary and saves the cloudinary URL into database.
     *
     * After the new course is saved into database it returns to the bitClassroom web application the JSON object that
     * contains the token.
     *
     * @return JSON object that contains the new saved course token.
     */
    public Result saveCourse() {
        // Checking if the request has security key.
        if (request().getHeader("secret_key").equals(ConstantsHelper.BIT_CLASSROOM_KEY)) {
            // Declaring JSON object that will contain JSON object from request.
            JsonNode json = request().body().asJson();
            // Declaring string variables that represent course attributes.
            String name = json.findPath("course_name").textValue();
            String description = json.findPath("course_description").textValue();
            String price = json.findPath("course_price").textValue();
            String quantity = json.findPath("course_quantity").textValue();
            String imageUrl = json.findPath("course_image_url").textValue();
            // Creating new course and saving him into database.
            Product course = new Product(CommonHelpers.bitclassroomUser(), name, description, "BitClassroom",
                    CommonHelpers.getBitClassroomCategory(), Double.parseDouble(price), Integer.parseInt(quantity),
                    ConstantsHelper.FIXED_PRICE);
            course.save();
            // Calling method that reads image from URL, uploads image file on cloudinary and saves image cloudinary
            // URL into database.
            Image.savingImageFromUrl(imageUrl, course);
            // Declaring JSON object that contain premiumId.
            JsonNode object = Json.toJson(course.id + "bitbay");
            // Returning created JSON object.
            return ok(object);
        }
        return badRequest(views.html.notFound.render());
    }
}
