package com.turui.pi.demo;

import java.util.concurrent.TimeUnit;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.platform.MockPlatform;
import com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogInputProvider;
import com.pi4j.plugin.mock.provider.gpio.analog.MockAnalogOutputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import com.pi4j.plugin.mock.provider.i2c.MockI2CProvider;
import com.pi4j.plugin.mock.provider.pwm.MockPwmProvider;
import com.pi4j.plugin.mock.provider.serial.MockSerialProvider;
import com.pi4j.plugin.mock.provider.spi.MockSpiProvider;
import com.pi4j.util.Console;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-10-27
 */
public class PiExample {
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
//        DigitalOutput digitalOutput = pi4j.dout().create(PIN_LED);
//        digitalOutput.addListener(System.out::println);
//        //闪烁
//        digitalOutput.state(DigitalState.HIGH)
//                .state(DigitalState.LOW)
//                .state(DigitalState.HIGH)
//                .state(DigitalState.LOW);
//        //
//        digitalOutput.toggle()
//                .toggle()
//                .toggle();
//        digitalOutput.low().high().low().high();
//
//        console.println("CURRENT DIGITAL OUTPUT [" + digitalOutput + "] STATE IS [" + digitalOutput.state() + "]");
//        console.println("PULSING OUTPUT STATE TO HIGH FOR 3 SECONDS");
//        digitalOutput.pulse(3, TimeUnit.SECONDS, DigitalState.HIGH);
//        console.println("PULSING OUTPUT STATE COMPLETE");

        pi4j.shutdown();
    }


}
