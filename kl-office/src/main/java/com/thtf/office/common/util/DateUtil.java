package com.thtf.office.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DateUtil {
	public static final String YYYMMDD = "yyyy-MM-dd";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

     
    public static LocalDateTime convertDateToLDT(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    //LocalDateTime转换为Date
    public static Date convertLDTToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }


    //获取指定日期的毫秒
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    //获取指定日期的秒
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    //获取指定时间的指定格式
    public static String formatTime(LocalDateTime time,String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    //获取当前时间的指定格式
    public static String formatNow(LocalDateTime time , String pattern) {
        return  formatTime(time, pattern);
    }
    public static String formatLocalDate2String(LocalDate date ) {
        return  date.format(DateTimeFormatter.ofPattern(YYYMMDD));
    }
    //日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    //日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
    public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field){
        return time.minus(number,field);
    }

    /**
     * 获取两个日期的差  field参数为ChronoUnit.*
     * @param startTime
     * @param endTime
     * @param field  单位(年月日时分秒)
     * @return
     */
    public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
        Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
        if (field == ChronoUnit.YEARS) return period.getYears();
        if (field == ChronoUnit.MONTHS) return period.getYears() * 12 + period.getMonths()*1L;
        return field.between(startTime, endTime);
    }

    //获取一天的开始时间，2017,7,22 00:00
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return time.withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
    }

    //获取一天的结束时间，2017,7,22 23:59:59.999999999
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return time.withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);
    }

    public static LocalDateTime string2Date(String dateStr,String pattern) {
    	return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    
    public static LocalDate string2LocalDate(String dateStr) {
    	return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(YYYMMDD));
    }
    
    public static long differentDays(LocalDate date1,LocalDate date2) {
    	Duration duration =  Duration.between(date1,date2);
    	return duration.toDays();
    }
    
	public static List<LocalDateTime> getDatesBetweenTwoDate(LocalDateTime beginDate, LocalDateTime endDate) {
        List<LocalDateTime> lDate = new ArrayList<>();  
        lDate.add(beginDate);// 把开始时间加入集合  
        
        int days= (int)Duration.between(beginDate,endDate).toDays();
        for(int i =1; i <= days; i++) {
        	lDate.add(beginDate.plusDays(i));
        }

        return lDate; 
	}
	
	public static List<LocalDate> getDatesBetweenTwoDate2(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> lDate = new ArrayList<>();  
        lDate.add(beginDate);// 把开始时间加入集合  

       long days = endDate.toEpochDay()-beginDate.toEpochDay();
        for(int i =1; i <= days; i++) {
        	lDate.add(beginDate.plusDays(i));
        }

        return lDate; 
	}
	
	public static String date2String(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String dateString = formatter.format(date);
		return dateString;

	}
	
	public static Date string2Date2(String dateStr, String pattern) {
		  SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		  try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		  return null;
	}
}
