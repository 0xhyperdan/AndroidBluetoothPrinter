package com.codingapi.android.library.printer.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;
import com.codingapi.android.library.printer.PrinterDeviceManager;
import com.codingapi.android.library.printer.threads.ThreadFactoryBuilder;
import com.codingapi.android.library.printer.threads.ThreadPool;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by iCong on BLUETOOTH_PORT1/BLUETOOTH_PORT3/2BLUETOOTH_PORT18.
 */

public class PrinterService extends Service implements PrinterDeviceManager.PrintStatusListener {
    private static final String TAG = PrinterService.class.getSimpleName();
    private static final String BLUETOOTH_NOT_SUPPORT = "该设备不支持蓝牙";
    private static final String PRINT_DATA_NOT_NULL = "打印数据不能为空";
    private static final String OPEN_BLUETOOTH = "请开启设备蓝牙";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String PRINT_RESET_ADDRESS = "print_reset_address";

    public static final String FILTER_ACTION = PrinterService.class.getName();
    // 打印机蓝牙地址
    public static final String BLUETOOTH_ADDRESS = "bluetooth_address";
    // 打印数据
    public static final String PRINT_DATA = "print_data";
    // 打印模式
    public static final String PRINT_MODEL = "print_model";
    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    // 线程池
    private ThreadFactoryBuilder mThreadFactory;
    // 打印内容
    private ArrayList<byte[]> mPrintData;
    // 打印模式 默认正常
    private MODE model = MODE.NORMAL;
    // 打印机管理
    private PrinterDeviceManager mPrintManager;
    // 是否重设
    private boolean isReset;

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();
        mThreadFactory = new ThreadFactoryBuilder(PrinterService.class.getSimpleName());
    }

    @Override public void onDestroy() {
        mPrintManager.release();
        ThreadPool.getInstance().stopThreadPool();
        super.onDestroy();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String address;
            if (intent.hasExtra(PRINT_RESET_ADDRESS)) {
                address = intent.getStringExtra(PRINT_RESET_ADDRESS);
                isReset = true;
            } else {
                address = intent.getStringExtra(BLUETOOTH_ADDRESS);
            }
            if (TextUtils.isEmpty(address)) {
                throw new NullPointerException("Bluetooth address not null.");
            }
            if (isReset && mPrintManager != null) {
                mPrintManager.release();
            }
            mPrintManager = PrinterDeviceManager.getInstance(address);
            mPrintManager.addListener(this);
            if (intent.hasExtra(PRINT_MODEL)) {
                model = (MODE) intent.getSerializableExtra(PRINT_MODEL);
            }
            if (model != MODE.TEST && intent.hasExtra(PRINT_DATA)) {
                mPrintData = (ArrayList<byte[]>) intent.getSerializableExtra(PRINT_DATA);
            }
        }
        if (isOpenBluetooth()) {
            if (model != MODE.TEST && isEmptyPrintData()) {
                showMessage(PRINT_DATA_NOT_NULL);
            } else {
                print();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    // 检查蓝牙设备
    private boolean isOpenBluetooth() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetoothAdapter == null) { // 设备不支持蓝牙
            showMessage(BLUETOOTH_NOT_SUPPORT);
            return false;
        } else if (mBluetoothAdapter.isEnabled()) { // 开启蓝牙
            return true;
        } else { // 蓝牙未开启
            showMessage(OPEN_BLUETOOTH);
            return false;
        }
    }

    // 打印
    private void print() {
        final Runnable runnable = mThreadFactory.newThread(new Runnable() {
            @Override public void run() {
                if (model == MODE.TEST) {
                    mPrintManager.printTest();
                } else {
                    mPrintManager.sendCommand(mPrintData);
                }
            }
        });
        ThreadPool.getInstance().addTask(runnable);
    }

    private boolean isEmptyPrintData() {
        return mPrintData == null || mPrintData.isEmpty();
    }

    @Override public void connecting() {
        // 打印机正在连接
        send(STATUS.CONNECTING);
    }

    @Override public void connected() {
        // 打印机连接成功
        send(STATUS.CONNECTED);
    }

    @Override public void disconnected() {
        // 打印机断开连接
        send(STATUS.DISCONNECTED);
    }

    @Override public void connectFail(Exception e) {
        // 打印机连接失败
        send(STATUS.CONNECT_FAIL, e.toString());
    }

    @Override public void error(int code) {
        // 打印机逻辑错误
        send(STATUS.ERROR, "打印机状态码：" + code);
    }

    @Override public void noPaper() {
        // 无纸张
        send(STATUS.NO_PAPER);
    }

    @Override public void open() {
        // 开盖
        send(STATUS.OPEN);
    }

    @Override public void ready() {
        // 打印机准备就绪
        send(STATUS.READY);
    }

    private void send(STATUS status) {
        send(status, null);
    }

    private void send(STATUS status, String message) {
        final Intent intent = new Intent(FILTER_ACTION);
        intent.putExtra(FILTER_ACTION, status);
        if (message != null) {
            intent.putExtra(ERROR_MESSAGE, message);
        }
        sendBroadcast(intent);
    }

    public enum MODE implements Serializable {
        NORMAL, TEST
    }

    public enum STATUS implements Serializable {
        ERROR,
        DISCONNECTED,
        CONNECTING,
        CONNECT_FAIL,
        CONNECTED,
        OPEN,
        NO_PAPER,
        READY
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
