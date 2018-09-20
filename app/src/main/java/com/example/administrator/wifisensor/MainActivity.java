package com.example.administrator.wifisensor;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.SensorManager;
import android.view.inputmethod.InputMethodManager;
import android.net.wifi.WifiManager;
import java.io.File;
import android.os.Looper;
import android.os.Message;
import android.view.View.OnClickListener;
public class MainActivity extends Activity implements OnClickListener {
    private static final int START = 1, STOP = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE =1 ;
    private static final int REQUEST_ACCESS_FINE_LOCATION =2 ;
    private static final int REQUEST_ACCESS_COARSE_LOCATION =3 ;
    private static final String TAG_SERVICE = "ok";
    private EditText etAction,etAction1,etAction2,etAction3,etAction4,etAction5;
    private Button button1, button2;
    private Toast Toast;
    private MainActivity activity;
    private Context context;
    private InputMethodManager inputManager;
    private WifiManager wifiManager;
    private SensorManager sensorManager;
    private BluetoothManager bluetoothManager;
    private Scan scan;
    private WifiThread wifiThread;
    private SensorThread sensorThread;
    private BLEThread bleThread;
    private Chronometer chronometer;
    private TextView textView;

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private LeDeviceListAdapter mLeDeviceListAdapter;
    /**
     * 搜索BLE终端
     */
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        init();
        checkPermission();
        creatDic();
    }
    protected void onResume(){
        super.onResume();
        initManager();
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        scan = new Scan(activity, context, wifiManager, sensorManager,bluetoothManager,mBluetoothAdapter,chronometer);
        button1.setOnClickListener(this);
        //button2.setOnClickListener(this);
        //启动wifi-scan子线程
        wifiThread = new WifiThread();
        wifiThread.start();
        sensorThread = new SensorThread();
        sensorThread.start();
        bleThread = new BLEThread();
        bleThread.start();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                Message msg = new Message();
                msg.what = START;
                wifiThread.mHandler.sendMessage(msg);
                Message msg1 = new Message();
                msg1.what = START;
                sensorThread.mHandler.sendMessage(msg1);
                Message msg2 = new Message();
                msg2.what = START;
                bleThread.mHandler.sendMessage(msg2);
                break;
            /*case R.id.button2:
                //1.分别向两个子线程发stop消息
                chronometer.stop();
                Message msgg = new Message();
                msgg.what = STOP;
                wifiThread.mHandler.sendMessage(msgg);
                Message msgg1 = new Message();
                msgg1.what = STOP;
                sensorThread.mHandler.sendMessage(msgg1);
                Message msgg2 = new Message();
                msgg2.what = STOP;
                bleThread.mHandler.sendMessage(msgg2);
                break;*/
            default:
                break;
        }

    }
    class WifiThread extends Thread {
        public Handler mHandler;
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                //定义处理消息的方法
                @Override
                public void handleMessage(Message msg) {
                    String ID = etAction.getText().toString();
                    int device = Integer.parseInt(ID);
                    String xx = etAction1.getText().toString();
                    float xw = Float.parseFloat(xx);
                    String yy = etAction2.getText().toString();
                    float yw =  Float.parseFloat(yy);
                    String zz = etAction3.getText().toString();
                    float zw =  Float.parseFloat(zz);
                    char phonehead = etAction4.getText().toString().charAt(0);
                    String durw = etAction5.getText().toString();
                    float duringw = Float.parseFloat(durw);

                    if (msg.what == 1)//收到主线程 start 的消息
                    {
                        Toast.makeText(context, "开始采集！", Toast.LENGTH_SHORT).show();
                        System.out.println("Wifi子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" START！" );
                       // String inputText = etAction.getText().toString();
                        //int SampleFreq =Integer.parseInt(inputText);


                        //System.out.println("采集频率："+10+"Hz");
                        scan.startWifiScan(10,device,xw,yw,zw,phonehead,duringw);
                    } /*else if (msg.what == 0)//收到主线程 stop 的消息
                    {
                        Toast.makeText(context, "结束采集！", Toast.LENGTH_SHORT).show();
                        System.out.println("Wifi子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" STOP！" );
                        scan.stopWifiscan(device,xw,yw,zw,phonehead);
                    }*/
                }

            };
            Looper.loop();
        }
    }
    class SensorThread extends Thread {
        public Handler mHandler;
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                //定义处理消息的方法
                @Override
                public void handleMessage(Message msg) {
                    String ID = etAction.getText().toString();
                    int device = Integer.parseInt(ID);
                    String xx = etAction1.getText().toString();
                    float xs =  Float.parseFloat(xx);
                    String yy = etAction2.getText().toString();
                    float ys =  Float.parseFloat(yy);
                    String zz = etAction3.getText().toString();
                    float zs =  Float.parseFloat(zz);
                    char phonehead = etAction4.getText().toString().charAt(0);
                    String durs = etAction5.getText().toString();
                    float durings = Float.parseFloat(durs);
                    if (msg.what == 1)//收到主线程 start 的消息
                    {
                        System.out.println("Sensor子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" START！");
                        //String inputText = etAction.getText().toString();
                        //int SampleFreq =Integer.parseInt(inputText);

                        scan.startSensorScan(30,device,xs,ys,zs,phonehead,durings);
                    } /*else if (msg.what == 0)//收到主线程 stop 的消息
                    {
                        System.out.println("Sensor子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" STOP！");
                        scan.stopSensorscan(device,xs,ys,zs,phonehead);
                    }*/
                }
            };
            Looper.loop();
        }
    }
    class BLEThread extends Thread {
        public Handler mHandler;
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                //定义处理消息的方法
                @Override
                public void handleMessage(Message msg) {
                    String ID = etAction.getText().toString();
                    int device = Integer.parseInt(ID);
                    String xx = etAction1.getText().toString();
                    float xb = Float.parseFloat(xx);
                    String yy = etAction2.getText().toString();
                    float yb =  Float.parseFloat(yy);
                    String zz = etAction3.getText().toString();
                    float zb =  Float.parseFloat(zz);
                    char phonehead = etAction4.getText().toString().charAt(0);
                    String durb = etAction5.getText().toString();
                    float duringb = Float.parseFloat(durb);
                    if (msg.what == 1)//收到主线程 start 的消息
                    {
                        //Toast.makeText(context, "开始采集！", Toast.LENGTH_SHORT).show();
                        System.out.println("BLE子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" START！" );
                        // String inputText = etAction.getText().toString();
                        //int SampleFreq =Integer.parseInt(inputText);


                        //System.out.println("采集频率："+30+"Hz");
                        scan.startBleScan(10,device,xb,yb,zb,phonehead,duringb);
                    } /*else if (msg.what == 0)//收到主线程 stop 的消息
                    {
                        //Toast.makeText(context, "结束采集！", Toast.LENGTH_SHORT).show();
                        System.out.println("BLE子线程的ID：" + String.valueOf(Thread.currentThread().getId())+" STOP！" );
                        scan.stopBlescan(device,xb,yb,zb,phonehead);
                    }*/
                }

            };
            Looper.loop();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }


    /*
     * 1.初始化布局
     */
    public void init(){
        textView=(TextView) findViewById(R.id.textView11);
        etAction = (EditText) findViewById(R.id.etAction);
        etAction1 = (EditText) findViewById(R.id.etAction1);
        etAction2 = (EditText) findViewById(R.id.etAction2);
        etAction3 = (EditText) findViewById(R.id.etAction3);
        etAction4 = (EditText) findViewById(R.id.etAction4);
        etAction5 = (EditText) findViewById(R.id.etAction5);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
    }
    /*
     * 2.申请权限
     */
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            //Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            //Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }
    /*
    private void checkPermissions() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
        }
    private void checkPermissionss() {
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }

    }
    */
    /*
     * 3.建立文件夹目录
     */
    public void creatDic() {
        File file1 = new File(MyConstans.DATA_PATH1);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        /*
        File file2 = new File(MyConstans.DATA_PATH2);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        */
    }
    /*
     * 4.初始化manager
     */
    public void initManager(){
        activity = this;
        context = this;
        wifiManager = (WifiManager) getApplicationContext().getSystemService(context.WIFI_SERVICE);
        sensorManager = (SensorManager) getSystemService(context.SENSOR_SERVICE);

    }
}