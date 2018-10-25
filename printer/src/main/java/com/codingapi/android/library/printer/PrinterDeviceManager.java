package com.codingapi.android.library.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by iCong
 */
public class PrinterDeviceManager {
    private static final String TAG = PrinterDeviceManager.class.getSimpleName();
    // 单例
    private static PrinterDeviceManager sDeviceManager;
    // 蓝牙 io
    private BluetoothSocket mSocket;
    // 输入流
    private InputStream mInputStream;
    // 读取流
    private OutputStream mOutputStream;
    // UUID
    private static final UUID sUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 蓝牙地址
    private String mBluetoothAddress;

    private PrinterDeviceManager(String address) {
        mBluetoothAddress = address;
    }

    public static PrinterDeviceManager getInstance(String address) {
        if (sDeviceManager == null) {
            synchronized (PrinterDeviceManager.class) {
                if (sDeviceManager == null) {
                    sDeviceManager = new PrinterDeviceManager(address);
                }
            }
        }
        return sDeviceManager;
    }

    // 发送指令
    public void sendCommand(ArrayList<byte[]> bytes) {
        try {
            int count = 3; // 重试 3 次
            while (!isConnected() && count > 0) {
                count--;
                connection();
            }
            boolean isOk = checkPrinter();
            if (isOk) {
                for (byte[] aByte : bytes) {
                    mOutputStream.write(aByte);
                    Log.i(TAG, "打印数据：" + new String(aByte, PrinterCommand.GBK));
                }
                mOutputStream.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "向打印机发送是指令失败", e);
            try {
                mSocket.close();
            } catch (IOException ignored) {
            }
            e.printStackTrace();
        }
    }

    // 打印测试
    public void printTest() {
        ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(PrinterCommand.TEST);
        sendCommand(bytes);
    }

    // 检查打印机
    private boolean checkPrinter() throws IOException {
        if (mOutputStream == null) return false;
        if (mInputStream == null) return false;
        final byte[] buffer = new byte[128];
        mOutputStream.write(PrinterCommand.ESC);
        mOutputStream.flush();
        int ret = mInputStream.read(buffer);
        Log.i(TAG, "check printer read :" + ret);
        if (ret == -1) {
            mListener.error(ret);
            mListener.disconnected();
            Log.e(TAG, "打印机状态异常，连接断开。ret:" + ret);
        } else {
            int result = judgeResponseType(buffer[0]);
            if (result == 1) {
                if ((buffer[0] & PrinterCommand.ESC_STATE_PAPER_ERR) > 0) {
                    mListener.noPaper();
                    Log.e(TAG, "打印机状态异常，缺纸。");
                }
                if ((buffer[0] & PrinterCommand.ESC_STATE_COVER_OPEN) > 0) {
                    mListener.open();
                    Log.e(TAG, "打印机状态异常，机盖打开。");
                }
                if ((buffer[0] & PrinterCommand.ESC_STATE_ERR_OCCURS) > 0) {
                    mListener.error((buffer[0] & PrinterCommand.ESC_STATE_ERR_OCCURS));
                    Log.e(TAG, "打印机状态异常");
                }
                if (buffer[0] == 0) {
                    mListener.ready();
                    Log.i(TAG, "打印机就绪");
                } else {
                    Log.w(TAG, "打印机状态码：" + buffer[0]);
                    mListener.error((buffer[0] & PrinterCommand.ESC_STATE_ERR_OCCURS));
                }
            }
        }
        return ret != -1;
    }

    // 判断是实时状态（10 04 02）还是查询状态（1D 72 01）
    private int judgeResponseType(byte r) {
        return (byte) ((r & 0x10) >> 4);
    }

    // 连接打印机
    private void connection() {
        Log.i(TAG, "开始连接接打印机...");
        mListener.connecting();
        // 蓝牙适配器
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        // 蓝牙设备
        BluetoothDevice mDevice = mAdapter.getRemoteDevice(mBluetoothAddress);
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(sUUID);
            mAdapter.cancelDiscovery();
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            Log.i(TAG, "打印机连接成功...");
            mListener.connected();
        } catch (Exception e) {
            Log.e(TAG, "打印机连接失败", e);
            mListener.connectFail(e);
            try {
                if (isConnected()) {
                    mSocket.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    // 是否连接
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected();
    }

    // 释放资源
    public void release() {
        if (sDeviceManager != null) {
            sDeviceManager.closePort();
            sDeviceManager = null;
        }
    }

    // 关闭端口
    private void closePort() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.flush();
                mOutputStream.close();
                mOutputStream = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "关闭端口", e);
            e.printStackTrace();
        }
    }

    // 清除打印机缓存
    private void cleanBuffer() {
        ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(PrinterCommand.CLEAR);
        sendCommand(bytes);
    }

    public void addListener(PrintStatusListener listener) {
        mListener = listener;
    }

    private PrintStatusListener mListener;

    public interface PrintStatusListener {
        void connecting();

        void connected();

        void disconnected();

        void connectFail(Exception e);

        void error(int code);

        void noPaper();

        void open();

        void ready();
    }
}