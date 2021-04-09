package com.wisdomguo.xifeng.util;

public class BoolUtil {

    public static boolean startByPoint(String str) {// 判断小数点开头
        return str.matches("\\.([\\s\\S]*)");
    }

    public static boolean startByFullStop(String str) {// 判断。开头
        return str.matches("\\。([\\s\\S]*)");
    }
}
