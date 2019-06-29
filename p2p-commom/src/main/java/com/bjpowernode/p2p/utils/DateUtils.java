package com.bjpowernode.p2p.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author :动力节点张开
 * 2019-6-3
 */
public class DateUtils {

    /**
     * 添加天数，返回日期
     * @param date
     * @param count
     * @return
     */
    public static Date getDateByAddDays(Date date, Integer count) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.add(Calendar.DATE, count);

        return calendar.getTime();
    }

    /**
     * 添加月份返回日期
     * @param date
     * @param count
     * @return
     */
    public static Date getDateByAddMonths(Date date, Integer count) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);

        calendar.add(Calendar.MONTH, count);

        return calendar.getTime();
    }

    /**
     * 创建时间戳
     * @return
     */
    public static String getDimeTamp() {
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        return time;
    }
}
