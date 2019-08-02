package com.jindi.land.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;


public class DateUtils {

  public static final long ONE_DAY_LONG = 86400000L;

  public static final long TWO_DAY_LONG = 172800000L;

  public static final String SUFFIX = " 00:00:00.0";
  /**
   * 英文简写（默认）如：2010-12-01
   */
  public static final String FORMAT_SHORT = "yyyy-MM-dd";

  private static final String FORMAT_SHORT_ONLY_NUM = "yyyyMMdd";

  /**
   * 英文全称 如：2010-12-01 23:15:06
   */
  public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
  /**
   * 精确到毫秒的完整时间 如：yyyy-MM-dd HH:mm:ss.S
   */
  public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
  /**
   * 中文简写 如：2010年12月01日
   */
  public static final String FORMAT_SHORT_CN = "yyyy年MM月dd日";
  /**
   * 中文全称 如：2010年12月01日 23时15分06秒
   */
  public static final String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
  /**
   * 精确到毫秒的完整中文时间
   */
  public static final String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";

  private static final SimpleDateFormat shortFormat = new SimpleDateFormat("yyyyMMdd");

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public static Date getYesterday() {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    Date date = calendar.getTime();
    return date;
  }

  public static String getYesterdayStr() {
    Date yesterday = getYesterday();
    synchronized (DATE_FORMAT) {
      return DATE_FORMAT.format(yesterday);
    }
  }

  public static List<String> getBeforeDays(int days, String format) {
    List<String> list = new ArrayList<>();
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    for (int i = 1; i <= days; i++) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -i);
      list.add(formatter.format(calendar.getTime()));
    }
    return list;
  }

  public static List<String> getAfterDays(int days, String format) {
    List<String> list = new ArrayList<>();
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    for (int i = 0; i <= days; i++) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, +i);
      list.add(formatter.format(calendar.getTime()));
    }
    return list;
  }

  /**
   * 获得默认的 date pattern
   */
  public static String getDatePattern() {
    return FORMAT_LONG;
  }

  /**
   * 根据预设格式返回当前日期
   */
  public static String getNow() {
    return format(new Date());
  }

  /**
   * 根据用户格式返回当前日期
   */
  public static String getNow(String format) {
    return format(new Date(), format);
  }

  /**
   * 使用预设格式格式化日期
   */
  public static String format(Date date) {
    return format(date, getDatePattern());
  }

  public static String format(Long time) {
    return format(new Date(time), FORMAT_SHORT) + SUFFIX;
  }

  public static String formatShort(Long time) {
    return format(new Date(time), FORMAT_SHORT);
  }

  /**
   * 使用用户格式格式化日期
   *
   * @param date 日期
   * @param pattern 日期格式
   */
  public static String format(Date date, String pattern) {
    String returnValue = "";
    if (date != null) {
      SimpleDateFormat df = new SimpleDateFormat(pattern);
      returnValue = df.format(date);
    }
    return (returnValue);
  }

  /**
   * 使用预设格式提取字符串日期
   *
   * @param strDate 日期字符串
   */
  public static Date parse(String strDate) {
    return parse(strDate, getDatePattern());
  }

  /**
   * 使用用户格式提取字符串日期
   *
   * @param strDate 日期字符串
   * @param pattern 日期格式
   */
  public static Date parse(String strDate, String pattern) {
    if (strDate == null || strDate.length() == 0) {
      return null;
    }
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    try {
      return df.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 在日期上增加数个整月
   *
   * @param date 日期
   * @param n 要增加的月数
   */
  public static Date addMonth(Date date, int n) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.MONTH, n);
    return cal.getTime();
  }

  /**
   * 在日期上增加天数
   *
   * @param date 日期
   * @param n 要增加的天数
   */
  public static Date addDay(Date date, int n) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, n);
    return cal.getTime();
  }

  /**
   * 获取时间戳
   */
  public static String getTimeString() {
    SimpleDateFormat df = new SimpleDateFormat(FORMAT_FULL);
    Calendar calendar = Calendar.getInstance();
    return df.format(calendar.getTime());
  }

  /**
   * 获取日期年份
   *
   * @param date 日期
   */
  public static String getYear(Date date) {
    return format(date).substring(0, 4);
  }

  /**
   * 按默认格式的字符串距离今天的天数
   *
   * @param date 日期字符串
   */
  public static int countDays(String date) {
    long t = Calendar.getInstance().getTime().getTime();
    Calendar c = Calendar.getInstance();
    c.setTime(parse(date));
    long t1 = c.getTime().getTime();
    return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
  }

  public static long diff(Date d1, Date d2) {
    long ld1 = d1.getTime();
    long ld2 = d2.getTime();
    return ld1 - ld2;
  }

  /**
   * 获得两个时间的差值【以“天”为单位】
   */
  public static int getDiffDays(Date startDate, Date endDate) {
    if (null == startDate || null == endDate) {
      return -1;
    }
    long intervalMilli = endDate.getTime() - startDate.getTime();
    int f1 = new BigDecimal(((float) intervalMilli / (24 * 60 * 60 * 1000)))
        .setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
    return f1;
  }

  /**
   * 获得两个时间的差值
   */
  public static int getDiffMinute(Date startDate, Date endDate) {
    if (null == startDate || null == endDate) {
      return -1;
    }
    long intervalMilli = endDate.getTime() - startDate.getTime();
    int f1 = new BigDecimal(((float) intervalMilli / (60 * 1000)))
        .setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
    return f1;
  }

  /**
   * 按用户格式字符串距离今天的天数
   *
   * @param date 日期字符串
   * @param format 日期格式
   */
  public static int countDays(String date, String format) {
    long t = Calendar.getInstance().getTime().getTime();
    Calendar c = Calendar.getInstance();
    c.setTime(parse(date, format));
    long t1 = c.getTime().getTime();
    return (int) (t / 1000 - t1 / 1000) / 3600 / 24;
  }

  /**
   * 根据格式转化时间
   *
   * @param strDate 2015-11-12
   * @param formartStr yyyy-MM-dd
   */
  public static Date dateConvert(String strDate, String formartStr) {
    if (StringUtils.isEmpty(strDate)) {
      return null;
    }
    DateFormat format = new SimpleDateFormat(formartStr);
    Date date = null;
    try {
      date = format.parse(strDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  //public static void main(String[] args) throws Exception {
  //
  //  long l = Calendar.getInstance().getTimeInMillis();
  //  System.out.println(format(l));
  //  Date d2 = new Date();
  //  Thread.sleep(1);
  //  long diff = diff(new Date(), d2);
  //  System.out.println(diff);
  //
  //  Date date = formatDate("2018-04-20");
  //  System.out.println(judgeDaysBefore(date, 2));
  //}

  public static Date formatDate(String string) {
    try {
      if (StringUtils.isEmpty(string) || string.length() < 4) {
        return null;
      }
      string = string.trim();
      string = string.replace("一", "1").replace("二", "2").replace("三", "3").replace("四", "4")
          .replace("五", "5").replace("六", "6").replace("七", "7").replace("八", "8").replace("九", "9")
          .replace("十", "").replace("〇", "0").replace("零", "0").replace("o", "0").replace("O", "0")
          .replace("軨", "年").replace("爖", "日");
      if (matchRegex("^\\d{4}-\\d{1,2}-\\d{1,2}", string)) {
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT);
      }
      if (matchRegex("^\\d{4}\\d{1,2}\\d{1,2}", string)) {
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT_ONLY_NUM);
      }
      if (matchRegex("^\\d{4}\\.\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}:\\d{2}", string)) {
        string = string.replace(".", "-");
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}\\.\\d{1,2}\\.\\d{1,2} \\d{2}:\\d{2}", string)) {
        string = string.replace(".", "-") + ":00";
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}", string)) {
        string = string.replace(".", "-");
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT);
      }
      if (matchRegex("^\\d{4}-\\d{1,2}-\\d{1,2}", string)) {
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT);
      }
      if (matchRegex("^\\d{4}年\\d{1,2}月\\d{1,2}日 \\d{2}:\\d{2}:\\d{2}", string)) {
        string = string.replace("年", "-").replace("月", "-").replace("日", "");
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}年\\d{1,2}月\\d{1,2}日 \\d{2}:\\d{2}", string)) {
        string = string.replace("年", "-").replace("月", "-").replace("日", "") + ":00";
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}年\\d{1,2}月\\d{1,2}日", string)) {
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT_CN);
      }
      if (matchRegex("^\\d{13}", string)) {
        return new Date(Long.parseLong(string));
      }
      if (matchRegex("^\\d{1,2}/\\d{1,2} \\d{2}:\\d{2}", string)) {
        string = string.replace("/", "-") + ":00";
        string = Calendar.getInstance().get(Calendar.YEAR) + "-" + string;
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{2}:\\d{2}:\\d{2}", string)) {
        string = string.replace("/", "-");
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{2}:\\d{2}", string)) {
        string = string.replace("/", "-") + ":00";
        return DateUtils.parse(string, DateUtils.FORMAT_LONG);
      }
      if (matchRegex("^\\d{4}/\\d{1,2}/\\d{1,2}", string)) {
        string = string.replace("/", "-");
        return DateUtils.parse(string, DateUtils.FORMAT_SHORT);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 返回字符串与正则表达式的匹配结果
   *
   * @param regex 正则表达式，不可以为null，否则异常 value要匹配的字符串，不可以为null，否则异常
   * @return 返回字符串与正则表达式的匹配结果
   * @author huling
   */
  public static boolean matchRegex(String regex, String value) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    return matcher.find();
  }

  /**
   * 计算指定时间是否距离今天时间小于days天
   *
   * @param assignDate 需判断的时间
   * @param days 时间范围
   * @return true if and only if assignDate距离今天时间小于days天
   */
  public static boolean judgeDaysBefore(Date assignDate, int days) {
    if (assignDate == null) {
      return false;
    }
    Calendar calendar = Calendar.getInstance();
    Date today = new Date();
    calendar.setTime(today);
    calendar.add(Calendar.DAY_OF_MONTH, -days);
    return assignDate.after(calendar.getTime());
  }

  /**
   * 判断两个日期字符串是否表示相同日期，忽略时分秒
   */
  public static boolean equalsDateStrIgnoreHms(String dateStr1, String dateStr2) {
    dateStr1 = dateStr1.trim();
    dateStr2 = dateStr2.trim();
    if (dateStr1.equals(dateStr2)) {
      return true;
    }
    if (StringUtils.isEmpty(dateStr1) || StringUtils.isEmpty(dateStr2)) {
      return false;
    }
    Date date1 = DateUtils.formatDate(dateStr1);
    Date date2 = DateUtils.formatDate(dateStr2);
    return DateUtils.equalsIgnoreHms(date1, date2);
  }

  /**
   * 比较时间，忽略时分秒
   */
  public static boolean equalsIgnoreHms(Date date1, Date date2) {
    if (date1 == date2) {
      return true;
    }
    if (date1 == null || date2 == null) {
      return false;
    }
    synchronized(shortFormat){
      return shortFormat.format(date1).equals(shortFormat.format(date2));
    }
  }

  /**
   * 比较时间
   */
  public static boolean curDateLaterAnotherDate(Date curDate, Date anotherDate) {
    if (curDate != null && anotherDate != null) {
      if (curDate.after(anotherDate)) {
        return true;
      }
    }
    if (curDate == null || anotherDate == null) {
      if (curDate == null && anotherDate == null) {
        return true;
      }
      if (curDate == null && anotherDate != null) {
        return false;
      }
      if (curDate != null && anotherDate == null) {
        return false;
      }
    }
    return false;
  }

  public static String formatMilliSecondTime(Long ms) {

    if (ms == null || ms <= 0) {
      return "";
    }

    Integer ss = 1000;
    Integer mi = ss * 60;
    Integer hh = mi * 60;
    Integer dd = hh * 24;

    Long day = ms / dd;
    Long hour = (ms - day * dd) / hh;
    Long minute = (ms - day * dd - hour * hh) / mi;
    Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
    Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

    StringBuffer sb = new StringBuffer();
    if (day > 0) {
      sb.append(day + "天");
    }
    if (hour > 0) {
      sb.append(hour + "小时");
    }
    if (minute > 0) {
      sb.append(minute + "分");
    }
    if (second > 0) {
      sb.append(second + "秒");
    }
    if (milliSecond > 0) {
      sb.append(milliSecond + "毫秒");
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    System.out.println(formatDate("二〇〇九年九月二十三日"));
  }
}
