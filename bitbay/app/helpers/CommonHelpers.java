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
}
