package helpers;

import models.Category;
import models.User;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kerim Dragolj on 8.10.2015.
 */
public class CommonHelpers {

    public static String getDateAsString(Date date){
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String myDate = dtf.format(date);
        return myDate;
    }

    public static String getOnlyDate(Date date){
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String myDate = dtf.format(date);
        return myDate;
    }

    public static String getTimeFromDate(Date date){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(date);
        return time;
    }

    public static User serviceUser(){
        return User.getUserByEmail("bitbayservice@gmail.com");
    }

    /**
     * Finds the bitClassroom user in the database and returns it.
     *
     * @return bitClassroom user.
     */
    public static User bitclassroomUser() {
        return User.getUserByEmail(ConstantsHelper.BIT_CLASSROOM_EMAIL);
    }

    /**
     * Finds the bitClassroom subcategory in the database and returns the bitClassroom subcaterory.
     *
     * @return bitClassroom subcategory.
     */
    public static Category getBitClassroomCategory() {
        return Category.getCategoryByName(ConstantsHelper.BIT_CLASSROOM_CATEGORY);
    }
}
