package models;
import com.avaje.ebean.Model;

import play.data.validation.Constraints;


import javax.persistence.*;
import java.lang.String;
import java.util.List;

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

    private  static Finder<String, Country> finder = new Finder<String, Country>(Country.class);

    public static List<Country> findAllCountries() {
        List<Country> countries = finder.findList();
        return countries;
    }

    public static Country findCountryByName(String name){
        for(Country c: findAllCountries()){
            if(c.name.equals(name)){
                return c;
            }
        }
        return null;
    }
}
