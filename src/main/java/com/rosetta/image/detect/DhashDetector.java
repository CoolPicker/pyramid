package com.rosetta.image.detect;

import com.rosetta.image.exception.DetectException;
import com.rosetta.image.util.CommonUtils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

/**
 * @Author: niuya
 * @Date: 18-6-11 下午3:31
 */
@Service
public class DhashDetector {

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public Integer getDhashGRAY32(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = CommonUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(5, 5);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[5 * 5];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int feature = 0;
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                int colBit = pixels[i*5+j] > pixels[(i+1)*5+j] ? 1 : 0;
                feature = (feature << 1) + colBit;
                int rowBit = pixels[i*5+j] > pixels[i*5+j+1] ? 1 : 0 ;
                feature = (feature << 1) + rowBit;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feature;
    }

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public int[] getDhashGRAY128(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = CommonUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(9, 9);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[9 * 9];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int[] features = new int[8 * 8 * 2 / 32];
        for (int i=0; i<features.length; i++) {
            features[i] = 0;
        }
        int idx = 0;
        for (int j=0; j<8; j++) {
            for (int i=0; i<8; i++) {
                int colBit = pixels[i*9+j] > pixels[(i+1)*9+j] ? 1 : 0;
                features[idx/32] = (features[idx/32] << 1) + colBit;
                idx ++;
                int rowBit = pixels[i*9+j] > pixels[i*9+j+1] ? 1 : 0 ;
                features[idx/32] = (features[idx/32] << 1) + rowBit;
                idx ++;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

}
