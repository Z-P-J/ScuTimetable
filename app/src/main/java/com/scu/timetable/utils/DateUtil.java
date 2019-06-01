package com.scu.timetable.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Z-P-J
 */
public final class DateUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    private static final String[] WEEK_DAYS = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

    private DateUtil() {

    }

    public static String currentDate() {
        synchronized (FORMAT) {
            return FORMAT.format(new Date());
        }
    }

    public static Date parse(String str) {
        synchronized (FORMAT) {
            try {
                return FORMAT.parse(str);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static int dayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String dayOfWeekStr() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return dayOfWeekStr(calendar.get(Calendar.DAY_OF_WEEK) - 1);
    }

    public static String dayOfWeekStr(int index) {
        return WEEK_DAYS[index];
    }

    public static int computeWeek(Date startDate, Date endDate) {
        int weeks = 0;

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(startDate);

        int weekIndex = beginCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        System.out.println("weekIndex=" + weekIndex);
        beginCalendar.add(Calendar.DAY_OF_YEAR, 7 - weekIndex);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        weekIndex = endCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        System.out.println("weekIndex=" + weekIndex);

        endCalendar.add(Calendar.DAY_OF_YEAR, 7 - weekIndex);

        while (beginCalendar.before(endCalendar)) {
            System.out.println("111111111111111");
            System.out.println("beginCalendar.get(Calendar.YEAR)=" + beginCalendar.get(Calendar.YEAR));
            System.out.println("endCalendar.get(Calendar.YEAR)=" + endCalendar.get(Calendar.YEAR));
            System.out.println("beginCalendar.get(Calendar.MONTH)=" + beginCalendar.get(Calendar.MONTH));
            System.out.println("endCalendar.get(Calendar.MONTH)=" + endCalendar.get(Calendar.MONTH));
            System.out.println("beginCalendar.get(Calendar.WEEK_OF_MONTH)=" + beginCalendar.get(Calendar.WEEK_OF_MONTH));
            System.out.println("endCalendar.get(Calendar.WEEK_OF_MONTH)=" + endCalendar.get(Calendar.WEEK_OF_MONTH));
            // 如果开始日期和结束日期在同年、同月且当前月的同一周时结束循环
            if (beginCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
                    beginCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) &&
                    beginCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH)) {
                System.out.println("22222222222222222222");
                break;
            } else {
                System.out.println("33333333333333333333");
                beginCalendar.add(Calendar.DAY_OF_YEAR, 7);
                weeks += 1;
            }
        }
        return weeks;
    }

}
