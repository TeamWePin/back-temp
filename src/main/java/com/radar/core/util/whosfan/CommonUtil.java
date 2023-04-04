package com.radar.core.util.whosfan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    public static String alterGMTDate(String date, int hour) {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String format_time1 = "";

        try {
            Date tmpDate = dt.parse(date);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            format_time1 = format1.format(tmpDate.getTime() + (long)(hour * 1000 * 60 * 60));
        } catch (ParseException var6) {
            var6.printStackTrace();
        }

        return format_time1;
    }
}
