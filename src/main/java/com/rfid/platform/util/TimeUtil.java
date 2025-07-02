package com.rfid.platform.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class TimeUtil {


    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter dayNoLineFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter monthNoLineFormatter = DateTimeFormatter.ofPattern("yyyyMM");

    private static final DateTimeFormatter secondNoLineFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private TimeUtil() {
    }

    public static final LocalDateTime getSysDate() {
        return LocalDateTime.now();
    }


    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return dateTime;
    }

    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            return instant.toEpochMilli();
        }
        return 0L;
    }

    public static String getDateFormatterString(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return dateTimeFormatter.format(localDateTime);
        }
        return "";
    }

    public static LocalDateTime parseDateFormatterString(String dateTime) {
        if (StringUtils.isNotBlank(dateTime)) {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        }
        return null;
    }

    public static String getMonthNoLineString(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return monthNoLineFormatter.format(localDateTime);
        }
        return "";
    }

    public static String getDayNoLineString(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return dayNoLineFormatter.format(localDateTime);
        }
        return "";
    }

    public static String getSecondNoLineString(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return secondNoLineFormatter.format(localDateTime);
        }
        return "";
    }


    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        LocalDateTime current;
        if (Objects.nonNull(localDateTime)) {
            current = localDateTime;
        } else {
            current = getSysDate();
        }

        return current.toLocalDate().atStartOfDay();
    }


    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        LocalDateTime current;
        if (Objects.nonNull(localDateTime)) {
            current = localDateTime;
        } else {
            current = getSysDate();
        }

        return LocalDateTime.of(current.toLocalDate(), LocalTime.MAX);
    }





    /**
     * 获取指定日期所在月份的第一天第一秒
     * @param localDateTime 指定日期时间
     * @return 所在月份的第一天第一秒
     */
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
        LocalDateTime current;
        if (Objects.nonNull(localDateTime)) {
            current = localDateTime;
        } else {
            current = getSysDate();
        }
        
        return LocalDateTime.of(current.getYear(), current.getMonth(), 1, 0, 0, 0);
    }

    /**
     * 获取指定日期所在月份的最后一天最后一秒
     * @param localDateTime 指定日期时间
     * @return 所在月份的最后一天最后一秒
     */
    public static LocalDateTime getLastDayOfMonth(LocalDateTime localDateTime) {
        LocalDateTime current;
        if (Objects.nonNull(localDateTime)) {
            current = localDateTime;
        } else {
            current = getSysDate();
        }
        
        LocalDate lastDay = current.toLocalDate()
                .withDayOfMonth(current.toLocalDate().lengthOfMonth());
        return LocalDateTime.of(lastDay, LocalTime.MAX);
    }

}
