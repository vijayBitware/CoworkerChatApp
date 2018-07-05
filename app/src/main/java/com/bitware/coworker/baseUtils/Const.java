package com.bitware.coworker.baseUtils;

import android.media.RingtoneManager;
import android.net.Uri;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

/**
 * Created by KrishnaDev on 1/10/17.
 */

////wohoooo

public class Const {

    //image Upload from amazon s3
    public static final String amazons3ServerImagePath = "https://s3.us-east-2.amazonaws.com/zoechatapp/";
    public static final String buildvversion = "24";
    //VoiceCallActivity common
    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;
    public static final String ACTION_KEY_CHANNEL_NAME = "ecHANEL";
    //web service parameter keys
    public static final String PHONE = "mobileNumber";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String NAME = "name";
    public static final String ZOE_CHAT_ID = "zoechatid";
    public static final String IMAGE = "image";
    public static final String STATUS = "status";
    public static final String DEVICE_TOKEN = "pushNotificationId";
    public static final String CONTACTS = "contacts";
    public static String accessKey = "AKIAIRRKLPMPBLZI4TSQ";
    public static String secretKey = "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";
    public static String bucket_name = "zoechatapp";

    public static String chatSocketURL = "http://13.59.215.19:3000";
    //    public static Region Regionss3=Region.getRegion(Regions.AP_NORTHEAST_2);
//    public static String Endpoint="https://s3-ap-northeast-2.amazonaws.com";
//    public static String ENDPOINT=Endpoint;
    public static Regions cognitoRegion = Regions.US_EAST_2;
    public static String ENDPOINT = "https://s3-us-east-2.amazonaws.com";
    public static Region REGIONS = Region.getRegion(Regions.US_EAST_2);
    public static String IDENTIY_POOL_ID = "us-east-2:3831a7db-eb2f-4d25-b401-5d3517106648";

    public static class PrefManager {
        public static final String PREF_PROPERTY_UID = "pOCXx_uid";
    }

    public static class AppError {
        public static final int NO_NETWORK_CONNECTION = 3;
    }

    //Common urls
    public class Methods {
         private static final String BASE = "http://13.59.215.19:3000/user/";
        //private static final String BASE = "http://13.126.124.20:8080/user/";
        public static final String REGISTER = BASE + "register";
        public static final String GET_OTP = BASE + "getOTP";
        public static final String SYNC_CONTACTS = BASE + "syncContacts";
        public static final String PARTICIPANTS_DETAILS = "http://13.59.215.19:3000/group/getGroupParticipants";
        public static final String CHANNEL_PARTICIPANTS_DETAILS = "http://13.59.215.19:3000/channel/getChannelParticipants";
        public static final String USER_UPDATE = BASE + "update";
        public static final String CALL = BASE + "voiceCall";
        public static final String ACCEPT_CALL = BASE + "acceptCall";
        public static final String END_CALL = BASE + "endCall";
        private static final String BASE_GROUP = "http://13.59.215.19:3000/group/";
        public static final String CREATE_GROUP = BASE_GROUP + "createGroup";
        public static final String PRIVACY_SETTINGS = BASE + "updatePrivacySettings";
        public static final String MAIL_CHECK = BASE + "checkRecoveryDetails";
        public static final String UPDATE_MAIL = BASE + "updateRecoveryDetails";
        public static final String OTP_REQUEST = BASE + "requestOtp";
        public static final String MAKE_ADMIN = BASE_GROUP + "makeAdmin";
    }

    //Service codes
    public class ServiceCode {
        public static final int REGISTER = 1;
        public static final int GET_OTP = 2;
        public static final int SYNC_CONTACTS = 3;
        public static final int USER_UPDATE = 4;
        public static final int CALL_CODE = 5;
        public static final int CREATE_GROUP = 6;
        public static final int END_CALL = 6;
        public static final int PARTICIPANTS_DETAILS = 7;
        public static final int PRIVAY_DETAILS = 8;
        public static final int CHANNEL_PARTICIPANTS_DETAILS = 9;

    }

    public static class URI {

        public static Uri default_single_message = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        public static Uri default_group_message = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        public static Uri default_call = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        public static long default_single_vibrate[] = {300, 300, 300, 300};
        public static long default_group_vibrate[] = {500, 500, 500, 500};
        public static long default_call_vibrate[] = {700, 700, 700, 700};
        public static int single_light = 0xFFFFFF;
        public static int group_light = 0xFFFFFF;
        public static Boolean inMessageTone = true;

        public static boolean[] mobile_data = new boolean[]{false, false, false, false};
        public static boolean[] roaming_data = new boolean[]{false, false, false, false};
        public static boolean[] wifi = new boolean[]{true, true, true, true};

        public static String last_seen = "c";
        public static String profile_photo = "c";
        public static String status = "c";

        public static boolean enterKey = false;
    }

}