package Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private static final String DateFormat = "dd-MM-yyyy@HH:mm:ss";

    public static String dateTime2Str(Date date) {
        String dStr = new SimpleDateFormat(DateFormat).format(date);
        return dStr;
    }

    public static Date str2DateTime(String dateTimeStr) throws ParseException {
        Date date = new SimpleDateFormat(DateFormat).parse(dateTimeStr);
        return date;
    }
}
