package com.example.administrator.wifisensor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by yejiarui on 2018/2/27.
 */

public class Scan {

    private Activity activity;
    private Toast toast;
    private Context context;
    private WifiManager wifiManager;
    private SensorManager sensorManager;
    private BluetoothManager bluetoothManager;
    private StringBuilder sb1, sb2,sb3;
    private int offset;
    private float acc_x, acc_y, acc_z, gyr_x, gyr_y, gyr_z, com_x, com_y, com_z, mag_x, mag_y, mag_z, pres;
    private float degree1,degree2,degree3;
    private long currentTime1 = 0;
    private Timer timer;
    private GetWifiDataTask getWifiDataTask;
    private GetSensorDataTask getSensorDataTask;
    private GetBleDataTask getBleDataTask;
    private Chronometer chronometer;
    private MySensorEventListener mySensorEventListener;
    private SaveDataTask wifisave,sensorsave;
    private ProgressDialog dialog;

    //ble:
    public String proximityUuid;
    public int major;
    public int minor;
    public int rssi;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private ArrayList<iBeaconClass.iBeacon> mLeDevices;



    public Scan(MainActivity activity, Context context, WifiManager wifiManager, SensorManager sensorManager,
                BluetoothManager bluetoothManager, BluetoothAdapter mBluetoothAdapter,Chronometer chronometer) {
        this.activity = activity;
        this.context = context;
        this.wifiManager = wifiManager;
        this.sensorManager = sensorManager;
        //offset = TimeZone.getDefault().getRawOffset();
        this.bluetoothManager = bluetoothManager;
        this.chronometer=chronometer;
        this.mBluetoothAdapter = mBluetoothAdapter;
        mLeDevices = new ArrayList<iBeaconClass.iBeacon>();
        mLeDeviceListAdapter = new LeDeviceListAdapter(activity);

        sb1 = new StringBuilder();
        sb2 = new StringBuilder();
        sb3 = new StringBuilder();

        mBluetoothAdapter.enable();

    }

    private class MySensorEventListener implements SensorEventListener {
        @SuppressWarnings("deprecation")
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER://加速度传感器event.values
                    acc_x = event.values[0];
                    acc_y = event.values[1];
                    acc_z = event.values[2];
                    break;
                case Sensor.TYPE_GYROSCOPE:   //陀螺仪
                    gyr_x = event.values[0];
                    gyr_y = event.values[1];
                    gyr_z = event.values[2];
                    break;
                /*case Sensor.TYPE_ORIENTATION:  //方向传感器
                    com_x = event.values[0];
                    com_y = event.values[1];
                    com_z = event.values[2];
                    break;*/
                case Sensor.TYPE_MAGNETIC_FIELD://磁场传感器
                    mag_x = event.values[0];
                    mag_y = event.values[1];
                    mag_z = event.values[2];
                    break;
                case Sensor.TYPE_PRESSURE:   //压力传感器
                    pres = event.values[0];
                    break;
            }
            float[] accelerometerValues={acc_x,acc_y,acc_z};
            float[] magneticValues={mag_x,mag_y,mag_z};
            float[] R = new float[9];
            float[] values = new float[3];
            sensorManager.getRotationMatrix(R,null, accelerometerValues, magneticValues);
            sensorManager.getOrientation(R, values);
           degree1 = (int) Math.toDegrees(values[0]);
           degree2 = (int) Math.toDegrees(values[1]);
           degree3 = (int) Math.toDegrees(values[2]);//旋转角度
            if (degree1 < 0) {
                degree1 += 360;
            }
            if (degree2 < 0) {
                degree2 += 360;
            }
            if (degree3 < 0) {
                degree3 += 360;
            }

        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {//准确度改变时调用
        }
    }







    private class GetBleDataTask extends TimerTask{
        int devicee,countb;
        float x,y,z,dur;
        char head;

        public GetBleDataTask(int devicee,float x,float y,float z,char head,float dur,int countb){
            this.countb=countb;
            this.devicee=devicee;
            this.x=x;
            this.y=y;
            this.z=z;
            this.head=head;
            this.dur=dur;
        }
        @Override
        public void run(){
            countb++;
            currentTime1= System.currentTimeMillis();
            //开始扫描周围ble设备
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            System.out.println("周围BLE数目："+mLeDeviceListAdapter.getCount());
            for (int i = 0; i < mLeDeviceListAdapter.getCount(); i++) {
                iBeaconClass.iBeacon device = mLeDeviceListAdapter.getDevice(i);
                sb3.append((currentTime1 - offset) + "\t").append(devicee+ "\t").append(head+ "\t").append(x+ "\t").append(y+ "\t").
                        append(z+ "\t").append(device.major + "\t").append(device.minor + "\t").
                        append(device.rssi + "\t").append("\r\n");
            }
            if (countb>dur*60*10){
                getBleDataTask.cancel();
                timer.cancel();
                //chronometer.stop();
                //new SaveDataTask(devicee,x,y,z,head).execute();
            }
        }


    }
    private class GetWifiDataTask extends TimerTask {
        int device,countw;
        float x,y,z,dur;
        char head;
        public GetWifiDataTask(int device,float x,float y,float z,char head,float dur,int countw){
            this.countw=countw;
            this.device=device;
            this.x=x;
            this.y=y;
            this.z=z;
            this.head=head;
            this.dur=dur;
        }
        @Override
        public void run(){
            countw++;
            currentTime1= System.currentTimeMillis();
                wifiManager.startScan();
                List<ScanResult> lists = wifiManager.getScanResults();
                //System.out.println("周围AP数目：" + lists.size());
                for (int i = 0; i < lists.size(); i++) {
                    ScanResult mScanResult;
                    mScanResult = lists.get(i);
                    sb1.append((currentTime1 - offset) + "\t").append(device + "\t").append(head+"\t").
                            append(x+ "\t").append(y+ "\t").append(z+ "\t").
                            append(mScanResult.BSSID + "\t").append(mScanResult.SSID + "\t").append(mScanResult.level + "\t").append("\n");
                }
            if (countw>dur*60*10){
                getWifiDataTask.cancel();
                timer.cancel();
                chronometer.stop();
                new SaveDataTask(device,x,y,z,head).execute();
                //chronometer.stop();
                //new SaveDataTask(x,y,z,device,head).execute();
            }
            }
    }
    private class GetSensorDataTask extends TimerTask {
        int device,counts;
        float x,y,z,dur;
        char head;
        public GetSensorDataTask(int device,float x,float y,float z,char head,float dur,int counts){
            this.counts=counts;
            this.device=device;
            this.x=x;
            this.y=y;
            this.z=z;
            this.head=head;
            this.dur=dur;

        }
        @Override
        public void run(){
            counts++;
            currentTime1= System.currentTimeMillis();
            /*System.out.println(degree1);
            System.out.println(degree2);
            System.out.println(degree3);*/
            Log.i(TAG, "run: degree1:"+degree1);
            Log.i(TAG, "run: degree2:"+degree2);
            Log.i(TAG, "run: degree2:"+degree3);

            sb2.append(currentTime1 - offset).append("\t").append(device + "\t").append(head+"\t").
                    append(x+ "\t").append(y+ "\t").append(z+ "\t").
                    append(acc_x).append("\t").append(acc_y).append("\t").append(acc_z).append("\t").
                    append(gyr_x).append("\t").append(gyr_y).append("\t").append(gyr_z).append("\t").
                    append(mag_x).append("\t").append(mag_y).append("\t").append(mag_z).append("\t").
                    append(degree1).append("\t").append(degree2).append("\t").append(degree3).append("\t").
                    append(pres).append("\t").append("\n");
            if (counts>dur*60*30){
                getSensorDataTask.cancel();
                timer.cancel();
                //chronometer.stop();
                //new SaveDataTask(x,y,z,device,head).execute();
            }


        }
    }
    public void startWifiScan(int freq,int device,float x,float y,float z,char head,float dur) {
        long itime;
        itime = 1000 / freq;
        sb1.setLength(0);
        /*sb1.append("timestamp").append("\t").append("device" + "\t").append("head" + "\t").
                append("x"+ "\t").append("y"+ "\t").append("z"+ "\t").
                append("bssid"+"\t").append("ssid"+"\t").append("rssi").append("\n");*/
        timer = new Timer();
        int countw=0;
        getWifiDataTask = new GetWifiDataTask(device,x,y,z,head,dur,countw);
        timer.scheduleAtFixedRate(getWifiDataTask,0, itime);
    }
    public void startSensorScan(int freq,int device,float x,float y,float z,char head,float dur) {
        long itime;
        itime = 1000/freq;
        sb2.setLength(0);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        MySensorEventListener mySensorEventListener = new MySensorEventListener();
        for (Sensor sensor : sensors) {
            sensorManager.registerListener(mySensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        /*sb2.append("ms").append("\t").append("device" + "\t").append("head" + "\t").
                append("x"+ "\t").append("y"+ "\t").append("z"+ "\t").
                append("acc_x").append("\t").append("acc_y").append("\t").append("acc_z").append("\t").
                append("gyr_x").append("\t").append("gyr_y").append("\t").append("gyr_z").append("\t").
                append("mag_x").append("\t").append("mag_y").append("\t").append("mag_z").append("\t").
                append("com_x").append("\t").append("com_y").append("\t").append("com_z").append("\t").
                append("pres").append("\t").append("\n");*/
        timer = new Timer();
        int counts=0;
        getSensorDataTask = new GetSensorDataTask(device,x,y,z,head,dur,counts);
        timer.scheduleAtFixedRate(getSensorDataTask,0, itime);
    }

    public void startBleScan(int freq,int device,float x,float y,float z,char head,float dur){
        long itime;
        itime = 1000 / freq;
        sb3.setLength(0);
        /*sb3.append("ms").append("\t").append("device" + "\t").append("head" + "\t").append("x"+ "\t").append("y"+ "\t").append("z"+ "\t").append("\t")
                .append("major").append("\t").append("minor").append("\t").append("rssi").append("\r\n");*/
        timer = new Timer();
        int countb=0;
        getBleDataTask = new GetBleDataTask(device,x,y,z,head,dur,countb);
        timer.scheduleAtFixedRate(getBleDataTask, 0, itime);
    }
    public void stopBlescan(int device,float x,float y,float z,char head){
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        if (timer != null) {
            timer.cancel();
        }
        if (getBleDataTask != null) {
            getBleDataTask.cancel();
        }


    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    final iBeaconClass.iBeacon ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord);
                    mLeDeviceListAdapter.addDevice(ibeacon);
                }
            };
    public void stopWifiscan(int device,float x,float y,float z,char head){
        sensorManager.unregisterListener(mySensorEventListener);
        if (timer != null) {
            timer.cancel();
        }
        if (getWifiDataTask != null) {
            getWifiDataTask.cancel();
        }
        if (getSensorDataTask != null) {
            getSensorDataTask.cancel();
        }
        //wifisave=new SaveDataTask();
        //wifisave.execute();


    }
    public void stopSensorscan(int device,float x,float y,float z,char head){
        sensorManager.unregisterListener(mySensorEventListener);
        if (timer != null) {
            timer.cancel();
        }
        if (getWifiDataTask != null) {
            getWifiDataTask.cancel();
        }
        if (getSensorDataTask != null) {
            getSensorDataTask.cancel();
        }
        //sensorsave=new SaveDataTask();
        //sensorsave.execute();
        new SaveDataTask(device,x,y,z,head).execute();

    }
    private class SaveDataTask extends AsyncTask<Void, Void, Void> {//首先明确Android之所以有Handler和AsyncTask，都是为了不阻塞主线程（UI线程），且UI的更新只能在主线程中完成，因此异步处理是不可避免的
        int device;
        float x,y,z;
        char head;
        public SaveDataTask(int device,float x,float y,float z,char head){
            this.device=device;
            this.x=x;
            this.y=y;
            this.z=z;
            this.head=head;
        }
        boolean saveSuccess1 = false;
        boolean saveSuccess2 = false;
        boolean saveSuccess3 = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = DialogUitl.createProgressDialog(context);
            //dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
                FileUtil fileUtil1 = new FileUtil();
                String content1 = sb1.toString();
                long currentTime = System.currentTimeMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                Date date = new Date(currentTime);
                saveSuccess1 = fileUtil1.saveData(MyConstans.DATA_PATH1 + "WIFI-device"+ device +"-"+ "RP("+x+","+y+","+z+")-head"+ "-"+head +"-"+formatter.format(date)+".txt", content1);
                sb1.setLength(0);

                FileUtil fileUtil2 = new FileUtil();
                String content2 = sb2.toString();
                saveSuccess2 = fileUtil2.saveData(MyConstans.DATA_PATH1 + "IMU-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+"-"+ head +"-"+formatter.format(date)+".txt" ,content2);
                sb2.setLength(0);

                FileUtil fileUtil3 = new FileUtil();
                String content3 = sb3.toString();
                saveSuccess3 = fileUtil3.saveData(MyConstans.DATA_PATH1 + "BLE-device"+ device + "-"+"RP("+x+","+y+","+z+")-head"+ "-"+head +"-"+formatter.format(date)+".txt" ,content3);
                sb3.setLength(0);

                System.out.println("save1=" +saveSuccess1);
                System.out.println("save2="+ saveSuccess2);
                System.out.println("save3="+ saveSuccess3);
                return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (saveSuccess1 && saveSuccess2 && saveSuccess3 ) {
                Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "保存错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
