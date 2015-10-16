package helpers;

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
        SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
        String myDate = dtf.format(date);
        return myDate;
    }

    public static String getTimeFromDate(Date date){
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(date);
        return time;
    }
}
