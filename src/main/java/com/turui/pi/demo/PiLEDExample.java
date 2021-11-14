package com.turui.pi.demo;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-10-27
 */
public class PiLEDExample {
    /**
     * 注意这里需要填BCM编码，BCM 17 = PIN 11
     */
    private static final int PIN_LED = 17;

    public static void main(String[] args) throws InterruptedException {
        final Console console = new Console();
        console.title("<-- The Pi4J Project -->", "Minimal Example project");
        //自动生成，context需要全局唯一
        Context pi4j = Pi4J.newAutoContext();
        //测试用mock对象
//        Context pi4j = Pi4J.newContextBuilder()
//                .add(new MockPlatform())
//                .add(MockAnalogInputProvider.newInstance(),
//                        MockAnalogOutputProvider.newInstance(),
//                        MockSpiProvider.newInstance(),
//                        MockPwmProvider.newInstance(),
//                        MockSerialProvider.newInstance(),
//                        MockI2CProvider.newInstance(),
//                        MockDigitalInputProvider.newInstance(),
//                        MockDigitalOutputProvider.newInstance())
//                .build();
        console.title("<-- Pi4J Context created -->");
        PrintInfo.printLoadedPlatforms(pi4j);
        PrintInfo.printDefaultPlatform(pi4j);
        PrintInfo.printProviders(pi4j);

        DigitalOutput led = pi4j.dout().create(PIN_LED);
        console.println("---- led start ----");
        for (int i = 0; i < 10; i++) {
            led.state(DigitalState.HIGH);
            console.println("---- led high ----");
            Thread.sleep(1000);
            led.state(DigitalState.LOW);
            console.println("---- led low ----");
            Thread.sleep(1000);
        }
        console.println("----  led end  ----");

        pi4j.shutdown();
    }




}
