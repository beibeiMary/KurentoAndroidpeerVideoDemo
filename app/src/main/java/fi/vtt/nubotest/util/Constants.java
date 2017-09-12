package fi.vtt.nubotest.util;

/**
 * Created by GleasonK on 7/30/15.
 */
public class Constants {
    //public static final String SHARED_PREFS = "fi.vtt.nubotest.SHARED_PREFS";
    public static final String USER_NAME    = "fi.vtt.nubotest.SHARED_PREFS.USER_NAME";
    public static final String CALL_USER    = "fi.vtt.nubotest.SHARED_PREFS.CALL_USER";
    public static final String STDBY_SUFFIX = "-stdby";

    public static final String PUB_KEY = "pub-c-9d0d75a5-38db-404f-ac2a-884e18b041d8";
    public static final String SUB_KEY = "sub-c-4e25fb64-37c7-11e5-a477-0619f8945a4f";

    public static final String JSON_CALL_USER = "call_user";
    public static final String JSON_CALL_TIME = "call_time";
    public static final String JSON_OCCUPANCY = "occupancy";
    public static final String JSON_STATUS    = "status";

    // JSON for user messages
    public static final String JSON_USER_MSG  = "user_message";
    public static final String JSON_MSG_UUID  = "msg_uuid";
    public static final String JSON_MSG       = "msg_message";
    public static final String JSON_TIME      = "msg_timestamp";
    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_OFFLINE   = "Offline";
    public static final String STATUS_BUSY      = "Busy";
    public static final String SERVER_NAME      = "serverName";
    public static final String DEFAULT_SERVER   = "wss://192.168.1.113:8443/room";
//    public static final String DEFAULT_SERVER   = "wss://193.166.161.152:8443/room";
    public static String SERVER_ADDRESS_SET_BY_USER = "wss://192.168.1.113:8443/room";
    public static final String ROOM_NAME    = "fi.vtt.nubotest.SHARED_PREFS.ROOM_NAME";
    public static int id    = 0;
}
