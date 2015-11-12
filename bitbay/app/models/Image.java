package models;

import com.avaje.ebean.Model;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import helpers.SessionHelper;
import org.apache.commons.io.FileUtils;
import play.Logger;

import javax.persistence.*;
import java.io.File;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Adis Cehajic on 9/10/2015.
 * Edited by neo on 9/21/2015.
 */
@Entity
public class Image extends Model {

    // Declaring variable.
    private static Finder<String, Image> finder =
            new Finder<>(Image.class);

    public static Cloudinary cloudinary;

    @Id
    public Integer id;

    public String public_id;

    public String secret_image_url;

    public String image_url;

    @ManyToOne
    @JsonBackReference
    public Product product;

    @OneToOne
    public User user;


    /**
     * Constructor
     */
    public static Image create(String public_id, String image_url, String secret_image_url) {
        Image img = new Image();
        img.public_id = public_id;
        img.image_url = image_url;
        img.secret_image_url = secret_image_url;
        img.save();
        return img;
    }

    /**
     * Method that return image after uploading it on cloudinary
     */
    public static Image create(File image, Integer id) {
        Map result;
        try {
            result = cloudinary.uploader().upload(image, null);
            return create(result, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Uploading new image for selected product and saving its path with product
     */
    public static Image create(Map uploadResult, Integer id) {
        Image img = new Image();
        img.public_id = (String) uploadResult.get("public_id");
        img.image_url = (String) uploadResult.get("url");
        img.secret_image_url = (String) uploadResult.get("secure_url");
        Product product = Product.getProductById(id);
        img.product = product;
        img.save();
        return img;
    }

    /**
     * Adding new profile picture for user
     */
    public static Image createUserImage(File image) {
        Map result;
        try {
            result = cloudinary.uploader().upload(image, null);
            return createUserImage(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creating picture for user profile
     */
    public static Image createUserImage(Map uploadResult) {
        Image img = new Image();

        img.public_id = (String) uploadResult.get("public_id");
        img.image_url = (String) uploadResult.get("url");
        img.secret_image_url = (String) uploadResult.get("secure_url");
        User user = SessionHelper.currentUser();
        img.user = user;
        img.save();
        return img;
    }

    /**
     *  Return list of all images
     */
    public static List<Image> all() {
        return finder.all();
    }

    /**
     * Method that return image path for selected product
     */
    public static String getImagePath(Product product) {
        List<Image> image = Image.finder.where().eq("product", product).findList();
        if (image.size() > 0) {
            return image.get(0).image_url;
        } else {
            return "http://placehold.it/450x600";
        }
    }

    /**
     * Return user profile picture
     */
    public static Image getUserImage(User user) {
        Image image = Image.finder.where().eq("user", user).findUnique();
            return image;

    }

    /**
     *  Setting size for picture
     */
    public String getSize(int width, int height) {

        String url = cloudinary.url().format("jpg")
                .transformation(new Transformation().width(width).height(height).crop("fit"))
                .generate(public_id);

        return url;
    }

    /**
     * Setting thumbnail size for picture
     */
    public String getThumbnail(Integer width, Integer height){
        String url = cloudinary.url().format("png")
                .transformation(
                        new Transformation().width(width).height(height).crop("fill"))
                .generate(public_id);
        return url;
    }

    /**
     * Delete picture
     */
    public void deleteImage() {
        try {
            cloudinary.uploader().destroy(public_id, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uploads image that is stored on inputed URL on cloudinary and saves image cloudinary URL in database. First
     * creates new image file and saves the image file in temp directory.
     *
     * Then copies image that is stored on inputed URL into created file. After that creates image file and uploads
     * him on cloudinary, and saves new image file cloudinary URL into database. At the end it deletes created image
     * file from the temp directory.
     *
     * @param imageUrl - URL of the image that user wants to upload on cloudinary.
     * @param product - Product that is on image file.
     */
    public static void savingImageFromUrl(String imageUrl, Product product) {
        try {
            // Checking if the inputed string is URL.
            if (!imageUrl.equals("no_image")) {
                // Storing image file in temp directory.
                String tmpDirectory = System.getProperty("java.io.tmpdir");
                String path = tmpDirectory + "tmp" + ".jpg";
                File fileImage = new File(path);
                fileImage.deleteOnExit();
                // Creating URL from inputed string variable.
                URL url = new URL(imageUrl);
                // Storing image file from URL into created file.
                FileUtils.copyURLToFile(url, fileImage);
                // Uploading image file on cloudinary and saving image cloudinary URL into database.
                Image image = Image.create(fileImage, product.id);
                image.save();
            }
        } catch (IOException e) {
            Logger.error("Error while uploading image to cloudinary and saving into database. " + " - " + e.getMessage());
        }
    }

}
