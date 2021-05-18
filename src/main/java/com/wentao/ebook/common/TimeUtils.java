package com.wentao.ebook.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils {
    public static final Date BASE_DATE_1904 = getBaseDate(1904);
    public static final Date BASE_DATE_1970 = getBaseDate(1970);
    
    private static Date getBaseDate(int year){
        Calendar c = new GregorianCalendar();
        c.clear();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        return c.getTime();
    }
    
    public static Date add(Date date, int field, int value){
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(field, value);
        return c.getTime();
    }
}
