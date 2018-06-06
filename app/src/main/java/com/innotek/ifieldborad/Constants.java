package com.innotek.ifieldborad;

import android.os.Environment;

/**
 * Created by Raleigh.Luo on 18/6/6.
 */

public interface Constants {
    String TEMPLE_TABLE = "temple_table";// 临时存储，记录写过的数据
    String PREFS_SERVER_TABLE = "SERVERS";
    String BROADCAST_SERVER="broadcastServer";//服务器地址
    String PLATFORM_NAME="platform_name";
    String MANAGER_TITLE="manager_title";
    String PRISON_NAME="prison_name";
    String REQUEST_TIME="request_time";//请求周期 秒为单位
    String BROADCAST_SERVER_DEFAULT=" http://192.168.1.1:8080";//默认服务器地址
    String CACHE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ifieldborad";
    String APK_NAME="/app.apk";
    int REQUEST_TIME_DEFAULT=3600;//默认1小时刷新一次
}
