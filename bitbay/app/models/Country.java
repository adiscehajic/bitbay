package models;
import com.avaje.ebean.Model;

import play.data.validation.Constraints;


import javax.persistence.*;
import java.lang.String;

/**
 * Created by dinko.hodzic on 08/09/15.
 */
@Entity
public class Country extends Model{

    @Id
    public Integer id;

    @Constraints.MaxLength(255)
    public String name;
    //Some comment
}
