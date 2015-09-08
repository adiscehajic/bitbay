package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.format.Formats;

import javax.persistence.*;

import java.lang.String;
<<<<<<< HEAD


=======
>>>>>>> 89e26e0d4eb85f455eac3c0f54dc325cea0857db
/**
 * Created by Adnan on 8.9.2015.
 */
@Entity
public class Category extends Model {

    @Id
    public Integer id;
<<<<<<< HEAD
    public String name;
    public Integer parent_id;
    public Integer status_id;

    public Category(String name, Integer parent_id, Integer status_id){
=======
    public Integer parent_id;
    public Integer status_id;
    public String name;


    public Category(String name, Integer parent_id, Integer status_id){

>>>>>>> 89e26e0d4eb85f455eac3c0f54dc325cea0857db
        this.name = name;
        this.parent_id = parent_id;
        this.status_id = status_id;
    }

}
