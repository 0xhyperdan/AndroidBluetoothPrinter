package com.codingapi.android.library.printer;

import android.text.TextUtils;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by iCong
 */
public class PrinterCommand {
    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;
    /**
     * 复位打印机
     */
    public static final byte[] RESET = { 0x1b, 0x40 };
    /**
     * 左对齐
     */
    public static final byte[] TEXT_LEFT = { 0x1b, 0x61, 0x00 };
    /**
     * 中间对齐
     */
    public static final byte[] TEXT_CENTER = { 0x1b, 0x61, 0x01 };
    /**
     * 右对齐
     */
    public static final byte[] TEXT_RIGHT = { 0x1b, 0x61, 0x02 };
    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = { 0x1b, 0x45, 0x01 };
    /**
     * 取消加粗模式
     */
    public static final byte[] CANCEL_BOLD = { 0x1b, 0x45, 0x00 };
    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = { 0x1d, 0x21, 0x11 };
    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = { 0x1d, 0x21, 0x10 };
    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = { 0x1d, 0x21, 0x01 };
    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = { 0x1d, 0x21, 0x00 };
    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = { 0x1b, 0x32 };
    /**
     * 字体 最小 拉长
     */
    public static final byte[] FONT_H_1 = { 0x1d, 0x21, 0x01 };
    public static final byte[] FONT_H_2 = { 0x1d, 0x21, 0x02 };
    public static final byte[] FONT_H_3 = { 0x1d, 0x21, 0x03 };
    public static final byte[] FONT_H_4 = { 0x1d, 0x21, 0x04 };
    public static final byte[] FONT_H_5 = { 0x1d, 0x21, 0x05 };
    public static final byte[] FONT_H_6 = { 0x1d, 0x21, 0x06 };
    public static final byte[] FONT_H_7 = { 0x1d, 0x21, 0x07 };
    public static final byte[] FONT_W_1 = { 0x1d, 0x21, 0x11 };
    public static final byte[] FONT_W_2 = { 0x1d, 0x21, 0x12 };
    public static final byte[] FONT_W_3 = { 0x1d, 0x21, 0x13 };
    public static final byte[] FONT_W_4 = { 0x1d, 0x21, 0x14 };
    public static final byte[] FONT_W_5 = { 0x1d, 0x21, 0x15 };
    public static final byte[] FONT_W_6 = { 0x1d, 0x21, 0x16 };
    public static final byte[] FONT_W_7 = { 0x1d, 0x21, 0x17 };
    public static final byte[] FONT_1 = { 0x1d, 0x21, 0x11 };
    public static final byte[] FONT_2 = { 0x1d, 0x21, 0x22 };
    public static final byte[] FONT_3 = { 0x1d, 0x21, 0x33 };
    public static final byte[] FONT_4 = { 0x1d, 0x21, 0x44 };
    public static final byte[] FONT_5 = { 0x1d, 0x21, 0x55 };
    public static final byte[] FONT_6 = { 0x1d, 0x21, 0x66 };
    public static final byte[] FONT_7 = { 0x1d, 0x21, 0x77 };

    /**
     * 汉字格式
     */
    static final Charset GBK = Charset.forName("GBK");
    /**
     * ESC查询打印机实时状态指令
     */
    static final byte[] ESC = { 0x10, 0x04, 0x02 };
    /**
     * ESC查询打印机实时状态 缺纸状态
     */
    static final int ESC_STATE_PAPER_ERR = 0x20;
    /**
     * ESC指令查询打印机实时状态 打印机开盖状态
     */
    static final int ESC_STATE_COVER_OPEN = 0x04;
    /**
     * ESC指令查询打印机实时状态 打印机报错状态
     */
    static final int ESC_STATE_ERR_OCCURS = 0x40;
    /**
     * 清除打印机缓存指令
     */
    static final byte[] CLEAR = "CLS\n".getBytes();
    /**
     * 打印机测试数据
     */
    static final byte[] TEST = "小票打印机测试\n\n\n\n".getBytes(GBK);
    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;
    private static final int LEFT_LENGTH = 20;
    private static final int RIGHT_LENGTH = 12;
    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;
    /**
     * 撕纸
     */
    private static final byte[] CUT = "\n\n\n\n".getBytes(GBK);
    /**
     * 换行
     */
    private static final String WRAP = "\n\n";

    /**
     * 换行
     */
    private static final byte[] BR = { 0x0A };

    /**
     * 打印文字 换行
     *
     * @param content 内容
     * @return 内容格式
     */
    public static byte[] addTextWrap(String content) {
        final String s = content + WRAP;
        return s.getBytes(GBK);
    }

    /**
     * 打印文字 不换行
     *
     * @param content 内容
     * @return 内容格式
     */
    public static byte[] addText(String content) {
        return content.getBytes(GBK);
    }

    /**
     * 标题 H1
     *
     * @param title 标题
     * @return 标题格式
     */
    public static ArrayList<byte[]> addTitleH1(String title) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(DOUBLE_HEIGHT_WIDTH);
        bytes.add(BOLD);
        bytes.add(TEXT_CENTER);
        bytes.add(addTextWrap(title));
        bytes.add(CANCEL_BOLD);
        return bytes;
    }

    /**
     * 标题 H2
     *
     * @param title 标题
     * @return 标题格式
     */
    public static ArrayList<byte[]> addTitleH2(String title) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(BOLD);
        bytes.add(NORMAL);
        bytes.add(TEXT_CENTER);
        bytes.add(addTextWrap(title));
        bytes.add(CANCEL_BOLD);
        return bytes;
    }

    /**
     * 内容 左边对齐普通文字
     *
     * @param content 标题
     * @return 标题格式
     */
    public static ArrayList<byte[]> addLeftText(String content) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(NORMAL);
        bytes.add(TEXT_LEFT);
        bytes.add(addText(content + "\n"));
        return bytes;
    }

    /**
     * 内容 左边对齐普通文字
     *
     * @param content 标题
     * @return 标题格式
     */
    public static ArrayList<byte[]> addLeftTextBold(String content) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(DOUBLE_HEIGHT_WIDTH);
        bytes.add(TEXT_LEFT);
        bytes.add(addText(content + "\n"));
        return bytes;
    }

    /**
     * 商品 商品名字     x1       0.01
     *
     * @param left 左边文字 middle 中间文字  right 右边文字
     * @return 标题格式
     */
    public static ArrayList<byte[]> addGoods(String left, String middle, String right) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(FONT_H_1);
        bytes.add(TEXT_LEFT);
        if (left.length() > LEFT_TEXT_MAX_LENGTH) {
            bytes.add(LeftToMiddleToRightBr(left, middle, right));
        } else {
            bytes.add(LeftToMiddleToRight(left, middle, right));
        }
        bytes.add(BR);
        return bytes;
    }

    /**
     * 联系信息
     * 地址
     * 名字
     * 手机
     *
     * @param address 地址
     * @param name 名字
     * @param phone 手机
     * @return 标题格式
     */
    public static ArrayList<byte[]> addInfo(String address, String name, String phone) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(DOUBLE_HEIGHT_WIDTH);
        bytes.add(TEXT_LEFT);
        bytes.add(addTextWrap(address));
        bytes.add(addTextWrap(name));
        bytes.add(addTextWrap(phone));
        return bytes;
    }

    /**
     * 空行
     */
    public static byte[] addLineSpacing() {
        return BR;
    }

    /**
     * 破折线
     */
    public static ArrayList<byte[]> addGapLine() {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        int count = LINE_BYTE_SIZE / "-".getBytes(GBK).length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("-");
        }
        bytes.add(NORMAL);
        bytes.add(BR);
        bytes.add(sb.toString().getBytes(GBK));
        bytes.add(BR);
        return bytes;
    }

    /**
     * 斯纸
     */
    public static byte[] addCutLine() {
        return CUT;
    }

    /**
     * 打印两列
     *
     * @param leftText 左侧文字
     * @param rightText 右侧文字
     */
    public static byte[] LeftToRight(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(leftText);
        int rightTextLength = getBytesLength(rightText);
        sb.append(leftText);
        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;
        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString().getBytes(GBK);
    }

    /**
     * 打印两列 加粗
     *
     * @param left 左侧文字
     * @param right 右侧文字
     */
    public static ArrayList<byte[]> LeftToRightBold(String left, String right) {
        final ArrayList<byte[]> bytes = new ArrayList<>();
        bytes.add(FONT_H_1);
        bytes.add(LeftToRight(left, right));
        return bytes;
    }

    /**
     * 打印三列
     *
     * @param left 左侧文字
     * @param middle 中间文字
     * @param right 右侧文字
     */
    public static byte[] LeftToMiddleToRight(String left, String middle, String right) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = getBytesLength(left);
        int middleTextLength = getBytesLength(middle);
        int rightTextLength = getBytesLength(right);
        sb.append(left);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;
        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middle);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(right);
        return sb.toString().getBytes(GBK);
    }

    /**
     * 打印三列 超过 8 个字换行
     *
     * @param left 左侧文字
     * @param middle 中间文字
     * @param right 右侧文字
     */
    public static byte[] LeftToMiddleToRightBr(String left, String middle, String right) {
        StringBuilder sb = new StringBuilder();
        left += WRAP;
        int middleTextLength = getBytesLength(middle);
        int rightTextLength = getBytesLength(right);
        sb.append(left);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - middleTextLength / 2;
        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middle);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(right);
        return sb.toString().getBytes(GBK);
    }

    /**
     * 获取数据长度
     */
    private static int getBytesLength(String msg) {
        return msg.getBytes(GBK).length;
    }

    /**
     * 格式化菜品名称，最多显示MEAL_NAME_MAX_LENGTH个数
     */
    public static String formatMealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() > MEAL_NAME_MAX_LENGTH) {
            return name.substring(0, 8) + "..";
        }
        return name;
    }

    /**
     * 设置字体大小
     *
     * @param fontSize 字号 0x00(最小) .... 0x77(最大)
     */
    public static byte[] fontSize(byte fontSize) {
        return new byte[] { 0x1D, 0x21, fontSize };
    }
}
