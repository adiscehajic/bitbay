package models;

import com.avaje.ebean.Model;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import helpers.SessionHelper;
import play.Logger;

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
    public Product product;

    @OneToOne
    public User user;


    public static Image create(String public_id, String image_url, String secret_image_url) {
        Image img = new Image();
        img.public_id = public_id;
        img.image_url = image_url;
        img.secret_image_url = secret_image_url;
        img.save();
        return img;
    }

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

    public static Image create(Map uploadResult, Integer id) {
        Image img = new Image();
        Logger.info("-------3--------");
        img.public_id = (String) uploadResult.get("public_id");
        Logger.debug(img.public_id);
        img.image_url = (String) uploadResult.get("url");
        Logger.debug(img.image_url);
        img.secret_image_url = (String) uploadResult.get("secure_url");
        Logger.debug(img.secret_image_url);
        Product product = Product.getProductById(id);
        img.product = product;
        img.save();
        return img;
    }

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

    public static Image createUserImage(Map uploadResult) {
        Image img = new Image();

        img.public_id = (String) uploadResult.get("public_id");
        Logger.debug(img.public_id);
        img.image_url = (String) uploadResult.get("url");
        Logger.debug(img.image_url);
        img.secret_image_url = (String) uploadResult.get("secure_url");
        Logger.debug(img.secret_image_url);
        User user = SessionHelper.currentUser();
        img.user = user;
        img.save();
        return img;
    }

    public static List<Image> all() {
        return finder.all();
    }

    public static String getImagePath(Product product) {
        List<Image> image = Image.finder.where().eq("product", product).findList();
        if (image.size() > 0) {
            return image.get(0).image_url;
        } else {
            return "http://placehold.it/450x600";
        }
    }

    public static Image getUserImage(User user) {
        Image image = Image.finder.where().eq("user", user).findUnique();
            return image;

    }

    public String getSize(int width, int height) {

        String url = cloudinary.url().format("jpg")
                .transformation(new Transformation().width(width).height(height).crop("fit"))
                .generate(public_id);

        return url;
    }

    public String getThumbnail(Integer width, Integer height){
        String url = cloudinary.url().format("png")
                .transformation(
                        new Transformation().width(width).height(height).crop("fill"))
                .generate(public_id);
        return url;
    }

    public void deleteImage() {
        try {
            cloudinary.uploader().destroy(public_id, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
