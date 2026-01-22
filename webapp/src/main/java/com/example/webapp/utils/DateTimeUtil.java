package com.example.webapp.utils;

import org.joda.time.*;

import javax.xml.bind.DatatypeConverter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

/**
 * @program: b2b-common
 * @author: gehaisong
 * @create: 2019-12-16 11:19
 * DateTimeConstants 常量类
 * 如果业务只需要日期，请使用 LocalDate,
 * 如果业务只关心时间，那么使用 LocalTime
 **/
public class DateTimeUtil {
    public static final String DATE_FORMAT_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYYMMDD_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_FORMAT_YYYYMMDD_HHMMSS_LONG = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_YYYYMMDD ="yyyy-MM-dd";
    public static final String DATE_FORMAT_YYYYMMDD_INT ="yyyyMMdd";
    public static final String DATE_FORMAT_MMDD ="MM-dd";
    public static final String DATE_FORMAT_MMDD_INT ="MMdd";
    public static final String DATE_FORMAT_YYYYMMDD_CN ="yyyy年MM月dd日";
    public static final String DATE_FORMAT_YYYYMMDD_EE_CN ="yyyy年MM月dd日 HH:mm:ss EE";
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS ="yyyyMMddHHmmss";
    /** 空格 */
    public static final String DELIMITER_ONE_SPACE = " ";
    /** 短杠- */
    public static final String DELIMITER_ONE_LINE = "-";
    /** 下划线_ */
    public static final String DELIMITER_ONE_UNDERLINE = "_";
    public static final String DELIMITER_ONE_SLASH = "/";
    /** 逗号, */
    public static final String DELIMITER_ONE_COMMA = ",";
    public static final String DELIMITER_ONE_COLON = ":" ;
    /**句号.*/
    public static final String DELIMITER_ONE_PERIOD = ".";


    /**
     * 日期格式化
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date,String pattern){
        DateTime dateTime =new DateTime(date);
        String str = dateTime.toString(pattern,Locale.CHINESE);
        return str;
    }

    /**
     * 日期字符(X: 时区+08, +代表东半球, -代表西半球)
     *    2022年03月28日 00:57:00.111 星期一
     *    2022-03-28T00:57:00.274+08:00
     *    星期三, 28 三月 2012 00:57:30 +0800
     *    星期三 三月 28 00:57:30 CST 2012
     * @param date
     * @return
     */
    public static Date parse(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy年MM月dd日[ HH:mm[:ss[.SSS][ EE]]]]"
                        + "[yyyy-MM-dd'T'HH:mm[:ss[.SSS[XX][XXX]]]]"
                        + "[yyyy-MM-dd[ HH:mm[:ss[.SSS]]]]"
                        + "[yyyy/MM/dd[ HH:mm[:ss[.SSS]]]]"
                        + "[yyyy.MM.dd[ HH:mm[:ss[.SSS]]]]"
                        + "[E, dd MMM yyyy HH:mm:ss[ Z]]"
                        + "[E MMM dd HH:mm:ss z yyyy]"
                        + "[E MMM dd HH:mm:ss z yyyy]"
                ,Locale.CHINESE);
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().append(formatter)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        java.time.LocalDateTime localDateTime = java.time.LocalDateTime.parse(date,dateTimeFormatter);
        return Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    /**
     * 比较两时间大小 时间大于指定时间返回 1 时间小于指定时间返回-1 相等返回0
     * @param date1 date2
     */
    public static int compareTo(Date date1 , Date date2){
        DateTime dt1 =new DateTime(date1);
        DateTime dt2 =new DateTime(date2);
        return dt1.compareTo(dt2);
    }

    /**
     * 判断 date1 是否大于 date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isAfter(Date date1 , Date date2){
        DateTime dt1 =new DateTime(date1);
        DateTime dt2 =new DateTime(date2);
        return dt1.isAfter(dt2.getMillis());
    }

    /**
     * 判断 date1 是否大于 当前时间
     * @param date
     * @return
     */
    public static boolean isAfterNow(Date date){
        DateTime dt =new DateTime(date);
        return dt.isAfterNow();
    }

    /**
     * 判断 date1 是否小于 date2
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isBefore(Date date1 , Date date2){
        DateTime dt1 =new DateTime(date1);
        DateTime dt2 =new DateTime(date2);
        return dt1.isBefore(dt2.getMillis());
    }

    /**
     * 判断 date1 是否小于 当前时间
     * @param date
     * @return
     */
    public static boolean isBeforeNow(Date date){
        DateTime dt =new DateTime(date);
        return dt.isBeforeNow();
    }

    /**
     * 判断当前时间是否在指定时间范围内
     * @param beginDate
     * @param endDate
     * @return true  有效
     */
    public static boolean isEffective(Date beginDate , Date endDate){
        DateTime now = DateTime.now();
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);

        return  ( now.isAfter(beginDateTime.getMillis()) || now.isEqual(beginDateTime.getMillis()))
                &&(now.isBefore(endDateTime.getMillis()) || now.isEqual(endDateTime.getMillis()));
    }

    /**
     * 年 这一天是哪年
     * @param date
     * @return
     */
    public static int getYear(Date date){
        DateTime dt =new DateTime(date);
        return dt.getYear();
    }

    /**
     * 对应年的周数
     * @param date
     * @return
     */
    public static int getWeekOfWeekyear(Date date){
        DateTime dt =new DateTime(date);
        return dt.getWeekOfWeekyear();
    }

    /**
     * 对应年的月数  这一天是哪月
     * @param date
     * @return 返回日期中的 月
     */
    public static int getMonthOfYear(Date date){
        DateTime dt =new DateTime(date);
        return dt.getMonthOfYear();
    }

    /**
     * 对应年的天数
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date){
        DateTime dt =new DateTime(date);
        return dt.getDayOfYear();
    }

    /**
     * 对应月的天数   这一天是几号
     * @param date
     * @return 返回日期中的 日
     */
    public static int getDayOfMonth(Date date){
        DateTime dt =new DateTime(date);
        return dt.getDayOfMonth();
    }

    /**
     * 对应周的天数 今天是星期几
     * @param date
     * @return 周一返回 1
     */
    public static int getDayOfWeek(Date date){
        DateTime dt =new DateTime(date);
        return dt.getDayOfWeek();
    }

    /**
     * 对应天的小时数
     * @param date
     * @return 二十四小时制，返回时间中的 小时数，
     */
    public static int getHourOfDay(Date date){
        DateTime dt =new DateTime(date);
        return dt.getHourOfDay();
    }

    /**
     * 对应天的分钟数
     * @param date
     * @return
     */
    public static int getMinuteOfDay(Date date){
        DateTime dt =new DateTime(date);
        return dt.getMinuteOfDay();
    }

    /**
     * 增加指定年份并返回
     * @param date
     * @return
     */
    public static Date plusYears(Date date,int years){
        DateTime dt =new DateTime(date);
        dt = dt.plusYears(years);
        return dt.toDate();
    }

    /**
     * 增加指定月份并返回
     * @param date
     * @return
     */
    public static Date plusMonths(Date date,int months){
        DateTime dt =new DateTime(date);
        dt = dt.plusMonths(months);
        return dt.toDate();
    }

    /**
     * 增加指定星期并返回
     * @param date
     * @return
     */
    public static Date plusWeeks(Date date,int weeks){
        DateTime dt =new DateTime(date);
        dt = dt.plusWeeks(weeks);
        return dt.toDate();
    }

    /**
     * 增加指定天数并返回
     * @param date
     * @return
     */
    public static Date plusDays(Date date,int days){
        DateTime dt =new DateTime(date);
        dt = dt.plusDays(days);
        return dt.toDate();
    }

    /**
     * 增加指定小时并返回
     * @param date
     * @return
     */
    public static Date plusHours(Date date,int hours){
        DateTime dt =new DateTime(date);
        dt = dt.plusHours(hours);
        return dt.toDate();
    }

    /**
     * 增加指定分钟并返回
     * @param date
     * @return
     */
    public static Date plusMinutes(Date date,int minutes){
        DateTime dt =new DateTime(date);
        dt = dt.plusMinutes(minutes);
        return dt.toDate();
    }

    /**
     * 增加指定秒数并返回
     * @param date
     * @return
     */
    public static Date plusSeconds(Date date,int seconds){
        DateTime dt =new DateTime(date);
        dt = dt.plusSeconds(seconds);
        return dt.toDate();
    }

    /**
     * 增加指定豪秒数并返回
     * @param date
     * @return
     */
    public static Date plusMillis(Date date,int millis){
        DateTime dt =new DateTime(date);
        dt = dt.plusMillis(millis);
        return dt.toDate();
    }


    /**
     * 某月最后一天
     * @param minusMoths
     *         0     本月
     *         大于0  过去某月， 1表示上个月，2上上个月
     *         小于0  未来某月   -1 下个月，-2下下个月
     * @return
     */
    public static Date getEndDateOfMonth(int minusMoths){
        LocalDate lastDayOfMonth = new LocalDate().minusMonths(minusMoths).dayOfMonth().withMaximumValue();
        return  lastDayOfMonth.toDate();
    }

    /**
     * 当前月最后一天
     * @param date
     * @return
     */
    public static Date getEndDateOfMonth(Date date){
        DateTime dateTime =new DateTime(date);
        //withMaximumValue 返回日期属性最大值的 datetime
        DateTime endDateTime =dateTime.dayOfMonth().withMaximumValue();
        return  endDateTime.toDate();
    }

    /**
     * 当前月第一天
     * @param date
     * @return
     */
    public static Date getFirstDateOfMonth(Date date){
        DateTime dateTime =new DateTime(date);
        //withMinimumValue 返回日期属性最小值的 datetime
        DateTime firstDateTime =dateTime.dayOfMonth().withMinimumValue();
        return  firstDateTime.toDate();
    }

    /**
     * 当本周最后一天
     * @param date
     * @return
     */
    public static Date getEndDateOfWeek(Date date){
        DateTime dateTime =new DateTime(date);
        DateTime endDateTime =dateTime.dayOfWeek().withMaximumValue();
        return  endDateTime.toDate();
    }

    /**
     * 当本周第一天
     * @param date
     * @return
     */
    public static Date getFirstDateOfWeek(Date date){
        DateTime dateTime =new DateTime(date);
        DateTime firstDateTime =dateTime.dayOfWeek().withMinimumValue();
        return  firstDateTime.toDate();
    }

    /**
     * 计算时间差-年
     * 如果开始时间小于结束时间，得到的天数是正数，否则就是负数
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int yearsBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        // 相差多少年
        return Years.yearsBetween(beginDateTime,endDateTime).getYears();
    }

    /**
     * 计算时间差-月
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int monthsBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        return Months.monthsBetween(beginDateTime,endDateTime).getMonths();
    }

    /**
     * 计算时间差-天 1号-3号  返回2
     * 忽略时分秒
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int daysBetween(Date beginDate,Date endDate){
        String bs = format(beginDate,DATE_FORMAT_YYYYMMDD);
        String es = format(endDate,DATE_FORMAT_YYYYMMDD);
        DateTime beginDateTime =new DateTime(parse(bs));
        DateTime endDateTime =new DateTime(parse(es));
        return Days.daysBetween(beginDateTime,endDateTime).getDays();
    }

    /**
     * 计算时间差-小时
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int hoursBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        return Hours.hoursBetween(beginDateTime,endDateTime).getHours();
    }

    /**
     * 计算时间差-分钟
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int minutesBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        return Minutes.minutesBetween(beginDateTime,endDateTime).getMinutes();
    }

    /**
     * 计算时间差-秒
     * @param endDate
     * @return
     */
    public static int secondsBetweenNow(Date endDate){
        DateTime beginDateTime =new DateTime(new Date());
        DateTime endDateTime =new DateTime(endDate);
        return Seconds.secondsBetween(beginDateTime,endDateTime).getSeconds();
    }

    /**
     * 计算时间差-秒
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int secondsBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        return Seconds.secondsBetween(beginDateTime,endDateTime).getSeconds();
    }

    /**
     * 计算时间差-周
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int weeksBetween(Date beginDate,Date endDate){
        DateTime beginDateTime =new DateTime(beginDate);
        DateTime endDateTime =new DateTime(endDate);
        return Weeks.weeksBetween(beginDateTime,endDateTime).getWeeks();
    }

    /**
     * =================以下joda time方法=========================
     */


    /**
     * 是否是闰年
     * @return
     */
    public static boolean isLeapYear(){
        java.time.LocalDate ld = java.time.LocalDate.now();
        return ld.isLeapYear();
    }


    /**
     * 判断时间是否过期
     * @param endDate 过期日期（时间到凌晨）
     * @return true 过期
     */
    public static boolean isExpired(Date endDate){
        String st= format(endDate,DATE_FORMAT_YYYYMMDD);
        endDate =  plusDays(parse(st),1);
        return  isBeforeNow(endDate);
    }

    public static int dateInt(Date date){
        try {
            String dateStr = format(date, DATE_FORMAT_YYYYMMDD_INT);
            return Integer.valueOf(dateStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当天0点时间
     * @return
     */
    public static Date todayOfZero() {
        DateTime currentDateTime = new DateTime();
        DateTime dateTime = currentDateTime.withMillisOfDay(0);
        return dateTime.toDate();
    }

    public static void main(String[] args) {
        System.out.println(format(new Date( DatatypeConverter.parseDateTime(
                "2022-03-28T00:57:33.274+08:00").getTimeInMillis()),DATE_FORMAT_YYYYMMDD_HHMMSS));
    }
}

