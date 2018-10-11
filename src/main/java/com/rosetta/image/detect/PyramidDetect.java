package com.rosetta.image.detect;

import com.rosetta.image.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * @Author: nya
 * @Date: 18-8-28 下午5:25
 */
@Service
public class PyramidDetect {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final Logger log = LoggerFactory.getLogger(PyramidDetect.class);

    public static void main(String[] args) {
        File file = new File("/home/han/holiday");
        File[] tempList = file.listFiles();
        List<String> files = new ArrayList<>();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].getPath());
            }
            if (tempList[i].isDirectory()) {
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/han/feature.txt"));
            PyramidDetect detect = new PyramidDetect();
            int i = 1 ;
            for (String path :
                    files) {
                Mat mat = detect.pathToOriginalMat(path);
                List<Integer> list = detect.featuresByPath(mat);
                Set<Integer> features = new HashSet<>(); // 去重后的特征集合
                Map<Integer,Integer> map = new HashMap<>();
                for (Integer feature :
                        list) {
                    if (map.containsKey(feature)) {
                        Integer integer = map.get(feature);
                        integer += 1;
                        map.put(feature , integer);
                    }else {
                        int m =1;
                        features.add(feature);
                        map.put(feature,1);
                    }
                }
                List<Integer> listThen = new ArrayList<>(4096);
                for (int j = 0 ; j < 4096 ; j++) {
                    if (map.containsKey(j)) {
                        listThen.add(map.get(j));
                    } else {
                        listThen.add(0);
                    }
                }

                String s = path+":" + listThen;
                bw.write(s);
                bw.newLine();
                bw.flush();
                System.out.println(i);
                i++;
            }
            bw.close();
        } catch (Exception e) {
            log.error("detect error : " , e);
        }

    }

    /**
     * 根据原生mat获取特征集合
     * @param mat original
     * @return features
     */
    public List<Integer> featuresByPath(Mat mat) {
        Mat imgMat = null;
        List<Integer> features = new LinkedList<>();
        try {
            imgMat = matToGray128(mat);

            List<Mat> all = new LinkedList<>();
            Mat clone9 = imgMat.clone();
            Imgproc.resize(imgMat, clone9, new Size(9,9));
            List<Mat> list9 = splitMat(clone9);
            //System.out.println("list9 size : " + list9.size());
            all.addAll(list9);

            Mat clone8 = imgMat.clone();
            Imgproc.resize(imgMat, clone8, new Size(8,8));
            List<Mat> list8 = splitMat(clone8);
            //System.out.println("list8 size : " + list8.size());
            all.addAll(list8);

            Mat clone7 = imgMat.clone();
            Imgproc.resize(imgMat, clone7, new Size(7,7));
            List<Mat> list7 = splitMat(clone7);
            //System.out.println("list7 size : " + list7.size());
            all.addAll(list7);

            Mat clone6 = imgMat.clone();
            Imgproc.resize(imgMat, clone6, new Size(6,6));
            List<Mat> list6 = splitMat(clone6);
            //System.out.println("list6 size : " + list6.size());
            all.addAll(list6);

            Mat clone5 = imgMat.clone();
            Imgproc.resize(imgMat, clone5, new Size(5,5));
            List<Mat> list5 = splitMat(clone5);
            //System.out.println("list5 size : " + list5.size());
            all.addAll(list5);

            Mat clone4 = imgMat.clone();
            Imgproc.resize(imgMat, clone4, new Size(4,4));
            List<Mat> list4 = splitMat(clone4);
            //System.out.println("list4 size : " + list4.size());
            all.addAll(list4);

            Mat clone3 = imgMat.clone();
            Imgproc.resize(imgMat, clone3, new Size(3,3));
            List<Mat> list3 = splitMat(clone3);
            //System.out.println("list3 size : " + list3.size());
            all.addAll(list3);

            //System.out.println("all list size : " + all.size());

            for (Mat temp :
                    all) {
                Integer feature = featureGet(temp);
                features.add(feature);
            }
            //System.out.println("features size : " + features.size());
            //System.out.println(features);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imgMat != null) {
                imgMat.release();
            }
        }
        return features;
    }

    /**
     * 根据 3*3 的图片mat获取特征
     * @param mat
     * @return
     */
    public Integer featureGet(Mat mat) {
        int feature = 0 ;
        try {
            byte[] bytePixels = new byte[3 * 3];
            mat.get(0, 0, bytePixels);
            int[] pixels = new int[bytePixels.length];
            for (int i = 0 ; i < pixels.length; i++) {
                pixels[i] = bytePixels[i] & 0xff;
            }
            for (int i = 0 ; i < 3 ; i++) {
                for (int j = 0 ; j < 3 ; j++) {
                    int index = j * 3 + i ;
                    int indexY = (j + 1) * 3 + i ;
                    int indexX = j * 3 + i + 1 ;
                    if (indexX / 3 == index / 3) {
                        int colBit = pixels[index] > pixels[indexX] ? 1 : 0;
                        feature = (feature << 1) + colBit;
                    }
                    if (indexY / 3 < 3) {
                        int rowBit = pixels[index] > pixels[indexY] ? 1 : 0 ;
                        feature = (feature << 1) + rowBit;
                    }
                }
            }
        } catch (Exception e) {
            log.error("3*3 mat get feature error : " , e);
        } finally {
            if (mat != null) {
                mat.release();
            }
        }
        return feature;
    }

    /**
     * 根据请求Mat,切割为3*3的区块
     * @param paramMat param
     * @return
     */
    public List<Mat> splitMat(Mat paramMat) {
        List<Mat> mats = new LinkedList<>();
        try {
            int height = paramMat.height();
            int x = height / 3;
            int y = height % 3;
            int z = (x - 1) * 3 + y + 1;
            for (int i = 0 ; i < z ; i++) {
                for (int j = 0 ; j < z ; j++) {
                    Rect rect = new Rect(i,j,3,3);
                    Mat thrMat = new Mat(paramMat,rect);
                    mats.add(thrMat);
                }
            }
        } catch (Exception e) {
            log.error("split Mat error : " , e);
        } finally {
            if (paramMat != null) {
                paramMat.release();
            }
        }
        return mats;
    }

    /**
     * path to original mat
     * @param path
     * @return
     */
    public Mat pathToOriginalMat(String path) {
        Mat resultMat = null;
        boolean isRemote = StringUtils.contains(path, "http");
        try {
            if (isRemote) {
                resultMat = urlToMat(path);
            } else {
                resultMat = Imgcodecs.imread(path);
            }
        } catch (Exception e) {
            log.error("pyramid detect pathToOriginalMat error : " , e);
        }
        return resultMat;
    }

    /**
     * url or path to gray mat with 128*128
     * @param mat
     * @return
     */
    public Mat matToGray128(Mat mat) {
        Mat grayMat = null;
        try {
            Mat clone = mat.clone();
            Imgproc.resize(mat, clone, new Size(128,128));
            grayMat = CommonUtils.bgr2gray(clone);
        } catch (Exception e) {
            log.error("path to mat error : " , e);
        } finally {
            if (mat != null) {
                mat.release();
            }
        }
        return grayMat;
    }

    /**
     * 根据路径url获取Mat矩阵
     * @param url remote path
     * @return mat
     */
    public Mat urlToMat(String url) {
        ByteArrayOutputStream os = null;
        Mat encoded = null;
        Mat resultMat = null;
        try {
            URL u = new URL(url);
            BufferedImage image = ImageIO.read(u);

            //convert BufferedImage to byte array
            os = new ByteArrayOutputStream();
            ImageIO.write( image, "jpg", os);
            os.flush();
            encoded = new Mat(1,os.size(), CvType.CV_8U);
            encoded.put(0,0,os.toByteArray());
            //从内存中读，返回Mat形式
            resultMat = Imgcodecs.imdecode(encoded, -1);
        } catch (Exception e){
            log.error("remote url image to mat error : " , e);
        }finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("os close stream error : " , e);
                }
            }
            if (encoded != null) {
                encoded.release();
            }
        }
        return resultMat;
    }


}
