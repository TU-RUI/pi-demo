package com.turui.pi.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-11-14
 */
public class CommonUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final ThreadLocalRandom THREAD_LOCAL_RANDOM = ThreadLocalRandom.current();

    public static String getCurTime() {
        return DATE_FORMAT.format(new Date());
    }

    public static String getPictureName() {
        return getCurTime() + "-" + THREAD_LOCAL_RANDOM.nextInt(1024);
    }

}
