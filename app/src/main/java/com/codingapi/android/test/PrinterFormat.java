package com.codingapi.android.test;

import com.codingapi.android.library.printer.PrinterCommand;
import java.util.ArrayList;

public class PrinterFormat {
    public static ArrayList<byte[]> getPrintData() {
        ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.addAll(PrinterCommand.addTitleH1("**#90吃喝玩了**"));
        bytes.addAll(PrinterCommand.addTitleH1("商家名称"));
        bytes.addAll(PrinterCommand.addLeftText("下单时间：2018-10-09 19:47:22"));
        bytes.addAll(PrinterCommand.addLeftText("订单号：k2018100919472232124"));
        bytes.add(PrinterCommand.addLineSpacing());
        bytes.addAll(PrinterCommand.addTitleH2("-----------下单商品-----------"));
        for (int i = 0; i < 5; i++) {
            bytes.addAll(PrinterCommand.addGoods("商品(规格)", "x" + (i + 1), "0.01"));
            bytes.add(PrinterCommand.addLineSpacing());
        }
        bytes.addAll(PrinterCommand.addGapLine());
        bytes.add(PrinterCommand.LeftToMiddleToRight("包装费:", "x1.2", "0.00"));
        bytes.add(PrinterCommand.LeftToRight("配送费:", "0.00"));
        bytes.addAll(PrinterCommand.LeftToRightBold("已支付:", "0.01"));
        bytes.addAll(PrinterCommand.addGapLine());
        bytes.addAll(PrinterCommand.addInfo("收餐地址", "姓名", "手机号码"));
        bytes.addAll(PrinterCommand.addGapLine());
        bytes.add(PrinterCommand.addCutLine());
        return bytes;
    }
}
