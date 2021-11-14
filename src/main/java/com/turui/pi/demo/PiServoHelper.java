package com.turui.pi.demo;

import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-11-14
 * 使用前手动调用init()方法初始化资源，程序结束后调用release()方法清理资源
 */
public class PiServoHelper {
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
    //不用通道偏移量
    private static final byte SHIFT = 4;

    //舵机通道0,1
    public static final int CHANNEL_1 = 0;
    public static final int CHANNEL_2 = 1;
    public static final int MIN_PULSE = 500;
    public static final int MAX_PULSE = 2500;
    public static final int MID_PULSE = 1500;

    private static I2C pca9685;
    private static PiServoHelper INSTANCE = null;

    private PiServoHelper() {
        try {
            initI2c();
        } catch (InterruptedException e) {
            PrintInfo.getConsole().println(e);
        }
        PrintInfo.title("----  Servo init ----");
    }

    public static PiServoHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (PiServoHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PiServoHelper();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 设置舵机角度
     * @param channel 舵机通道
     * @param pulse 占空比，500~2500，中位1500
     */
    public void setServoPulse(int channel, int pulse) {
        pulse = pulse * 4096 / 20000;
        setPWM(pca9685, channel, 0, pulse);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            PrintInfo.getConsole().println(e);
        }
        PrintInfo.println("setServoPulse channel=" + channel + ", pulse=" + pulse);
    }

    /**
     * 程序结束后需要release
     */
    public void release() {
        if (pca9685 != null) {
            pca9685.writeRegister(LED0_ON_L + SHIFT * CHANNEL_1, 0);
            pca9685.writeRegister(LED0_ON_H + SHIFT * CHANNEL_1, 0);
            pca9685.writeRegister(LED0_ON_L + SHIFT * CHANNEL_2, 0);
            pca9685.writeRegister(LED0_ON_H + SHIFT * CHANNEL_2, 0);
            pca9685.close();
        }
        PrintInfo.title("------ pca9685 release -------");
    }



    /***********************************************/

    private void initI2c() throws InterruptedException {
        I2CProvider i2CProvider = Pi4jHelper.getContext().provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(Pi4jHelper.getContext())
                .id("PCA9685")
                .bus(1)
                .device(PCA9685_ADDR)
                .build();
        pca9685 = i2CProvider.create(i2cConfig);
        PrintInfo.title("----  I2C init ----");
        initPCA9685(pca9685);
    }

    private void initPCA9685(I2C pca9685) throws InterruptedException {
        pca9685.writeRegister(MODE1, 0X00);
        pca9685.writeRegister(MODE2, 0X04); //可删?
        int preScale = (int) Math.floor((CLOCK_FREQ / 4096) / 50 - 0.5);

        int oldMode = pca9685.readRegister(MODE1);
        PrintInfo.getConsole().println("oldMode:" + oldMode);
        //sleep
        int newMode = (oldMode & 0x7F) | 0x10;
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
        PrintInfo.title("----  pca9685 init ----");
    }

    private void setPWM(I2C pca9685, int channel, int on, int off) {
        pca9685.writeRegister(LED0_ON_L + SHIFT * channel, on & 0xFF);
        pca9685.writeRegister(LED0_ON_H + SHIFT * channel, on >> 8);
        pca9685.writeRegister(LED0_OFF_L + SHIFT * channel, off & 0xFF);
        pca9685.writeRegister(LED0_OFF_H + SHIFT * channel, off >> 8);
    }



}
