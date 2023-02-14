package com.thtf.office.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: liwencai
 * @Date: 2022/12/27 13:07
 * @Description:
 */
public class CodeGeneratorUtil {


    private static final AtomicLong ORDER_SEQ = new AtomicLong(1);

    public static  final String DATE_FMT = "yyyy-MM-dd";
    public static String formatDate(Date date, String pattern) {
        String formatDate = null;
        if (StringUtils.isNotBlank(pattern)) {
            formatDate = DateFormatUtils.format(date, pattern);
        } else {
            formatDate = DateFormatUtils.format(date, DATE_FMT);
        }
        return formatDate;
    }

    public static String getAllTime2() {
        return formatDate(new Date(), "yyyyMMddHHmmssSSS");
    }

    public static String getCode(String prefix) {
        String orderNo = getAllTime2();
        orderNo += String.format("%03d", ORDER_SEQ.incrementAndGet() % 999 + 1);
        return prefix+orderNo;
    }
}
