package com.starer.website_navigation_server.util;

import java.util.Random;

public class RandomUtil {

    private static final Random random = new Random();

    public static String randomCode(int length){
        if(length <= 0 ) {
            return null;
        }
        int pow = (int) Math.pow(10, length - 1);
        return String.valueOf(random.nextInt(pow*10 - 100001) + pow);
    }
}
