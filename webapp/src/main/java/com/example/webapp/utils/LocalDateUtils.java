package com.example.webapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDK8线程安全的日期工具类
 */
@Slf4j
public class LocalDateUtils {

    private LocalDateUtils() {
    }

    /**
     * 默认 zoneId
     */
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = DEFAULT_ZONE_ID.getRules().getOffset(Instant.now());

    /**
     * 时间格式（yyyyMMdd）
     */
    public static final String DATE_SIMPLE_PATTERN = "yyyyMMdd";
    private static final DateTimeFormatter DATE_SIMPLE_FORMATTER = DateTimeFormatter.ofPattern(DATE_SIMPLE_PATTERN);

    /**
     * 时间格式（yyyy-MM-dd）
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * 时间格式（yyyy年MM月dd日）
     */
    public static final String DATE_CH_PATTERN = "yyyy年MM月dd日";
    private static final DateTimeFormatter DATE_CH_FORMATTER = DateTimeFormatter.ofPattern(DATE_CH_PATTERN);

    /**
     * 时间格式（yyyy-MM-dd HH:mm:ss）
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    /**
     * 时间格式（yyyy年MM月dd日 HH时mm分ss秒）
     */
    public static final String DATE_TIME_CH_PATTERN = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final DateTimeFormatter DATE_TIME_CH_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_CH_PATTERN);

    /**
     * 加入缓存，提高性能
     */
    private static final Map<String, DateTimeFormatter> DATE_TIME_FORMATTER_MAP = new ConcurrentHashMap<>(5);

    static {
        DATE_TIME_FORMATTER_MAP.put(DATE_PATTERN, DATE_FORMATTER);
        DATE_TIME_FORMATTER_MAP.put(DATE_SIMPLE_PATTERN, DATE_SIMPLE_FORMATTER);
        DATE_TIME_FORMATTER_MAP.put(DATE_CH_PATTERN, DATE_CH_FORMATTER);
        DATE_TIME_FORMATTER_MAP.put(DATE_TIME_PATTERN, DATE_TIME_FORMATTER);
        DATE_TIME_FORMATTER_MAP.put(DATE_TIME_CH_PATTERN, DATE_TIME_CH_FORMATTER);
    }

    /**
     * 从全局缓存中拿 pattern 对应的 formatter 或者新建
     *
     * @param pattern pattern
     * @return pattern 对应的 formatter
     */
    private static DateTimeFormatter getFormatter(String pattern) {
        return DATE_TIME_FORMATTER_MAP.getOrDefault(pattern, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前时间，get可以获取年月日时分秒
     * <br/>DateUtil.now().getYear();
     * <br/>DateUtil.now().getMonth();
     * <br/>DateUtil.now().getDayOfYear();
     * <br/>DateUtil.now().getDayOfMonth();
     * <br/>...
     *
     * @return LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前时间，get可以获取年月日时分秒
     * <br/>DateUtil.now().getYear();
     * <br/>DateUtil.now().getMonth();
     * <br/>DateUtil.now().getDayOfYear();
     * <br/>DateUtil.now().getDayOfMonth();
     * <br/>...
     *
     * @return LocalDateTime
     */
    public static Date nowDate() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = now().atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 获取当前日期 （yyyyMMdd）
     *
     * @return 当前日期
     */
    public static String getTodaySimple() {
        return now().format(DATE_SIMPLE_FORMATTER);
    }

    /**
     * 获取当前日期 （yyyy-MM-dd）
     *
     * @return 当前日期
     */
    public static String getToday() {
        return now().format(DATE_FORMATTER);
    }

    /**
     * 获取当前时间 （yyyy-MM-dd HH:mm:ss）
     *
     * @return 当前时间
     */
    public static String getTodayNow() {
        return now().format(DATE_TIME_FORMATTER);
    }

    /**
     * 获取当前时间
     *
     * @param pattern 格式，如：DateUtils.DATE_PATTERN
     * @return 当前时间
     */
    public static String getTodayNow(String pattern) {
        return now().format(getFormatter(pattern));
    }

    /**
     * 字符串转成 Date 类型
     *
     * @param str     日期字符串
     * @param pattern 日期的格式：如：DateUtils.DATE_PATTERN
     * @return Date 类型的时间
     */
    public static Date toDate(String str, String pattern) {
        Assert.hasText(str, String.format("STR: [%s] 参数非法", str));
        Assert.hasText(pattern, String.format("PATTERN: [%s] 参数非法", pattern));
        Date date;
        try {
            date = toDate(LocalDateTime.parse(str, getFormatter(pattern)));
        } catch (Exception exception) {
            date = toDate(LocalDate.parse(str, getFormatter(pattern)));
        }
        return date;
    }

    /**
     * 字符串日期从一个格式转换到另外一个格式
     *
     * @param date     日期
     * @param pattern1 格式，如：DateUtils.DATE_PATTERN
     * @param pattern2 格式，如：DateUtils.DATE_PATTERN
     * @return 返回指定格式字符串时间
     */
    public static String convert(String date, String pattern1, String pattern2) {
        return format(toDate(date, pattern1), pattern2);
    }

    /**
     * 日期格式化为指定格
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_PATTERN
     * @return 返回指定格式字符串时间
     */
    public static String format(Date date, String pattern) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        Assert.hasText(pattern, String.format("PATTERN: [%s] 参数非法", pattern));
        return formatLocalDateTime(toLocalDateTime(date), pattern);
    }

    /**
     * LocalDateTime 类型的时间格式化为指定格式的字符串
     *
     * @param localDateTime LocalDateTime 类型的时间
     * @param pattern       格式，如 DateUtils.DATE_PATTERN
     * @return 指定格式字符串时间
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        Assert.notNull(localDateTime, "传入的日期不可以为 [null]");
        Assert.hasText(pattern, String.format("PATTERN: [%s] 参数非法", pattern));
        return localDateTime.format(getFormatter(pattern));
    }

    /**
     * LocalDate 转成 Date
     *
     * @param localDate LocalDate 类型日期
     * @return Date 类型的日期
     */
    public static Date toDate(LocalDate localDate) {
        Assert.notNull(localDate, "传入的日期不可以为 [null]");
        return Date.from(localDate.atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * LocalDateTime 转成 Date
     *
     * @param localDateTime LocalDateTime 类型时间
     * @return Date 类型的时间
     */
    public static Date toDate(LocalDateTime localDateTime) {
        Assert.notNull(localDateTime, "传入的日期不可以为 [null]");
        return Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 字符串转成 LocalDate 类型的日期
     *
     * @param str     字符串日期
     * @param pattern 字符串格式，如 DateUtils.DATE_PATTERN
     * @return LocalDate 类型的日期
     */
    public static LocalDate toLocalDate(String str, String pattern) {
        Assert.hasText(str, String.format("STR: [%s] 参数非法", str));
        Assert.hasText(pattern, String.format("PATTERN: [%s] 参数非法", pattern));
        return LocalDate.parse(str, getFormatter(pattern));
    }

    /**
     * Date 类型日期转成 LocalDate 类型的日期
     *
     * @param date Date 类型的日期
     * @return LocalDate 类型的日期
     */
    public static LocalDate toLocalDate(Date date) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * 字符串类型的时间转成 LocalDateTime 类型的时间
     *
     * @param str     字符串时间
     * @param pattern 字符串时间格式
     * @return LocalDateTime 类型的时间
     */
    public static LocalDateTime toLocalDateTime(String str, String pattern) {
        Assert.hasText(str, String.format("STR: [%s] 参数非法", str));
        Assert.hasText(pattern, String.format("PATTERN: [%s] 参数非法", pattern));
        return LocalDateTime.parse(str, getFormatter(pattern));
    }


    /**
     * Date 类型的时间转成 LocalDateTime 类型的时间
     *
     * @param date Date 类型的时间
     * @return LocalDateTime 类型的时间
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * d1是否比d2小
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return boolean d1是否比d2小
     */
    public static boolean isBefore(Date d1, Date d2) {
        return toLocalDateTime(d1).isBefore(toLocalDateTime(d2));
    }

    /**
     * d1是否比d2大
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return boolean d1是否比d2大
     */
    public static boolean isAfter(Date d1, Date d2) {
        return toLocalDateTime(d1).isAfter(toLocalDateTime(d2));
    }

    /**
     * d1是否与d2相等
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return boolean d1是否与d2相等
     */
    public static boolean isEqual(Date d1, Date d2) {
        return toLocalDateTime(d1).isEqual(toLocalDateTime(d2));
    }

    /**
     * 日期相差天数
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return 相差天数
     */
    public static long betweenDays(Date d1, Date d2) {
        return ChronoUnit.DAYS.between(toLocalDate(d1), toLocalDate(d2));
    }

    /**
     * 日期相差月数
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return 相差月数
     */
    public static long betweenMonths(Date d1, Date d2) {
        return ChronoUnit.MONTHS.between(toLocalDate(d1), toLocalDate(d2));
    }

    /**
     * 日期相差年数
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return 相差年数
     */
    public static long betweenYears(Date d1, Date d2) {
        return ChronoUnit.YEARS.between(toLocalDate(d1), toLocalDate(d2));
    }

    /**
     * 获取某月的最后一天
     *
     * @param date 日期
     * @return 某月的最后一天
     */
    public static Date lastDayOfMonth(Date date) {
        return toDate(toLocalDate(date).with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 获取某月的第一天
     *
     * @param date 日期
     * @return 某月的第一天
     */
    public static Date firstDayOfMonth(Date date) {
        return toDate(toLocalDate(date).withDayOfMonth(1));
    }

    /**
     * 获取某年的最后一天
     *
     * @param date 日期
     * @return 某年的最后一天
     */
    public static Date lastDayOfYear(Date date) {
        return toDate(toLocalDate(date).with(TemporalAdjusters.lastDayOfYear()));
    }

    /**
     * 获取某年的第一天
     *
     * @param date 日期
     * @return 某年的第一天
     */
    public static Date firstDayOfYear(Date date) {
        return toDate(toLocalDate(date).withDayOfYear(1));
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusSeconds(seconds));
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusMinutes(minutes));
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusHours(hours));
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusDays(days));
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusWeeks(weeks));
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusMonths(months));
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        Assert.notNull(date, "传入的日期不可以为 [null]");
        return toDate(toLocalDateTime(date).plusYears(years));
    }

    /**
     * 系统参数 ----> 替换日期
     *
     * @param lastExecuteDate
     * @param hql
     * @return
     */
    public static String replaceDate(Date lastExecuteDate, String hql) {
        try {
            // 截取 @date_ad,-1,dd,day@ 时间参数
            while (hql.contains("@")) {
                String dateStr = hql.substring(hql.indexOf("@"), hql.indexOf("@", hql.indexOf("@") + 1) + 1);
                String[] split = dateStr.split(",");
                int num = Integer.parseInt(split[1]);
                String params = split[2].toUpperCase();
                String type = split[3].toUpperCase();
                String result = "";
                Date calcDate = new Date();
                switch (params) {
                    case "YYYY":
                        calcDate = LocalDateUtils.addDateYears(lastExecuteDate, num);
                        break;
                    case "MM":
                        calcDate = LocalDateUtils.addDateMonths(lastExecuteDate, num);
                        break;
                    case "DD":
                        calcDate = LocalDateUtils.addDateDays(lastExecuteDate, num);
                        break;
                    case "HH":
                        calcDate = LocalDateUtils.addDateHours(lastExecuteDate, num);
                        break;
                }

                switch (type) {
                    case "HOUR@":
                        // 小时
                        result = LocalDateUtils.format(calcDate, "yyyyMMddHH");
                        break;
                    case "DAY@":
                        // 日
                        result = LocalDateUtils.format(calcDate, "yyyyMMdd");
                        break;
                    case "DATETIME@":
                        // 日
                        result = LocalDateUtils.format(calcDate, "yyyy-MM-dd HH:mm:ss");
                        break;
                    case "WEEK@":
                        // 星期
                        int value = LocalDateUtils.toLocalDate(calcDate).getDayOfWeek().getValue();
                        result = String.valueOf(value);
                        break;
                }

                hql = hql.replace(dateStr, result);
            }
        } catch (NumberFormatException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return "error" + e.getMessage();
        }
        return hql;
    }
}


