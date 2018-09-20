package com.example.administrator.wifisensor;

import android.os.Environment;

/**
 * Created by yejiarui on 2018/2/27.
 */

class MyConstans {
    public static String MAC; //AP ID
    public static String IMEI; //device ID
    /**
     * 数据采集状态
     */
    public static String STATUS = "off";
    /**
     * 数据存储目录
     */
    public static String DATA_PATH1 = Environment.getExternalStorageDirectory().getPath()  + "/"+"com.融合5.0"+ "/";//获取SD卡的根目录
    //public static String DATA_PATH2 = Environment.getExternalStorageDirectory().getPath() + "com.123" + "/"+"com.123.sensor";
//    public static String DATA_PATH3 = Environment.getExternalStorageDirectory().getPath() + "/" + "com.123" +"/"+"com.123.gps"
//            + "/";//获取SD卡的根目录
//    public static String DATA_PATH4 = Environment.getExternalStorageDirectory().getPath() + "/" + "com.123" +"/"+"com.123.ble"
//            + "/";//获取SD卡的根目录
//    /**
//     * 数据采集频率
//     */
//    public static int[] DELAY = { SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_NORMAL,
//            SensorManager.SENSOR_DELAY_UI };
//    /**
//     * 采样频率
//     */
//    public static final int[] SAMPLING_RATE = { 1000, 2000, 3000 };
//    public static final String RECORD_FILE_NAME = DATA_PATH1 + "record.txt";
//
//    /**
//     * 动作
//     */

}
