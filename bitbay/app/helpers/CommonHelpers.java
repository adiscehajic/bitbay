package helpers;

import models.User;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kerim on 8.10.2015.
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
    public static Double getTwoDecimalNumber(Double number){
        DecimalFormat df = new DecimalFormat("#.00");
        return Double.parseDouble(df.format(number));
    }

    public static User serviceUser(){
        return User.getUserByEmail("bitbayservice@gmail.com");
    }
}
