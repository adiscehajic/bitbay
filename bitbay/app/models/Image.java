package models;

import com.avaje.ebean.Model;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import helpers.SessionHelper;

import javax.persistence.*;
import java.io.File;
import java.io.*;
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

}
