package com.ncse.zhhygis.utils.baseUtils;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 */
public class DateUtil {

    // 默认日期格式
    public static final String DATE_DEFAULT_FORMAT = "yyyy-MM-dd";
    public static final String DATE_MONTH_FORMAT = "yyyy-MM";

    // 默认时间格式
    public static final String DATETIME_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATETIME_DEFAULT_FORMAT_MS = "yyyyMMddHHmmssSSS";

    public static final String TIME_DEFAULT_FORMAT = "HH:mm:ss";

    //生成时间
    public static final String pkSerial = System.currentTimeMillis()+"";

    // 日期格式化
    private static DateFormat dateFormat = null;

    // 时间格式化
    private static DateFormat dateTimeFormat = null;

    private static DateFormat dateMonthFormat = null;

    private static DateFormat timeFormat = null;

    private static Calendar gregorianCalendar = null;

    static {
        dateFormat = new SimpleDateFormat(DATE_DEFAULT_FORMAT);
        dateMonthFormat = new SimpleDateFormat(DATE_MONTH_FORMAT);
        dateTimeFormat = new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
        timeFormat = new SimpleDateFormat(TIME_DEFAULT_FORMAT);
        gregorianCalendar = new GregorianCalendar();
    }

    /**
     * 日期格式化yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static Date formatDate(String date, String format) {
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 日期格式化yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDateFormat(Date date) {
        if(date==null){
            return null;
        }else{
            return dateFormat.format(date);
        }
    }
    /**
     * 日期格式化yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String getDateMonthFormat(Date date) {
        if(date==null){
            return null;
        }else{
            return dateMonthFormat.format(date);
        }
    }

    /**
     * 日期格式化yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String getDateTimeFormat(Date date) {
        if(date==null){
            return null;
        }else{
            return dateTimeFormat.format(date);
        }
    }

    /**
     * 时间格式化
     *
     * @param date
     * @return HH:mm:ss
     */
    public static String getTimeFormat(Date date) {
        if(date==null){
            return null;
        }else{
            return timeFormat.format(date);
        }
    }

    /**
     * 日期格式化
     *
     * @param date
     * @param formatStr 格式类型
     * @return
     */
    public static String getDateFormat(Date date, String formatStr) {
        if (StringUtils.isNotBlank(formatStr)) {
            return new SimpleDateFormat(formatStr).format(date);
        }
        return null;
    }

    /**
     * 日期格式化
     *
     * @param date
     * @return
     */
    public static Date getDateFormat(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间格式化
     *
     * @param date
     * @return
     */
    public static Date getDateTimeFormat(String date) {
        try {
            return dateTimeFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前日期(yyyy-MM-dd)
     *
     * @return
     */
    public static Date getNowDate() {
        return DateUtil.getDateFormat(dateFormat.format(new Date()));
    }

    /**
     * 获取当前日期星期一日期
     *
     * @return date
     */
    public static Date getFirstDayOfWeek() {
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek()); // Monday
        return gregorianCalendar.getTime();
    }

    /**
     * 获取当前日期星期日日期
     *
     * @return date
     */
    public static Date getLastDayOfWeek() {
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek() + 6); // Monday
        return gregorianCalendar.getTime();
    }

    /**
     * 获取日期星期一日期
     *
     * @param date 指定日期
     * @return date
     */
    public static Date getFirstDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek()); // Monday
        return gregorianCalendar.getTime();
    }

    /**
     * 获取日期星期一日期
     *
     * @param date 指定日期
     * @return date
     */
    public static Date getLastDayOfWeek(Date date) {
        if (date == null) {
            return null;
        }
        gregorianCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_WEEK, gregorianCalendar.getFirstDayOfWeek() + 6); // Monday
        return gregorianCalendar.getTime();
    }

    /**
     * 获取当前月的第一天
     *
     * @return date
     */
    public static Date getFirstDayOfMonth() {
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取当前月的最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        gregorianCalendar.add(Calendar.MONTH, 1);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取指定月的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取指定月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(Date date) {
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        gregorianCalendar.add(Calendar.MONTH, 1);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取日期前一天
     *
     * @param date
     * @return
     */
    public static Date getDayBefore(Date date) {
        gregorianCalendar.setTime(date);
        int day = gregorianCalendar.get(Calendar.DATE);
        gregorianCalendar.set(Calendar.DATE, day - 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取日期后一天
     *
     * @param date
     * @return
     */
    public static Date getDayAfter(Date date) {
        gregorianCalendar.setTime(date);
        int day = gregorianCalendar.get(Calendar.DATE);
        gregorianCalendar.set(Calendar.DATE, day + 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取当前年
     *
     * @return
     */
    public static int getNowYear() {
        Calendar d = Calendar.getInstance();
        return d.get(Calendar.YEAR);
    }

    /**
     * 获取当前天
     *
     * @return
     */
    public static int getNowDay() {
        Calendar d = Calendar.getInstance();
        return d.get(Calendar.DATE);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getNowMonth() {
        Calendar d = Calendar.getInstance();
        return d.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当月天数
     *
     * @return
     */
    public static int getNowMonthDay() {
        Calendar d = Calendar.getInstance();
        return d.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取时间段的每一天
     *
     * @param startDate 开始日期
     * @param endDate   结算日期
     * @return 日期列表
     */
    public static List<Date> getEveryDay(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }
        // 格式化日期(yy-MM-dd)
        startDate = DateUtil.getDateFormat(DateUtil.getDateFormat(startDate));
        endDate = DateUtil.getDateFormat(DateUtil.getDateFormat(endDate));
        List<Date> dates = new ArrayList<Date>();
        gregorianCalendar.setTime(startDate);
        dates.add(gregorianCalendar.getTime());
        while (gregorianCalendar.getTime().compareTo(endDate) < 0) {
            // 加1天
            gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
            dates.add(gregorianCalendar.getTime());
        }
        return dates;
    }

    /**
     * 获取提前多少个月
     *
     * @param monty
     * @return
     */
    public static Date getFirstMonth(int monty) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -monty);
        return c.getTime();
    }


    /**
     * @return 获取当前时间  精确到秒
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 时间比较 ，如果dat1>dat2  返回1;dat1=dat2  返回0;dat1<dat2  返回-1;
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareToDate(Date date1, Date date2) {
        return date1.compareTo(date2);
    }

    /**
     * 日期比较 ，如果dat1>dat2  返回1;dat1=dat2  返回0;dat1<dat2  返回-1;
     *
     * @param dateStr1
     * @param dateStr2
     * @return
     */
    public static int compareToDate(String dateStr1, String dateStr2) {
        Date date1 = getDateFormat(dateStr1);
        Date date2 = getDateFormat(dateStr2);
        return date1.compareTo(date2);
    }
    /**
     * 时间比较 ，如果dat1>dat2  返回1;dat1=dat2  返回0;dat1<dat2  返回-1;
     *
     * @param dateStr1
     * @param dateStr2
     * @return
     */
    public static int compareToDateTime(String dateStr1, String dateStr2) {
        Date date1 = getDateTimeFormat(dateStr1);
        Date date2 = getDateTimeFormat(dateStr2);
        return date1.compareTo(date2);
    }

    /**
     * 时间戳转为指定格式的Date时间串
     * @param timeMills
     * @param forMat
     * @return
     */
    public static String timeMillsToDate(Long timeMills,String forMat){
        SimpleDateFormat sdf=new SimpleDateFormat(forMat);
        String format = sdf.format(new Date(timeMills));
        return forMat;
    }

    /**
     * 手动分单日期格式化
     *
     * @param month 分单月份
     * @return
     */
    public static Map<String,String> allotMonth(String month) {
        int nowYear = getNowYear()-1900;
        Date date = new Date(nowYear,Integer.valueOf(month)-1,1);
        Date nextMonth = new Date(nowYear,Integer.valueOf(month),1);
        String time = getDateTimeFormat(date)+","+getDateTimeFormat(nextMonth);
        Map<String,String> dateMap = new HashMap<String, String>();
        dateMap.put("nowMonth",getDateTimeFormat(date));
        dateMap.put("nextMonth",getDateTimeFormat(nextMonth));
        return dateMap;
    }

    /**
     * 获取date的月份
     * @return
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month;
    }

    /**
     * 获取date的年份
     * @return
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }
    
    /**
     * 计算2个时间的时间差
     * @param str1
     * @param str2
     * @param type day 天，hour 小时,min 分，sec 秒，其他毫秒
     * @return
     */
    public static long getDistanceTimes(String str1, String str2,String type) {
        DateFormat df = new SimpleDateFormat(DATETIME_DEFAULT_FORMAT);
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff ;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            

            if("day".equals(type)) {
            	diff=diff / (24 * 60 * 60 * 1000);
            }else if("hour".equals(type)) {
            	diff = (diff / (60 * 60 * 1000));
            }else if("min".equals(type)) {
            	diff = (diff / (60 * 1000));
            }else if("sec".equals(type)) {
            	diff= (diff / (1000));
            }
        } catch (ParseException e) {
        	diff=0;
            e.printStackTrace();
        }
       
        return diff;
    }

    public static void main(String[] args) {
        Date firstMonth = getFirstMonth(3);
        String timeFormat1 = getDateTimeFormat(firstMonth);
        System.out.println(timeFormat1);
        Date nowDate = new Date();
        Date dayAfter = DateUtil.getDayAfter(nowDate);
        String timeFormat = getDateTimeFormat(dayAfter);
        System.out.println(timeFormat);
        String dateFormat = DateUtil.getDateTimeFormat(nowDate);
        System.out.println(dateFormat);
    }

}
