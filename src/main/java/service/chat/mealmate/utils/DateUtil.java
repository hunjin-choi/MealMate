package service.chat.mealmate.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class DateUtil {

    static public Date stringToDate(String str) {
        // yyyy-mm-dd 혹은 yyyy-m-dd 혹은 yyyy-m-d 포맷을 받습니다
        boolean matches = Pattern.matches("^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$", str);
        if (!matches) throw new RuntimeException("적절하지 않은 날짜 포맷의 String 입니다.");
        return Date.from(LocalDate.parse(str).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    static public Date addDaysToDate(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    static public Date stringToDateAndAddDays(String str, int days) {
        Date date = stringToDate(str);
        return addDaysToDate(date, days);
    }

    static public Long calculateTwoDateDiffAbs(Date d1, Date d2) {
        long diff = d1.getTime() - d2.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    static public boolean isSameDateWithoutTime(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    static public int isSameDate(Date d1, Date d2) {
        return d1.compareTo(d2);
    }

    static public Date getNow() {
        return new Date();
    }

    static public Date addDaysFromNow(int days) {
        Date now = getNow();
        return addDaysToDate(now, days);
    }
    static public int getHour(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("ASIA/SEOUL"));
        cal.setTime(d);
        return cal.get(Calendar.HOUR);
    }
    static public int getMinute(Date d) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("ASIA/SEOUL"));
        cal.setTime(d);
        return cal.get(Calendar.MINUTE);
    }

    static public Date addHour(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();
    }
}
