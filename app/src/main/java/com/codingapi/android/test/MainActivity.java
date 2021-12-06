package com.codingapi.android.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.codingapi.android.library.printer.service.PrinterService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mPrinterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PrinterService.STATUS status =
                    (PrinterService.STATUS) intent.getSerializableExtra(PrinterService.FILTER_ACTION);
            String errorMsg = "";
            if (intent.hasExtra(PrinterService.ERROR_MESSAGE)) {
                errorMsg = intent.getStringExtra(PrinterService.ERROR_MESSAGE);
            }
            Log.i(TAG, "mPrinterReceiver printer status: " + status);
            switch (status) {
                case OPEN:// 打印机机盖开启。
                    Toast.makeText(context, "打印机机盖开启", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:// 打印机错误。
                    Toast.makeText(context, "打印机错误: " + errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                case NO_PAPER:// 打印机缺纸。
                    Toast.makeText(context, "打印机缺纸", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTED:// 打印机连接成功。
                    Toast.makeText(context, "打印机连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECTING:// 正在连接打印机。
                    Toast.makeText(context, "正在连接打印机", Toast.LENGTH_SHORT).show();
                    break;
                case DISCONNECTED:// 打印机断开连接。
                    Toast.makeText(context, "打印机断开连接", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:// 打印机连接失败。
                    Toast.makeText(context, "打印机连接失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                case READY:// 打印机准备就绪。
                    Toast.makeText(context, "打印机准备就绪：" + errorMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(mPrinterReceiver, new IntentFilter(PrinterService.FILTER_ACTION));
    }

    public void print(View view) {
        // 构建 Intent 数据
        Intent intent = new Intent(this, PrinterService.class);
        // 打印模式 PrinterService.MODE.NORMAL 正常打印模式（默认） PrinterService.MODE.TEST 测试打印机
        intent.putExtra(PrinterService.PRINT_MODEL, PrinterService.MODE.NORMAL);
        // 蓝牙地址必须传(设置打印机的时候 存储到本地，如果没有 提示去设置打印机)
        intent.putExtra(PrinterService.BLUETOOTH_ADDRESS, "DC:0D:30:27:0A:64");
        // Test 模式可以不传要打印的数据 * 正常模式必传。 （打印数据格式为 byte[]） 数据格式参考 PrinterFormat
        intent.putExtra(PrinterService.PRINT_DATA, PrinterFormat.getPrintTestData());
        // 启动服务 自动打印。
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPrinterReceiver);
    }
}
