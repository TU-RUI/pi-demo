package com.turui.pi.demo;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-10-27
 */
public class PiExample {
    /** PIN 15 = BCM 22 */
    private static final int PIN_LED = 22;

    public static void main(String[] args) throws InterruptedException {
        final Console console = new Console();
        console.title("<-- The Pi4J Project -->", "Minimal Example project");
        Context pi4j = Pi4J.newAutoContext();
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        // Here we will create I/O interfaces for a (GPIO) digital output
        // and input pin. We define the 'provider' to use PiGpio to control
        // the GPIO.
        DigitalOutputConfigBuilder ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        DigitalOutput led = pi4j.create(ledConfig);

        for (int i = 0; i < 10; i++) {
            led.high();
            Thread.sleep(1000);
            led.low();
        }
        console.println("led end");

        pi4j.shutdown();
    }

}
