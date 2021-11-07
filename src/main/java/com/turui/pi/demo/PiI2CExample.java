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
    /**PCA9685参考：https://zhuanlan.zhihu.com/p/67336644 */

    //I2C设备地址，查看方式：sudo i2cdetect -y 1
    private static final int PCA9685_ADDR = 0x40;
    //PWM频率25MHz
    private static final float CLOCK_FREQ = 25000000F;
    private static final byte MODE1 = 0x00;
    private static final byte MODE2 = 0x01;
    private static final int PRE_SCALE = 0xFE;
    private static final byte LED0_ON_L = 0x06;
    private static final byte LED0_ON_H = 0x07;
    private static final byte LED0_OFF_L = 0x08;
    private static final byte LED0_OFF_H = 0x09;
    private static final int ALLLED_ON_L = 0xFA;
    private static final int ALLLED_ON_H = 0xFB;
    private static final int ALLLED_OFF_L = 0xFC;
    private static final int ALLLED_OFF_H = 0xFD;

    private static final byte SHIFT = 4;
    //舵机通道14,15
    private static final int CHANNEL_1 = 14;
    private static final int CHANNEL_2 = 15;

    private static Context pi4j;
    private static final Console console = new Console();


    //初始化
    private static void initPCA9685(I2C pca9685) throws InterruptedException {
        pca9685.writeRegister(MODE1, 0X00);
        pca9685.writeRegister(MODE2, 0X04); //可删?
        int preScale = (int) Math.floor((CLOCK_FREQ / 4096) / 50 - 0.5);

        int oldMode = pca9685.readRegister(MODE1);
        console.println("oldMode:" + oldMode);
        //sleep
        int newMode = (oldMode & 0x7F) | 0x10;
        pca9685.writeRegister(MODE1, newMode);

        pca9685.writeRegister(MODE1, newMode);
        //设置频率
        pca9685.writeRegister(PRE_SCALE, preScale);
        pca9685.writeRegister(MODE1, oldMode);
        Thread.sleep(500);
        pca9685.writeRegister(MODE1, oldMode | 0xa1); //0x80?

        //初始化舵机
        pca9685.writeRegister(LED0_ON_L + SHIFT * CHANNEL_1, 0);
        pca9685.writeRegister(LED0_ON_H + SHIFT * CHANNEL_1, 0);
        pca9685.writeRegister(LED0_ON_L + SHIFT * CHANNEL_2, 0);
        pca9685.writeRegister(LED0_ON_H + SHIFT * CHANNEL_2, 0);
    }


    public static void main(String[] args) throws InterruptedException {

        console.title("<-- The Pi4J Project -->", "Minimal Example project");
        //自动生成，context需要全局唯一
        pi4j = Pi4J.newAutoContext();

        console.title("<-- Pi4J Context created -->");
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);


        console.println("----  i2c start  ----");
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        //查看i2c设备地址：sudo i2cdetect -y 1
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("PCA9685")
                .bus(1)
                .device(PCA9685_ADDR)
                .build();
        try (I2C pca9685 = i2CProvider.create(i2cConfig)) {
            //初始化
            initPCA9685(pca9685);
            Thread.sleep(200);
            console.println("----  pca9685 init  ----");
            setServoPulse(pca9685, CHANNEL_1, 1500);
            Thread.sleep(200);
            setServoPulse(pca9685, CHANNEL_2, 1500);
            console.println("----  set pwm  ----");
            Thread.sleep(200);
        }
        console.println("----  i2c end  ----");
        pi4j.shutdown();
    }

    public static void setPWM(I2C pca9685, int channel, int on, int off) {
        pca9685.writeRegister(LED0_ON_L + SHIFT * channel, on & 0xFF);
        pca9685.writeRegister(LED0_ON_H + SHIFT * channel, on >> 8);
        pca9685.writeRegister(LED0_OFF_L + SHIFT * channel, on >> 0xFF);
        pca9685.writeRegister(LED0_OFF_H + SHIFT * channel, on >> 8);
    }

    public static void setServoPulse(I2C pca9685, int channel, int pulse) {
        pulse = pulse * 4096 / 20000;
        setPWM(pca9685, channel, 0, pulse);
    }




}
