package com.rosetta.image.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rosetta.image.constant.PyramidConstant;
import com.rosetta.image.entity.Hash;
import com.rosetta.image.mapper.HashMapper;
import com.rosetta.image.mapper.ImageMapper;
import com.rosetta.image.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: nya
 * @Date: 18-8-27 上午10:16
 */
@Service
public class HashServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(HashServiceImpl.class);

    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private HashMapper hashMapper;

    /**
     * 根据视频数和当前桶图片数,更新库中所有idf值
     */
    public void updateIdf() {
        long aa = System.currentTimeMillis();
        Integer countAll = imageMapper.selectCountAll();

        PageHelper.startPage(1, PyramidConstant.BATCH_UPDATE_PAGE_SIZE);
        List<Hash> params = hashMapper.selectAll();
        PageInfo page = new PageInfo(params);
        batchUpdateIdf(params,countAll);
        long total = page.getTotal();
        int pages = page.getPages();
        log.info("batch update total : " + total + " pages : " + pages);
        for (int i = 2 ; i <= pages ; i++ ) {
            log.info("update batch idf page : " + i + " p..");
            PageHelper.startPage(i,PyramidConstant.BATCH_UPDATE_PAGE_SIZE);
            params = hashMapper.selectAll();
            batchUpdateIdf(params,countAll);
        }

        // 直接获取不可行,需分页获取
        //List<Hash> params = hashMapper.selectAll();

        long bb = System.currentTimeMillis();
        log.info("update hash idf cost millis : " + (bb - aa));
    }

    /**
     * 批量更新idf
     * @param params
     * @param countAll
     */
    public void batchUpdateIdf(List<Hash> params, Integer countAll){
        long aa = System.currentTimeMillis();
        int paramsSize = params.size();
        int threadSize = PyramidConstant.NOW_SERVER_THREAD_SIZE;

        int maxSize = params.size() % threadSize == 0 ? params.size() / threadSize : params.size() / threadSize + 1 ;

        List<List<Hash>> split = CommonUtils.split(params, maxSize);
        try {
            int threadNum = split.size();
            ExecutorService exec = Executors.newFixedThreadPool(threadNum);
            List<Callable<Integer>> tasks = new ArrayList<>();
            Callable<Integer> task;
            for (List<Hash> hashes:
                    split){
                task = () -> {
                    List<Hash> updateList = new ArrayList<>();
                    for (Hash hash :
                            hashes) {
                        String images = hash.getImages();
                        List<String> strImages = Arrays.asList(StringUtils.split(images, ","));
                        int count = strImages.size();
                        double log = Math.log((countAll + 1) / (count + 1));
                        hash.setIdf(log);
                        updateList.add(hash);
                    }
                    // 批量更新
                    hashMapper.updateBatch(updateList);
                    return 1;
                };
                tasks.add(task);
            }
            exec.invokeAll(tasks);
            exec.shutdown();
            long bb = System.currentTimeMillis();
            log.info("batch update idf branches : " + paramsSize + " cost millis : " + (bb - aa) );
        } catch (Exception e) {
            log.error("concurrent thread invoke error" , e);
        }
    }

}
