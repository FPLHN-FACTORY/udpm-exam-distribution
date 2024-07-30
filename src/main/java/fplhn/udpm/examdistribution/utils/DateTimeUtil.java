package fplhn.udpm.examdistribution.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public static String parseLongToString(Long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date(date));
    }

    public static Long getCurrentTime() {
        return new Date().getTime();
    }

    public static Long getCurrentTimeMillis() {
        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfDay = currentDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return instant.toEpochMilli();
    }

    public static Long getCurrentDateWithoutTime() {
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        LocalDateTime startOfDay = currentDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        return instant.toEpochMilli();
    }

    public static LocalDate convertToLocalDate(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

}