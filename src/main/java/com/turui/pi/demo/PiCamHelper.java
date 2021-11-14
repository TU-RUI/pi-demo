package com.turui.pi.demo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uk.co.caprica.picam.ByteArrayPictureCaptureHandler;
import uk.co.caprica.picam.Camera;
import uk.co.caprica.picam.CameraConfiguration;
import uk.co.caprica.picam.CameraException;
import uk.co.caprica.picam.CaptureFailedException;
import uk.co.caprica.picam.FilePictureCaptureHandler;
import uk.co.caprica.picam.NativeLibraryException;
import uk.co.caprica.picam.PicamNativeLibrary;
import uk.co.caprica.picam.enums.Encoding;

/**
 * @author turui <turui@kuaishou.com>
 * Created on 2021-11-14
 */
public class PiCamHelper {
    private static final String CAM_PATH = "/home/turui/picam/";

    private static PiCamHelper INSTANCE = null;
    private static CameraConfiguration config = null;
    private static Camera camera = null;

    private PiCamHelper() {
        try {
            PicamNativeLibrary.installTempLibrary();
            config = CameraConfiguration.cameraConfiguration()
                    .width(1920)
                    .height(1080)
                    .encoding(Encoding.JPEG)
                    .quality(85);
            camera = new Camera(config);
            camera.takePicture(new FilePictureCaptureHandler(getPicturePath()), 2000);
        } catch (NativeLibraryException | CameraException | CaptureFailedException e) {
            PrintInfo.getConsole().println(e);
        }
    }

    public static PiCamHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (PiCamHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PiCamHelper();
                    PrintInfo.title("----  Camera init ----");
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 拍照，返回照片地址
     * @return
     */
    public String savePicture() {
        try {
            String fileName = getPicturePath();
            camera.takePicture(new FilePictureCaptureHandler(new File(fileName)), 5);
            return fileName;
        } catch (CaptureFailedException e) {
            PrintInfo.println("takePicture fail!, e=" + e.getMessage());
        }
        return null;
    }

    /**
     * 拍照，返回图片数据
     * @return
     */
    public byte[] takePicture() {
        try {
            byte[] picture = camera.takePicture(new ByteArrayPictureCaptureHandler(), 5);
            return picture;
        } catch (CaptureFailedException e) {
            PrintInfo.println("takePicture fail!, e=" + e.getMessage());
        }
        return null;
    }

    /**
     * 拍照，并返回图片数据
     * @return
     */
    public byte[] takeAndSavePicture() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data;
        try {
            String fileName = getPicturePath();
            File picture = camera.takePicture(new FilePictureCaptureHandler(new File(fileName)), 5);
            BufferedImage bi = ImageIO.read(picture);
            ImageIO.write(bi, "jpg", baos);
            data = baos.toByteArray();
            return data;
        } catch (CaptureFailedException | IOException e) {
            PrintInfo.println("takePicture fail!, e=" + e.getMessage());
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 程序结束后需要release
     */
    public void release() {
        if (camera != null) {
            camera.close();
        }
        PrintInfo.title("------ camera clase -------");
    }

    private String getPicturePath() {
        return CAM_PATH + CommonUtils.getPictureName() + ".jpg";
    }

}
