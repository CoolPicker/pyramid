package com.rosetta.image.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.rosetta.image.detect.PyramidDetect;
import com.rosetta.image.entity.Hash;
import com.rosetta.image.entity.Image;
import com.rosetta.image.mapper.HashMapper;
import com.rosetta.image.mapper.ImageMapper;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: nya
 * @Date: 18-8-28 上午10:57
 */
@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private PyramidDetect pyramidDetect;
    @Autowired
    private HashMapper hashMapper;
    @Autowired
    private ImageMapper imageMapper;

    /**
     * 根据url 获取匹配的图片路径
     * @param path param图片路径
     * @return 结果集
     */
    public String searchImageByPath(String path) {
        Mat srcImg = null;
        List<SortImage> collect = new ArrayList<>() ;
        long aa = System.currentTimeMillis();
        try {
            long mm = System.currentTimeMillis();
            srcImg = pyramidDetect.pathToOriginalMat(path);

            List<Integer> features = pyramidDetect.featuresByPath(srcImg);



            long nn = System.currentTimeMillis();
            List<Hash> hashes = hashMapper.selectBatch(features);
            long oo = System.currentTimeMillis();
            List<SortImage> sortImages = new LinkedList<>();
            Map<String,List<Double>> imageIdfMap = new HashMap<>();
            for (Hash hash :
                    hashes) {
                Double idf = hash.getIdf();
                String images = hash.getImages();
                List<String> strImages = Arrays.asList(StringUtils.split(images, ","));
                for (String imageKey :
                        strImages) {
                    if (imageIdfMap.containsKey(imageKey)) {
                        List<Double> doubles = imageIdfMap.get(imageKey);
                        doubles.add(idf);
                        imageIdfMap.put(imageKey,doubles);
                    } else {
                        List<Double> doubles = new LinkedList<>();
                        doubles.add(idf);
                        imageIdfMap.put(imageKey,doubles);
                    }
                }
            }
            log.info("all images come to sort size is : " + imageIdfMap.size());
            for (String key :
                    imageIdfMap.keySet()) {
                SortImage sortImage = new SortImage();
                List<Double> doubles = imageIdfMap.get(key);
                sortImage.setKey(key);
                // 根据key获取图片详情
                //Image image = imageMapper.selectByImageKey(key);
                Image image = new Image();
                sortImage.setPath(image.getImageSrc());
                // 所有筒idf的平方和
                Double reduce = doubles.stream().map(item -> Math.pow(item, 2)).reduce(0.0, Double::sum);
                Double norm = image.getNorm();
                double score = reduce / norm;
                sortImage.setScore(score);
                sortImages.add(sortImage);
            }
            log.info("sort images size : " + sortImages.size());
            collect = sortImages.stream().sorted(Comparator.comparing(SortImage::getScore).reversed()).collect(Collectors.toList());
            collect.stream().forEach(System.out::println);
            log.info("detect sift cost millis : " + (nn - mm));
            log.info("batch get hashes cost millis : " + (oo - nn));
        } catch (Exception e) {
            log.error("search error for path : " + path , e);
        } finally {
            if (srcImg != null) {
                srcImg.release();
            }
        }
        String result = JSONArray.toJSONString(collect);
        long bb = System.currentTimeMillis();

        log.info("search cost millis : " + (bb - aa));

        return result;
    }



    class SortImage {
        private String key;
        private String path;
        private Double score;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "SortImage{" +
                    "path='" + path + '\'' +
                    ", score=" + score +
                    '}';
        }
    }
}
