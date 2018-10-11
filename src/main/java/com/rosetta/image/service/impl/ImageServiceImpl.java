package com.rosetta.image.service.impl;

import com.rosetta.image.detect.DhashDetector;
import com.rosetta.image.detect.PyramidDetect;
import com.rosetta.image.entity.Hash;
import com.rosetta.image.entity.Image;
import com.rosetta.image.mapper.HashMapper;
import com.rosetta.image.mapper.ImageMapper;
import com.rosetta.image.service.IImageService;
import com.rosetta.image.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: nya
 * @Date: 18-8-27 上午10:03
 */
@Service
public class ImageServiceImpl implements IImageService {


    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Autowired
    private PyramidDetect pyramidDetect;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private HashMapper hashMapper;
    @Autowired
    private DhashDetector dhashDetector;

    /**
     * 根据图片路径,入库特征集
     * @param path 图片url
     */
    @Transactional
    @Override
    public void detectImagePath(String path) {
        Mat srcImg;
        long aa = System.currentTimeMillis();
        try {
            long mm = System.currentTimeMillis();
            srcImg = pyramidDetect.pathToOriginalMat(path);
            Integer dhashGRAY32 = dhashDetector.getDhashGRAY32(srcImg);
            int[] dhashGRAY128 = dhashDetector.getDhashGRAY128(srcImg);
            Integer[] dhash128Boxed = Arrays.stream(dhashGRAY128).boxed().toArray(Integer[]::new);
            String dhash128 = StringUtils.join(dhash128Boxed, ",");

            Set<Integer> features = new HashSet<>(); // 去重后的特征集合
            List<Integer> intFeatures = pyramidDetect.featuresByPath(srcImg);
            int featuresSize = intFeatures.size();
            Map<Integer,Integer> map = new HashMap<>();
            for (Integer fea :
                    intFeatures) {
                if (map.containsKey(fea)) {
                    Integer integer = map.get(fea);
                    integer += 1;
                    map.put(fea , integer);
                }else {
                    features.add(fea);
                    map.put(fea,1);
                }
            }

            log.info("pyramid distinct detect features size : " + features.size());
            long nn = System.currentTimeMillis();
            log.info("mat get features cost millis : " + (nn - mm));

            String pyramidHash = StringUtils.join(features.toArray(), ",");

            String imageKey = MD5Utils.md5(path);

            Image image = new Image();
            image.setImageKey(imageKey);
            image.setImageSrc(path);
            image.setPyramidHash(pyramidHash);
            image.setNorm(Math.sqrt(features.size()));
            image.setDhash32Key(dhashGRAY32);
            image.setDhash128Key(dhash128);
            image.setCreateTime(nn);
            image.setUpdateTime(nn);
            imageMapper.insertSelective(image);
            long qq = System.currentTimeMillis();
            // 遍历 特征集合
            List<Hash> insertHashs = new ArrayList<>(features.size());
            List<Hash> updateHashs = new ArrayList<>(features.size());
            List<Hash> hashes = hashMapper.selectBatch(new ArrayList<>(features));
            Map<Integer,Hash> hashMap = new HashMap<>();
            for (Hash ha :
                    hashes) {
                hashMap.put(ha.getPyramidKey(), ha);
            }
            for (Integer feature :
                    features) {
                Hash hash = hashMap.get(feature);
                Integer count = map.get(feature);
                Double sf = (double)count/featuresSize;
                String hashImageKey = imageKey + "_" + sf;
                if (hash != null) {
                    String images = hash.getImages();
                    List<String> strImages = Arrays.asList(StringUtils.split(images, ","));
                    strImages = new ArrayList<>(strImages); // 解决 UnsupportedOperationException 异常
                    strImages.add(hashImageKey);
                    String imagesParam = StringUtils.join(strImages.toArray(), ",");
                    hash.setImages(imagesParam);
                    hash.setUpdateTime(qq);
                    updateHashs.add(hash);
                } else {
                    hash = new Hash();
                    hash.setPyramidKey(feature);
                    hash.setIdf(1.0);
                    hash.setImages(hashImageKey);
                    hash.setCreateTime(qq);
                    hash.setUpdateTime(qq);
                    insertHashs.add(hash);
                }
            }
            long pp = System.currentTimeMillis();
            log.info("get insert and update list cost millis : " + (pp - qq));
            log.info("insert list hash size : " + insertHashs.size());
            log.info("update list hash size : " + updateHashs.size() + " list : " + updateHashs);
            long xx = System.currentTimeMillis();
            if (insertHashs.size() > 0) {
                hashMapper.insertBatch(insertHashs);
            }
            long zz = System.currentTimeMillis();
            log.info("batch insert cost millis : " + (zz - xx));
            if (updateHashs.size() > 0) {
                hashMapper.updateBatch(updateHashs);
            }
            long yy = System.currentTimeMillis();
            log.info("batch update cost millis : " + (yy - zz));
        } catch (Exception e) {
            log.error("detect insert error : " , e);
        }
        long bb = System.currentTimeMillis();

        log.info("image save features cost millis : " + (bb - aa ));

    }

    public static void main(String[] args) {
        boolean contains = StringUtils.contains("a", "aabb");
        System.out.println(contains);
    }
}
