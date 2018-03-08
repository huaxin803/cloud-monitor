package com.jyall.tools;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * 时间处理工具，拿到当前时间并格式化
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
public class TimeTool {

    /** 线程安全型 用于时间格式化 **/
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static String getTime(){

        return DATE_TIME_FORMATTER.format(LocalDate.now());
    }
}
