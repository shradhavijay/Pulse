package com.pmpulse.data;

import java.util.regex.Pattern;

/**
 * Created by root on 2/11/15.
 */
public class KeyValues {

    public static boolean isDebug = false;

    //for service
    public static String TYPE_FREE_AUDIO = "FREE_AUDIO";
    public static String TYPE_CHAPTER = "CHAPTER";
    public static String TYPE_PLAYLIST = "PLAYLIST";

    public static String POSTION_PREV = "POSTION_PREV";
    public static String KEY_FEATURE_NAME = "KEY_FEATURE_NAME";
    public static String KEY_CHAPTER_NAME = "KEY_CHAPTER_NAME";
    public static String KEY_EXAM_NAME = "KEY_EXAM_NAME";
    public static String KEY_EXAM_NUMBER = "KEY_EXAM_NUMBER";
    public static String KEY_EXAM_NUMBER_HISTORY = "KEY_EXAM_NUMBER_HISTORY";
    public static String KEY_EXAM_NUMBER_SELECTED = "KEY_EXAM_NUMBER_SELECTED";

    public static int TIMEOUT = 30000;
    public static String urlLogin = "http://pmdhwaniapi.pm-pulse.com/API/Login";
    public static String urlGetAudio = "http://pmdhwaniapi.pm-pulse.com/API/GetAudios";
    public static String urlLogout = "http://pmdhwaniapi.pm-pulse.com/API/Logout";
    public static String urlFreeAudio = "http://pmdhwaniapi.pm-pulse.com/API/FreeAudio";
    public static String urlAddRating = "http://pmdhwaniapi.pm-pulse.com/API/RateAudio";
    public static String urlIsLoggedIn = "http://pmdhwaniapi.pm-pulse.com/api/CheckUserStatus";
    public static String urlUpdateReadStatus = "http://pmdhwaniapi.pm-pulse.com/api/UpdateAudio";
    public static String urlLoginA2Z = "http://a2zservice.pm-pulse.com/A2ZService.svc/Rest/UserAuthentication";

    public static String udid = "UDID";

    public static boolean isViewReview = false;
    //for abc@def.com
    public static Pattern emailAddress = Pattern.compile
            (
                    "[a-z0-9A-Z]{1,30}"
                            + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                            + "[a-z0-9A-Z]{1,20}"
            );
    //for abc@xyx.co.in
    public static Pattern emailAddressDouble = Pattern.compile
            (
                    "[a-z0-9A-Z]{1,30}"
                            + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                            + "[a-z0-9A-Z]{1,20}" + "\\."
                            + "[a-z0-9A-Z]{1,20}"
            );
    //for abc.xyz@fgf.com
    public static Pattern emailAddressDot = Pattern.compile(
            "[a-z0-9A-Z]{1,30}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
                    + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
    );
    //for abc.sd@xyz.co.in
    public static Pattern emailAddressDotDouble = Pattern.compile(
            "[a-z0-9A-Z]{1,30}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
                    + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
    );
    //for abc_xyz@pqr.com
    public static Pattern emailAddressUnderscore = Pattern.compile(
            "[a-z0-9A-Z]{1,30}" + "\\_"
                    + "[a-z0-9A-Z]{1,20}"
                    + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
    );
    //abc_sdf@xyz.co.in
    public static Pattern emailAddressUnderscoreDouble = Pattern.compile(
            "[a-z0-9A-Z]{1,30}" + "\\_"
                    + "[a-z0-9A-Z]{1,20}"
                    + "@" + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}" + "\\."
                    + "[a-z0-9A-Z]{1,20}"
    );
    public static Pattern alphaNumeric = Pattern.compile(
            "[a-zA-Z0-9]{1,50}"
    );
}
