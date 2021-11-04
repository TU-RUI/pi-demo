package com.turui.pi.demo;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.util.Console;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-10-27
 */
public class PiI2CExample {
    /**
     * I2C设备地址，查看方式：sudo i2cdetect -y 1
     */
    private static final int PCA9685_INPUT = 0x40;
    /**
     * I2C设备输出通道，0-16
     */
    private static final byte PCA9685_OUTPUT_1 = 0x01;
    private static final byte PCA9685_OUTPUT_2 = 0x01;

    public static void main(String[] args) throws InterruptedException {
        final Console console = new Console();
        console.title("<-- The Pi4J Project -->", "Minimal Example project");
        //自动生成，context需要全局唯一
        Context pi4j = Pi4J.newAutoContext();

        console.title("<-- Pi4J Context created -->");
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);


        console.println("----  i2c start  ----");
        I2CProvider i2CProvider = pi4j.provider("i2c");
        //查看i2c设备地址：sudo i2cdetect -y 1
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j).id("PCA9685").bus(1).device(PCA9685_INPUT).build();
        try (I2C pca9685 = i2CProvider.create(i2cConfig)) {
            int config1 = pca9685.readRegister(PCA9685_OUTPUT_1);
            int config2 = pca9685.readRegister(PCA9685_OUTPUT_2);
            if (config1 < 0) {
                throw  new IllegalStateException(
                        "Failed to read configuration from address 0x" + String.format("%02x", PCA9685_OUTPUT_1));
            }
            if (config2 < 0) {
                throw  new IllegalStateException(
                        "Failed to read configuration from address 0x" + String.format("%02x", PCA9685_OUTPUT_2));
            }
            byte state1 = 0x00;
            pca9685.writeRegister(PCA9685_OUTPUT_1, state1);
            pca9685.writeRegister(PCA9685_OUTPUT_2, state1);
            setPin(state1, 0, pca9685, PCA9685_OUTPUT_1, true);
            setPin(state1, 1, pca9685, PCA9685_OUTPUT_1, true);
        }


        console.println("----  i2c end  ----");

        pi4j.shutdown();
    }

    public static byte setPin(byte currentState, int pin, I2C i2c, byte addr,  boolean high) {
        byte newState;
        if (high) {
            newState = (byte) (currentState | (1 << pin));
        } else {
            newState = (byte) (currentState & ~(1 << pin));
        }

        System.out.println("Setting" + i2c.getName() + " to new state " + asBinary(newState));
        i2c.writeRegister(addr, newState);
        return newState;
    }

    public static String asBinary(byte b) {
        StringBuilder sb = new StringBuilder();

        sb.append(((b >>> 7) & 1));
        sb.append(((b >>> 6) & 1));
        sb.append(((b >>> 5) & 1));
        sb.append(((b >>> 4) & 1));
        sb.append(((b >>> 3) & 1));
        sb.append(((b >>> 2) & 1));
        sb.append(((b >>> 1) & 1));
        sb.append(((b >>> 0) & 1));

        return sb.toString();
    }




}
