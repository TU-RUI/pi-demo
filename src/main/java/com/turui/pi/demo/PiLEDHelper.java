package com.turui.pi.demo;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-11-14
 */
public class PiLEDHelper {
    /**
     * 注意这里需要填BCM编码，BCM 17 = PIN 11
     */
    private static final int PIN_LED = 17;

    private static DigitalOutput LED = null;

    public static DigitalOutput getLed() {
        if (LED == null) {
            synchronized (PiLEDHelper.class) {
                if (LED == null) {
                    LED = Pi4jHelper.getContext().dout().create(PIN_LED);
                    PrintInfo.title("----  LED init ----");
                }
            }
        }
        return LED;
    }

    /**
     * 程序结束需要release()，避免gpio短路
     */
    public static void release() {
        if (LED != null) {
            LED.state(DigitalState.LOW);
        }
    }

}
