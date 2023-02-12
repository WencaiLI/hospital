package com.thtf.environment.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DateUtil {
	public static final String YYYMMDD = "yyyy-MM-dd";
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	// 字符串转date
	public static Date string2Date(String dateString, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	// date转字符串
	public static String date2String(Date date, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	//获取当前月有多少天
	public static int maxMonth(String dateString, String pattern) {
		Calendar cal = Calendar.getInstance();
		Date date = DateUtil.string2Date(dateString, pattern);
		cal.setTime(date);
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return max;
	}

	//
	public static String beforeDate2String(String dateString, int field,
			int amount, String pattern) {
		Date date = DateUtil.string2Date(dateString, pattern);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		String s = DateUtil.date2String(cal.getTime(), pattern);
		return s;
	}

	// 字符串时间转字符串时间
	public static String string2String(String dateString, String fromPattern,
			String toPattern) {
		DateFormat df = null;
		Date date = null;
		String dateStr = null;
		try {
			df = new SimpleDateFormat(fromPattern);
			date = df.parse(dateString);
			df = new SimpleDateFormat(toPattern);
			dateStr = df.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStr;
	}

	// 当前时间
	public static String getToday() {
		return DateUtil.date2String(new Date(), "yyyy-MM-dd");
	}

	// 前一天
	public static String beforeDay(String datetime) {
		// TODO Auto-generated method stub
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}

	// 后一天
	public static String afterDay(String datetime) {
		// TODO Auto-generated method stub
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}

	// 获取该时间所在周的周一
	public static String getMondayOfThisWeek(String dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		String imptimeBegin = sdf.format(cal.getTime());
		return imptimeBegin;
	}

	// 获取本周第一天
	public static String getFirstDayOfThisWeek(String dateTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_WEEK, 1);
		return sdf.format(cal.getTime());
	}

	// 前一个月
	public static String beforeMonth(String datetime) {
		// TODO Auto-generated method stub
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -1);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}
	
	// 后一个月
	public static String afterMonth(String datetime) {
		// TODO Auto-generated method stub
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, 1);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}

	//
	public static Timestamp getTimestamp() {
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	/**
	 * 返回当前时间 格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 * @author zhangqiang create date:2013-10-22
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	/**
	 * 返回当前时间 格式为：自定义
	 * 
	 * @return
	 * @author zhangqiang create date:2013-10-22
	 */
	public static String getCurrentTime(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}

	/**
	 * 返回数据库格式时间 yyyyMMddHHmmss
	 * 
	 * @return
	 * @author zhangqiang create date:2014-6-26
	 */
	public static String getSqlCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	/**
	 * 获得普通时间格式
	 * 
	 * @return
	 * @author zhangqiang create date:2014-6-16
	 */
	public static String stringToCommonDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat newSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = null;
		try {
			time = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newSdf.format(time);
	}

	/**
	 * 获得两个时间相差的小时数
	 * 
	 * @return
	 * @author zhangqiang create date:2014-7-28
	 */
	public static Long diffTimeHours(Date date1, Date date2) {

		Long time1 = date1.getTime();
		Long time2 = date2.getTime();

		Long diffTime = time1 > time2 ? time1 - time2 : time2 - time1;

		double hours = new Double(diffTime) / (1000 * 60 * 60);

		return Math.round(hours);
	}
	

	/**
	 * @Title: addAndSubtractHours
	 * @param hours
	 * @return
	 * @Description: 时间加减--小时
	 * @author wangp
	 * @date 2015-7-20 下午01:48:10
	 */
	public static String addAndSubtractHours(int hours) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		cd.add(Calendar.HOUR, hours);
		String nowTime = sdf.format(cd.getTime());
		return nowTime;
	}
	
	/**
	 * 时间加减--小时
	 * @author lizhongyuan
	 * @date 2016-4-29 上午10:08:52
	 */
	public static String addAndSubtractHours(String dateTime,int hour) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd HH:mm:ss");
		cd.setTime(date);
		cd.add(Calendar.HOUR, hour);
		String nowTime = sdf.format(cd.getTime());
		return nowTime;
	}
	
	/**
	 * 时间加减--分钟
	 * @author lizhongyuan
	 * @date 2016-4-29 上午10:08:52
	 */
	public static String addAndSubtractMins(String dateTime,int min) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd HH:mm:ss");
		cd.setTime(date);
		cd.add(Calendar.MINUTE, min);
		String nowTime = sdf.format(cd.getTime());
		return nowTime;
	}
	

	/**
	 * 时间加减 --天
	 * 
	 * @author lizhongyuan
	 * @date 2016-3-21 下午04:23:53
	 */
	public static String addAndSubtractDays(String dateTime, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		String nowTime = sdf.format(cal.getTime());
		return nowTime;
	}

	/**
	 * 获取给定日期月的最后一天
	 * 
	 * @author lizhongyuan
	 * @date 2016-3-24 上午09:50:43
	 */
	public static String lastDayOfMonth(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return sdf.format(cal.getTime());
	}

	/**
	 * 计算2个日期相差月数
	 * @param date
	 * @param date2
	 * @return
	 */
	public static int compareMonth(String date,String date2 ) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date start = format.parse(date);
			Date end = format.parse(date2);
			if (start.after(end)) {
				Date t = start;
				start = end;
				end = t;
			}
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTime(start);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(end);
			Calendar temp = Calendar.getInstance();
			temp.setTime(end);
			temp.add(Calendar.DATE, 1);
			int year = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
			int month = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
			if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) == 1)) {
				return year * 12 + month + 1;
			} else if ((startCalendar.get(Calendar.DATE) != 1) && (temp.get(Calendar.DATE) == 1)) {
				return year * 12 + month;
			} else if ((startCalendar.get(Calendar.DATE) == 1) && (temp.get(Calendar.DATE) != 1)) {
				return year * 12 + month;
			} else {
				return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 计算2个日期相差年数
	 * @param date
	 * @param date2
	 * @return
	 */
	public static int compareYear(String date,String date2 ) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar bef = Calendar.getInstance();
		Calendar aft = Calendar.getInstance();
		try {
			bef.setTime(sdf.parse(date));
			aft.setTime(sdf.parse(date2));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		int year = aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR);
		return Math.abs(year);

	}

	/**
	 * 时间增加天
	 * @param datetime
	 * @param step
	 * @return
	 */
	public static String addDay(String datetime,int step) {
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, step);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 时间 增加月
	 * @param datetime
	 * @param step
	 * @return
	 */
	public static String addMonth(String datetime,int step) {
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, step);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}
	/**
	 * 时间 增加年
	 * @param datetime
	 * @param step
	 * @return
	 */
	public static String addYear(String datetime,int step) {
		Date date = DateUtil.string2Date(datetime, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, step);
		return DateUtil.date2String(c.getTime(), "yyyy-MM-dd");
	}

	/**
	 * 时间 增加小时
	 * @param dateTime
	 * @param hour
	 * @param pattern
	 * @return
	 */
	public static String addAndSubtractHours(String dateTime,int hour,String pattern ) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = DateUtil.string2Date(dateTime, pattern);
		cd.setTime(date);
		cd.add(Calendar.HOUR, hour);
		String nowTime = sdf.format(cd.getTime());
		return nowTime;
	}

	/**
	 * 计算2个时间查询的天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int differentDaysByMillisecond(Date date1,Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
		return days;
	}

	private  static List<String> hourList = new ArrayList<>();
	public static List<String> getHourList() {
		if(hourList.size() ==0) {
			hourList.add(" 00");
			hourList.add(" 01");
			hourList.add(" 02");
			hourList.add(" 03");
			hourList.add(" 04");
			hourList.add(" 05");
			hourList.add(" 06");
			hourList.add(" 07");
			hourList.add(" 08");
			hourList.add(" 09");
			hourList.add(" 10");
			hourList.add(" 11");
			hourList.add(" 12");
			hourList.add(" 13");
			hourList.add(" 14");
			hourList.add(" 15");
			hourList.add(" 16");
			hourList.add(" 17");
			hourList.add(" 18");
			hourList.add(" 19");
			hourList.add(" 20");
			hourList.add(" 21");
			hourList.add(" 22");
			hourList.add(" 23");

		}
		return hourList;
	}

	public static String txDouble(double a,double b) {
		if(b == 0) {
			return "0";
		}
		DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
		return df.format((float)a/b);
	}

	public static String addAndSubtractDay(String dateTime,int day) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
		Date date = DateUtil.string2Date(dateTime, "yyyy-MM-dd HH:mm:ss");
		cd.setTime(date);
		cd.add(Calendar.DAY_OF_MONTH, day);
		String nowTime = sdf.format(cd.getTime());
		return nowTime;
	}

	public  static int getDaysOfMonth(String data) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(format.parse(data));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static String secondToTime(long second) {
		long days = second / 86400;//转换天数
		second = second % 86400;//剩余秒数
		long hours = second / 3600;//转换小时数
		second = second % 3600;//剩余秒数
		long minutes = second / 60;//转换分钟
		second = second % 60;//剩余秒数
		if (0 < days){
			return days + "天"+hours+"小时"+minutes+"分"+second+"秒";
		}else {
			return hours+"小时"+minutes+"分"+second+"秒";
		}
	}

	/**
	 * 获取当月的第一天和最后一天
	 * @author yangsc
	 * @date 2020年4月27日
	 */
	public static String[] getFirstAndLastDayOfMonth(String date){
		String[] dateArray = new String[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.string2Date(date, "yyyy-MM"));
		calendar.add(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
		dateArray[0] = DateUtil.date2String(calendar.getTime(), "yyyy-MM-dd");

		calendar.setTime(DateUtil.string2Date(date, "yyyy-MM"));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));//获取月份最后一天
		dateArray[1] = DateUtil.date2String(calendar.getTime(), "yyyy-MM-dd");

		return dateArray;
	}

	/**
	 * 补全时间字符串(查询前)
	 * @author yangsc
	 * @date 2020年4月27日
	 */
	public static String[] dateStrFormat(String dateType, String startTime, String endTime){
		String[] dateStr = new String[2];
		if(dateType.equalsIgnoreCase("year")){
			dateStr[0] = startTime + "-01-01 00:00:00";
			dateStr[1] = endTime + "-12-31 23:59:59";
		}else if(dateType.equalsIgnoreCase("month")){
			dateStr[0] = DateUtil.getFirstAndLastDayOfMonth(startTime)[0] + " 00:00:00";
			dateStr[1] = DateUtil.getFirstAndLastDayOfMonth(endTime)[1] + " 23:59:59";
		}else if(dateType.equalsIgnoreCase("day")){
			dateStr[0] = startTime + " 00:00:00";
			dateStr[1] = endTime + " 23:59:59";
		}else if(dateType.equalsIgnoreCase("hour")){
			dateStr[0] = startTime + ":00:00";
			dateStr[1] = endTime + ":59:59";
		}
		return dateStr;
	}

	/**
	 * 时间类型转换 逐时 '%Y-%m-%d %H'；逐日 '%Y-%m-%d'；逐月 '%Y-%m';逐年 '%Y'
	 * @author guola
	 * @date 2021-06-02
	 */
	public static String dateTypeFormat(String dateType){
		String type = new String();
		if(dateType.equalsIgnoreCase("year")){
			type = "%Y";
		}else if(dateType.equalsIgnoreCase("month")){
			type = "%Y-%m";
		}else if(dateType.equalsIgnoreCase("day")){
			type = "%Y-%m-%d";
		}else if(dateType.equalsIgnoreCase("hour")){
			type = "%Y-%m-%d %H";
		}
		return type;
	}

	/**
	 * 时间类型转换 逐时 'yyyy-MM-dd HH'；逐日 'yyyy-MM-dd'；逐月 'yyyy-MM';逐年 'yyyy'
	 * @author guola
	 * @date 2021-06-02
	 */
	public static String dateTypeTran(String dateType){
		String type = new String();
		if(dateType.equalsIgnoreCase("year")){
			type = "yyyy";
		}else if(dateType.equalsIgnoreCase("month")){
			type = "yyyy-MM";
		}else if(dateType.equalsIgnoreCase("day")){
			type = "yyyy-MM-dd";
		}else if(dateType.equalsIgnoreCase("hour")){
			type = "yyyy-MM-dd HH";
		}
		return type;
	}

	/**
	 * 查询范围内的所有时间
	 * @author dengyl
	 * @date 2021-06-02
	 */
	public static List<String> getAllDateString(String startTime, String endTime,String dateType){
		List<String> list = new ArrayList<String>();

		List<String> hourList = DateUtil.getHourList();
		if (dateType.equals("hour")) {
			int day = DateUtil.differentDaysByMillisecond(DateUtil.string2Date(startTime, DateUtil.YYYMMDD),
					DateUtil.string2Date(endTime, DateUtil.YYYMMDD));
			if(day ==0) {
				String d = DateUtil.string2String(startTime, DateUtil.yyyyMMddHHmmss, DateUtil.YYYMMDD);
				for(int len = 0; len < hourList.size();len++) {
					list.add(d+hourList.get(len));
				}
			}else {
				for(int i =0 ; i <=day ; i++) {
					String d = DateUtil.addDay(startTime,i);
					for(int len = 0; len < hourList.size();len++) {
						list.add(d+hourList.get(len));
					}
				}
			}
		} else if (dateType.equals("day")) {
			int day = DateUtil.differentDaysByMillisecond(DateUtil.string2Date(startTime, DateUtil.YYYMMDD),
					DateUtil.string2Date(endTime,DateUtil.YYYMMDD));
			if(day ==0) {
				list.add(DateUtil.addDay(startTime,0));
			}else {
				for(int i =0 ; i <=day ; i++) {
					list.add(DateUtil.addDay(startTime,i));
				}
			}
		} else if (dateType.equals("month")) {
			int len = DateUtil.compareMonth(startTime, endTime);
			if(len ==0) {
				list.add(DateUtil.addMonth(startTime, 0).substring(0,7));
			}else {
				for(int i =0; i <len;i++) {
					list.add(DateUtil.addMonth(startTime, i).substring(0,7));
				}
			}
		} else if (dateType.equals("year")) {
			int len = DateUtil.compareYear(startTime, endTime);
			if(len ==0) {
				list.add(DateUtil.addYear(startTime, 0).substring(0,4));
			}else {
				for(int i =0; i <=len;i++) {
					list.add(DateUtil.addYear(startTime, i).substring(0,4));
				}
			}
		}
		return list ;
	}

	//获取某年某月的最后一天
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	//获取某年某月的第一天
	public static String getFisrtDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());
		return firstDayOfMonth;
	}
	
	/**
	 * 判断字符串是否是yyyy-MM-dd格式
	 * @author yangsc
	 * @date 2021年7月1日
	 */
	public static boolean isRqFormat(String mes){
        String format = "([0-9]{4})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(mes);
        if (matcher.matches()) {
            pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
            matcher = pattern.matcher(mes);
            if (matcher.matches()) {
                int y = Integer.parseInt(matcher.group(1));
                int m = Integer.parseInt(matcher.group(2));
                int d = Integer.parseInt(matcher.group(3));
                if (d > 28) {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m-1, 1);
                    int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    return (lastDay >= d);
                }
            }
            return true;
        }
        return false;
    }

	/**
	 * 获取同比日期
	 *
	 * @author xubc
	 * @date 2015-8-4 上午11:12:37
	 * @param date
	 * @param pattern 日期格式
	 * @return
	 */
	public static String getLastYearDate(String date, String pattern) {
		pattern = "yyyy-MM-dd HH:mm:ss";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.string2Date(date, pattern));
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
		return DateUtil.date2String(calendar.getTime(), pattern);
	}

	/**
	 * 获取环比日期
	 * @author yangsc
	 * @date 2021-08-05
	 */
	public static String getChainDateByType(String date, String dateType) {
		String pattern = "yyyy-MM-dd HH:mm:ss";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.string2Date(date, pattern));
		if(dateType.equalsIgnoreCase("year")){
			calendar.add(Calendar.YEAR, -1);
		}else if(dateType.equalsIgnoreCase("day") || dateType.equalsIgnoreCase("hour") || dateType.equalsIgnoreCase("month")){
			calendar.add(Calendar.MONTH, -1);
		}
		return DateUtil.date2String(calendar.getTime(), pattern);
	}

	public static String LocalDateTime2String(LocalDateTime time,String dateType){
		//DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter df = DateTimeFormatter.ofPattern(dateType);
		String localTime = df.format(time);
		System.out.println("LocalDateTime转成String类型的时间："+localTime);
		return localTime;
	}

	public static LocalDateTime String2LocalDateTime(String time,String dateType){
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		//DateTimeFormatter df = DateTimeFormatter.ofPattern(dateType);
		String times = time;
		if (dateType != null){
			times = DateUtil.dateStrFormat(dateType,time,time)[0];
		}
		LocalDateTime ldt = LocalDateTime.parse(times,df);
		System.out.println("String类型的时间转成LocalDateTime："+ldt);
		return ldt;
	}


	/**
	 * 获取15分钟的时间 (返回yyyy-MM-dd HH:mm:ss)  后面自己拼
	 */
	/**
	 * @Title:	dateToStringForNumMinute
	 * @Description: 向前取整，间隔n分钟，n必须被60整除
	 * @param: @param num for 5,前一个5分钟的时间。
	 * @param: @return
	 * @return: Date
	 * @throws
	 * @author tianye
	 */
	public static LocalDateTime dateToStringForNumMinute(LocalDateTime now,int n){
		int minute = now.getMinute();
		int parameter = minute/n;
		LocalDateTime targetTime = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth()
				,now.getHour(),parameter*n,00,000);
		return targetTime;
	}
/**
 * @Package      com.thtf.oval.commons.util
 * @MethodName   localDateTimeToString
 * @Description  localDateTime转String
 * @param now	localDateTime时间
 * @param format	格式，例如 "yyyy-MM-dd HH:mm:ss"
 * @ParamType
 * @Return       java.lang.String
 * @Author       Tiany
 * @Date         2021/8/25 21:18
*/
	public static String localDateTimeToString(LocalDateTime now,String format){
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern(format);
		return now.format(dtf);
	}

	public static void main(String[] args) {
		int n =5;
		LocalDateTime now = LocalDateTime.now();
		int minute = now.getMinute();
		int parameter = minute/n;
		LocalDateTime targetTime = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth()
				,now.getHour(),parameter*n,00,000);
		String dateStr= targetTime.toLocalDate()+" " + targetTime.toLocalTime()+":00";
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String s = targetTime.format(dtf);
		System.out.println("值=" + s + "," + "当前类=DateUtil.main()");
	}

	/**
	 * 当前季度的开始时间，即2021-01-01 00:00:00
	 *
	 * @return
	 */
	public static String getCurrentQuarterStartTime(String date) {
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.string2Date(date,"yyyy-MM"));
		int currentMonth = c.get(Calendar.MONTH) + 1;
		SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 6);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DateUtil.date2String(now,"yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 当前季度的结束时间，即2021-03-31 23:59:59
	 *
	 * @return
	 */
	public static String getCurrentQuarterEndTime(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.string2Date(getCurrentQuarterStartTime(date),"yyyy-MM-dd HH:mm:ss"));
		cal.add(Calendar.MONTH, 2);
		return DateUtil.getFirstAndLastDayOfMonth(DateUtil.date2String(cal.getTime(),"yyyy-MM-dd"))[1]+" 23:59:59";
	}

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date asDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate asLocalDate(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static LocalDateTime asLocalDateTime(Date date) {
		return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * 比较两个时间的大小
	 */
	public static Long compareTime(Date date1, Date date2){
		long result = date1.getTime()-date2.getTime();
		//result大于0，则t1>t2；
		//result等于0，则t1=t2；
		//result小于0，则t1<t2
		return result;
	}


}
