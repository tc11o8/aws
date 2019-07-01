package com.smalleyes.aws.constants;

public class CacheKey {

    public static final String OPEN = "on";
    public static final String OFF = "off";
    public static final String COMM_TASK_QUEUE = "COMM_TASK_QUEUE";
    public static final String COMM_TIMING_ZSET = "COMM_TIMING_ZSET";
    public static final String WAITING_TASK_LOCK = "WAITING_TASK_LOCK";
    public static final String WAITING_HASH_KEY = "WAITING_HASH_KEY";
 // 消息队列
    public static final String FB_MESSAGE_QUEUE = "FB_MESSAGE_QUEUE";
    // 更新cityId任务锁
    public static final String MTK_CITYID_LOCK = "MTK_CITYID_LOCK";
    // sp_imsi_phone表索引ID
    public static final String MTK_CITYID_ID = "MTK_CITYID_ID";
    // 禁止下发的cityIds
    public static final String MTK_FORBID_CITYID = "MTK_FORBID_CITYID";
    public static final String COMM_DELAY_ZSET = "COMM_DELAY_ZSET";
    public static final String AWS_DELAY_TASK_KEY = "AWS_DELAY_TASK_KEY";
    // 收码业务缓存锁
    public static final String BIZ_CODE_LOCK = "BIZ_CODE_LOCK";
    // 收码业务缓存
    public static final String BIZ_CODE_CACHE = "BIZ_CODE_CACHE";
    
    public static final String IMSI_ZSET_KEY = "IMSI_TIMING_ZSET";
    public static final String IMSI_ZSET_KEY_LOCK = "IMSI_ZSET_KEY_LOCK";
    public static final String IMSI_QUEUE_KEY = "IMSI_TASK_QUEUE";
    public static final String IMSI_MOBILE_QUEUE = "IMSI_MOBILE_QUEUE";
    
    public static final String ACTION_NEW_TASK_LOCK = "ACTION_NEW_TASK_LOCK";
    
}
