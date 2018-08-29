package com.rosetta.image.service;

/**
 * @Author: nya
 * @Date: 18-8-27 上午10:03
 */
public interface IImageService {

    // 根据路径获取并入库图片特征
    void detectImagePath(String path);

}
