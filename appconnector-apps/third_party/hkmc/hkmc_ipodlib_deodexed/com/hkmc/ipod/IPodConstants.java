// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package com.hkmc.ipod;


public interface IPodConstants
{

    public static final String EXTRA_IPOD_MODEL = "com.hkmc.ipod.extra.model";
    public static final String EXTRA_IPOD_NAME = "com.hkmc.ipod.extra.name";
    public static final String EXTRA_IPOD_SERIAL = "com.hkmc.ipod.extra.serial";
    public static final String EXTRA_IPOD_STATUS = "com.hkmc.ipod.extra.status";
    public static final int IPOD_API_RET_ACK = -100;
    public static final int IPOD_API_RET_ARTWORK_NOT_SUPPORTED = -205;
    public static final int IPOD_API_RET_AUDIO_LAYER_FAIL = -204;
    public static final int IPOD_API_RET_AUTH_ERR = -52;
    public static final int IPOD_API_RET_BAD_ACK_ACC_NOT_GROUNDED = -82;
    public static final int IPOD_API_RET_BAD_ACK_BAD_PARAMS = -104;
    public static final int IPOD_API_RET_BAD_ACK_CERTIFICATE_INVALID = -110;
    public static final int IPOD_API_RET_BAD_ACK_CERTIFICATE_PERMISSION_INVALID = -111;
    public static final int IPOD_API_RET_BAD_ACK_CMD_PENDING = -106;
    public static final int IPOD_API_RET_BAD_ACK_COMMAND_FAILED = -102;
    public static final int IPOD_API_RET_BAD_ACK_COMMAND_TIMEDOUT = -115;
    public static final int IPOD_API_RET_BAD_ACK_COMMAND_UNAVAILABLE = -116;
    public static final int IPOD_API_RET_BAD_ACK_DIR_NOT_EMPTY = -114;
    public static final int IPOD_API_RET_BAD_ACK_FILE_IN_USE = -112;
    public static final int IPOD_API_RET_BAD_ACK_INVALID_ACC_RID = -117;
    public static final int IPOD_API_RET_BAD_ACK_INVALID_FILE_HANDLE = -113;
    public static final int IPOD_API_RET_BAD_ACK_NOT_AUTHENTICATED = -107;
    public static final int IPOD_API_RET_BAD_ACK_OUT_OF_RESOURCES = -103;
    public static final int IPOD_API_RET_BAD_ACK_POWER_MODE_RQST_FAILED = -109;
    public static final int IPOD_API_RET_BAD_ACK_TRK_NOT_GENIUS = -82;
    public static final int IPOD_API_RET_BAD_ACK_UNKNOWN_DB_CATEGORY = -101;
    public static final int IPOD_API_RET_BAD_ACK_UNKNOWN_ID = -105;
    public static final int IPOD_API_RET_BAD_AUTHENTICATION_VERSION = -108;
    public static final int IPOD_API_RET_BAD_DROPPED_DATA = -123;
    public static final int IPOD_API_RET_BAD_HID_DESC_IN_USE = -122;
    public static final int IPOD_API_RET_BAD_INVALID_VIDEO_SETTING_FOR_IPODOUT_MODE = -124;
    public static final int IPOD_API_RET_BAD_LINGO_BUSY = -120;
    public static final int IPOD_API_RET_BAD_MAX_CONNECTION_REACHED = -121;
    public static final int IPOD_API_RET_CHKSUM_ERROR = -203;
    public static final int IPOD_API_RET_CMD_CANCELED = -209;
    public static final int IPOD_API_RET_CMD_NOT_SUPPORTED = -207;
    public static final int IPOD_API_RET_COMMON_FAIL = -1;
    public static final int IPOD_API_RET_FAIL = -255;
    public static final int IPOD_API_RET_INVALID_ACK_TYPE = -206;
    public static final int IPOD_API_RET_INVALID_CMD = -202;
    public static final int IPOD_API_RET_INVALID_LINGO = -201;
    public static final int IPOD_API_RET_INVALID_MODE = -51;
    public static final int IPOD_API_RET_INVALID_RQST = -200;
    public static final int IPOD_API_RET_LINGO_NOT_SUPPORTED = -208;
    public static final int IPOD_API_RET_LINK_DISCONNECTED = -54;
    public static final int IPOD_API_RET_LINK_ERROR = -53;
    public static final int IPOD_API_RET_MALLOC_FAIL = -50;
    public static final int IPOD_API_RET_PASS = 0;
    public static final int IPOD_API_RET_PASS_MULTISECTION_DATA_READ = -119;
    public static final int IPOD_API_RET_PTHREAD_ERROR = -55;
    public static final int IPOD_API_RET_RSP_Q_TIMEOUT = -150;
    public static final int IPOD_CONNECTED = 2;
    public static final int IPOD_CONNECTING = 1;
    public static final String IPOD_CONNECTION_CHANGE_ACTION = "com.hkmc.ipod.action.IPOD_CONNECTION_CHANGE_ACTION";
    public static final int IPOD_DB_CATEGORY_ALBUM = 3;
    public static final int IPOD_DB_CATEGORY_ARTIST = 2;
    public static final int IPOD_DB_CATEGORY_AUDIOBOOK = 7;
    public static final int IPOD_DB_CATEGORY_COMPOSER = 6;
    public static final int IPOD_DB_CATEGORY_GENRE = 4;
    public static final int IPOD_DB_CATEGORY_PLAYLIST = 1;
    public static final int IPOD_DB_CATEGORY_PODCAST = 8;
    public static final int IPOD_DB_CATEGORY_TRACK = 5;
    public static final int IPOD_DISCONNECTED = 4;
    public static final int IPOD_DISCONNECTING = 3;
    public static final int IPOD_LINGO_DIGITAL_AUDIO = 10;
    public static final int IPOD_LINGO_DISPLAY_REMOTE = 3;
    public static final int IPOD_LINGO_EXTENDED = 4;
    public static final int IPOD_LINGO_GENERAL = 0;
    public static final int IPOD_LINGO_IOUT = 13;
    public static final int IPOD_LINGO_SIMPLE_REMOTE = 2;
    public static final int IPOD_LINGO_SPORTS = 9;
    public static final int IPOD_LINGO_STORAGE = 12;
    public static final int IPOD_LINK_TYPE_BT_LINK = 3;
    public static final int IPOD_LINK_TYPE_UART_LINK = 1;
    public static final int IPOD_LINK_TYPE_USB_LINK = 2;
    public static final int IPOD_PLAY_STATUS_FAST_FORWARD = 3;
    public static final int IPOD_PLAY_STATUS_FAST_REWIND = 4;
    public static final int IPOD_PLAY_STATUS_PAUSED = 2;
    public static final int IPOD_PLAY_STATUS_PLAYING = 1;
    public static final int IPOD_PLAY_STATUS_STOPPED = 0;
    public static final int IPOD_PLAY_TRACK_ALBUM = 2;
    public static final int IPOD_PLAY_TRACK_ARTIST = 3;
    public static final int IPOD_PLAY_TRACK_COMPOSER = 4;
    public static final int IPOD_PLAY_TRACK_GENRE = 5;
    public static final int IPOD_PLAY_TRACK_TITLE = 1;
    public static final int IPOD_REPEAT_ALL = 2;
    public static final int IPOD_REPEAT_NONE = 0;
    public static final int IPOD_REPEAT_ONE = 1;
    public static final int IPOD_SERVICE_NOT_BINDED = -40;
    public static final int IPOD_SHUFFLE_ALBUMS = 2;
    public static final int IPOD_SHUFFLE_NONE = 0;
    public static final int IPOD_SHUFFLE_TRACKS = 1;
    public static final int IPOD_STATUS_CHAPTER_INDEX = 3;
    public static final int IPOD_STATUS_PLAY_STATE = 4;
    public static final int IPOD_STATUS_TRACK_INDEX = 2;
    public static final int IPOD_STATUS_TRACK_POSITION = 1;
}
