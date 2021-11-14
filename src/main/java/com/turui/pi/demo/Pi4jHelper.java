package com.turui.pi.demo;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-11-14
 * 程序结束后需要shutdown()
 */
public class Pi4jHelper {
    private static Context INSTANCE = null;

    private Pi4jHelper() {
    }

    public static Context getContext() {
        if (INSTANCE == null) {
            synchronized (Pi4jHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = Pi4J.newAutoContext();
                    PrintInfo.title("<-- Pi4J Context created -->");
                    PrintInfo.printLoadedPlatforms(INSTANCE);
                    PrintInfo.printDefaultPlatform(INSTANCE);
                    PrintInfo.printProviders(INSTANCE);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 程序结束后需要shutdown
     */
    public static void shutdown() {
        if (INSTANCE != null) {
            INSTANCE.shutdown();
        }
    }

}
