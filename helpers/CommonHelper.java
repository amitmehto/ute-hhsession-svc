package com.rogers.ute.hhsessionsvc.helpers;

import com.rogers.ute.hhsessionsvc.exceptions.HHSessionException;
import com.rogers.ute.hhsessionsvc.models.hhsessionresponses.ActivationResponse;
import com.rogers.ute.hhsessionsvc.models.hhsessionresponses.SessionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.regex.PatternSyntaxException;

/**
 * Common helper or utility methods for this module
 */
public class CommonHelper {

    private static Logger logger = LoggerFactory.getLogger(CommonHelper.class);

    public static final String ID_TIME_ZONE = "Z";
    /**
     * Response Error Codes
     */
    public static final String CODE_DEFAULT_SYSTEM_ERROR = "3002";

    /**
     * Method that returns ActivationResponse object from HHSessionException
     */
    public static ActivationResponse getActivationResponseFromException(HHSessionException hhse) {
        ActivationResponse response = new ActivationResponse();
        if (null != hhse.getHhCode()) {
            response.setResultCode(hhse.getHhCode());
        } else {
            response.setResultCode(CommonHelper.CODE_DEFAULT_SYSTEM_ERROR);
        }
        response.setResultDesc(hhse.getHhMessage());
        return response;
    }

    /**
     * Method that returns SessionResponse object from HHSessionException
     */
    public static SessionResponse getSessionResponseFromException(HHSessionException hhse) {
        SessionResponse response = new SessionResponse();
        if (null != hhse.getHhCode()) {
            response.setResultCode(hhse.getHhCode());
        } else {
            response.setResultCode(CommonHelper.CODE_DEFAULT_SYSTEM_ERROR);
        }
        return response;
    }


    /**
     * Method that converts unix time to java Date
     */
    public static String convertUnixTime(String unixTime) {
        try {
            ZonedDateTime time = Instant.ofEpochSecond(Long.valueOf(removeSpacesFromString(unixTime)))
                    .atZone(ZoneId.of(ID_TIME_ZONE));
            return time.toString();
        } catch (DateTimeException | ArithmeticException | NumberFormatException e) {
            logger.error(e.getMessage());
            return unixTime;
        }
    }


    // TODO
    public static boolean isLastModifiedTimeValid(String utcTime, Date lastModifiedTime) {
        return false;
    }


    /**
     * Method that removes whitespaces
     */
    public static String removeSpacesFromString(String stringToUse) {
        try {
            return stringToUse.replaceAll("\\s+", "");
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            return stringToUse;
        }
    }


    /**
     * Method that checks if object is null or empty (in case of string)
     * @param objects
     * @return
     */
    public static boolean isNullOrEmpty(Object... objects) {
        for (Object obj : objects) {
            if  (null == obj) {
                return true;
            }
            if (obj instanceof String) {
                return ((String) obj).isEmpty();
            }
        }
        return false;
    }


    public static boolean isTimeForCacheRefresh(Date lastUpdateTime, int refreshInSeconds) {
        Date currentTime = new Date(System.currentTimeMillis());
        long diffInMinutes = ((currentTime.getTime() - lastUpdateTime.getTime()) / (60000)) % 60;
        if (diffInMinutes >= (refreshInSeconds/60)) {
            return true;
        }
        return false;
    }

}
