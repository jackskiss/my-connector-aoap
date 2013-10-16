package com.obigo.baidumusic.standard.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public final class Holidays {
    
    private static final String TAG = "Holidays";
    
    private static String DATE_FORMAT = "yyyy-MM-dd";
    
    private static List<String> mHolidays = Arrays.asList(
            /* 2013 */
            /* 元旦 */ "2013-01-01", "2013-01-02", "2013-01-03", 
            /* 春节 */ "2013-02-09", "2013-02-10", "2013-02-11", "2013-02-12", "2013-02-13", "2013-02-14", "2013-02-15", /* lunar 12.29~1.6 */
            /* 清明节 */ "2013-04-04", "2013-04-05", "2013-04-06", /* lunar 2.24~2.26 */
            /* 劳动节 */ "2013-04-29", "2013-04-30", "2013-05-01",
            /* 端午节 */ "2013-06-10", "2013-06-11", "2013-06-12", /* lunar 5.3~5.5 */
            /* 中秋节 */ "2013-09-19", "2013-09-20", "2013-09-21", /* lunar 8.15~8.17 */
            /* 国庆节 */ "2013-10-01", "2013-10-02", "2013-10-03", "2013-10-04", "2013-10-05", "2013-10-06", "2013-10-07",
            
            /* 2014 */
            /* 元旦 */ "2014-01-01", "2014-01-02", "2014-01-03", 
            /* 春节 */ "2014-01-29", "2014-01-30", "2014-01-31", "2014-02-01", "2014-02-02", "2014-02-03", "2014-02-04",
            /* 清明节 */ "2014-03-24", "2014-03-25", "2014-03-26",
            /* 劳动节 */ "2014-04-29", "2014-04-30", "2014-05-01",
            /* 端午节 */ "2014-05-31", "2014-06-01", "2014-06-02",
            /* 中秋节 */ "2014-09-08", "2014-09-09", "2014-09-10",
            /* 国庆节 */ "2014-10-01", "2014-10-02", "2014-10-03", "2014-10-04", "2014-10-05", "2014-10-06", "2014-10-07",
            
            /* 2015 */
            /* 元旦 */ "2015-01-01", "2015-01-02", "2015-01-03", 
            /* 春节 */ "2015-02-17", "2015-02-18", "2015-02-19", "2015-02-20", "2015-02-21", "2015-02-22", "2015-02-23",
            /* 清明节 */ "2015-04-12", "2015-04-13", "2015-04-14",
            /* 劳动节 */ "2015-04-29", "2015-04-30", "2015-05-01",
            /* 端午节 */ "2015-06-18", "2015-06-19", "2015-06-20",
            /* 中秋节 */ "2015-09-27", "2015-09-28", "2015-09-29",
            /* 国庆节 */ "2015-10-01", "2015-10-02", "2015-10-03", "2015-10-04", "2015-10-05", "2015-10-06", "2015-10-07",
            
            /* 2016 */
            /* 元旦 */ "2016-01-01", "2016-01-02", "2016-01-03", 
            /* 春节 */ "2016-02-07", "2016-02-08", "2016-02-09", "2016-02-10", "2016-02-11", "2016-02-12", "2016-02-13",
            /* 清明节 */ "2016-04-01", "2016-04-02", "2016-04-03",
            /* 劳动节 */ "2016-04-29", "2016-04-30", "2016-05-01",
            /* 端午节 */ "2016-06-07", "2016-06-08", "2016-06-09",
            /* 中秋节 */ "2016-09-15", "2016-09-16", "2016-09-17",
            /* 国庆节 */ "2016-10-01", "2016-10-02", "2016-10-03", "2016-10-04", "2016-10-05", "2016-10-06", "2016-10-07"
            
            /* 2017 */
            // add holidays hear.
            );

    private static Comparator<String> comp = new Comparator<String>() {

        @Override
        public int compare(String s1, String s2) {
            Date day1 = null;
            Date day2 = null;
            
            ObiLog.d(TAG, "s1 = " + s1 + ", s2 = " + s2);
            
            SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
            
            try {
                day1 = dataFormat.parse(s1);
                day2 = dataFormat.parse(s2);
                
                ObiLog.d(TAG, "day1 = " + day1.toGMTString());
                ObiLog.d(TAG, "day2 = " + day2.toGMTString());
                
            } catch (ParseException e) {
                ObiLog.e(TAG, "SimpleDateFormat parce error", e);
            }

            if (day1 != null && day2 != null) {
                int ret = day1.compareTo(day2);
                
                ObiLog.d(TAG, "compare ret = " + ret);
                
                if (ret == 0) {
                    return 0;
                } else if(ret > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
            
            ObiLog.d(TAG, "compare day1 == null or day2 == null");
            return -1;
        }

    };

    private Holidays() {
        // not called
    }

    public static boolean isContain(Calendar calendar) {
        SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_FORMAT);
        String day = dataFormat.format(calendar.getTime());

        int dow = calendar.get(Calendar.DAY_OF_WEEK);

        if (dow == Calendar.SATURDAY || dow == Calendar.SUNDAY) {
            ObiLog.d(TAG, "Today is Weekend.");
            return true;
        }

        if (Collections.binarySearch(mHolidays, day, comp) >= 0) {
            ObiLog.d(TAG, "Today is Holiday.");
            return true;
        }
        return false;
    }
}
