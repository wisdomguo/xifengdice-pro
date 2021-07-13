package com.wisdomguo.xifeng.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateTimeUtil
 *
 * @author wisdom-guo
 * @since 2021/7/8
 */
public class DateTimeUtil {

    static SimpleDateFormat yyyy_MM_dd_hh_mm = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    /**
     * 日期添加到分钟得到新时间
     * @param day 开始时间
     * @param x	  相隔分钟数
     * @return
     *
     */
    public static Date addDateMinute(String day, int x) {
        //入参的格式
        // 24小时制
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(day);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (date == null){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 24小时制
        cal.add(Calendar.MINUTE, x);
        //得到结算后的结果 yyyy-MM-dd HH:mm
        date = cal.getTime();
        cal = null;
        return date;
    }

    /**
     * 用SimpleDateFormat计算时间差
     * @throws ParseException
     */
    public static void getTimeDifference(String date) throws ParseException {
        Date parse = yyyyMMddHHmmss.parse(date);
        long between = System.currentTimeMillis() - parse.getTime();
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        System.out.println(day + "天" + hour + "小时" + min + "分" + s + "秒");
    }

    /**
     * 计算分钟差
     */
    public static long getTimeMinDifference(Date date) throws ParseException {
        long between = System.currentTimeMillis() - date.getTime();
        long min = ((between / (60 * 1000)));
        return min;
    }
    /**
     * 计算秒数差
     */
    public static long getTimeSecondDifference(Date date) throws ParseException {
        long between = System.currentTimeMillis() - date.getTime();
        long s = (between / 1000);
        return s;
    }

}
