package com.efeiyi.website.util;

import java.util.UUID;

public class UUIDGenerator {
    public UUIDGenerator() {
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号 
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * 获得指定数目的UUID
     *
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID(int number) {
        if (number < 1) {
            return null;
        }
        String[] ss = new String[number];
        for (int i = 0; i < number; i++) {
            ss[i] = getUUID();
        }
        return ss;
    }

    public static void main(String[] args) {
        String[] ss = getUUID(10);
        for (int i = 0; i < ss.length; i++) {
            System.out.println(ss[i]);
        }
    }

    public static String generateId() {
        String dateId = Long.toString(System.currentTimeMillis(), 36);
        Double numIds = Math.random();
        String numIds2 = numIds.toString();
        numIds2 = numIds2.substring(numIds2.indexOf(".") + 1, numIds2.length() - 1);
        if (numIds2.length() <= 17) {
            numIds2 = fillWithZero(numIds2, 17);
        }
        String numIds3 = numIds2.substring(1, 15);
        String numId = Long.toString(Long.parseLong(numIds3), 36);

        StringBuilder stringBuilder = new StringBuilder(16);

        stringBuilder
                .append(fillWithZero(dateId, 8))
                .append(fillWithZero(numId, 8));

        return stringBuilder.toString();

    }

    private static String fillWithZero(String str, Integer length) {

        StringBuilder stringBuilder = new StringBuilder(length);

        stringBuilder.append(str);
        if (str.length() > length) {
            stringBuilder.deleteCharAt(length);
        } else if (str.length() < length) {
            int tempLength = length - str.length();
            for (int i = 0; i < tempLength; i++) {
                stringBuilder.insert(0, "0");
            }
        }

        return stringBuilder.toString();
    }
}   