package fplhn.udpm.examdistribution.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static Long parseStringToLong(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateConvert = dateFormat.parse(date);
            return dateConvert.getTime();
        } catch (ParseException e) {
            try {
                return Long.parseLong(date);
            } catch (NumberFormatException ex) {
                ex.printStackTrace(System.err);
                return null;
            }
        }
    }

    public static Long getCurrentTime() {
        return new Date().getTime();
    }

}