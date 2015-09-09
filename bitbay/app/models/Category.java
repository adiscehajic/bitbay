package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.format.Formats;

import javax.persistence.*;

import java.lang.String;

/**
 * Created by Adnan on 8.9.2015.
 */
@Entity
public class Category extends Model {

    @Id
    public Integer id;
    public String name;
    public Integer parent_id;
    public Integer status_id;


    public Category(String name, Integer parent_id, Integer status_id){

        this.name = name;
        this.parent_id = parent_id;
        this.status_id = status_id;
    }

}
