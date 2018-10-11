package com.rosetta.image.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RestSearchController
 * @Decription: TODO
 * @Author: nya
 * @Date: 18-10-11 下午3:24
 * @Version: 1.0
 **/
public class RestSearchController {

    public static void main(String[] args) {
        File file = new File("/home/nya/sources/holiday");
        File[] tempList = file.listFiles();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/nya/image_search/searchResult.txt"));
            int i = 1;
            int total = 0;
            for (File fi :
                    tempList) {

                long aa = System.currentTimeMillis();
                String path = fi.getPath();
                String url = "http://127.0.0.1:8100/search/get";

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.add("charset","UTF-8");

                LinkedMultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
                paramMap.add("path",path);

                HttpEntity<LinkedMultiValueMap<String,Object>> httpEntity = new HttpEntity<>(paramMap, requestHeaders);

                ResponseEntity<String> exchange = restTemplate.postForEntity(url,httpEntity,String.class);

                String resultRemote = exchange.getBody();

                JSONObject parse = (JSONObject) JSONObject.parse(resultRemote);

                String data = parse.getString("data");
                JSONObject dataJson = (JSONObject) JSONObject.parse(data);
                String result = dataJson.getString("result");
                JSONArray objects = JSONArray.parseArray(result);
                if (objects == null) continue;
                List<String> resultStr = new ArrayList<>(5);
                String substring = path.substring(path.length()-10, path.length()-4);
                int all = 0 ;


                if (objects.size() > 0) {
                    for (int j = 0 ; j < objects.size() ; j ++) {
                        Object o = objects.get(j);
                        JSONObject o1 = (JSONObject) o;
                        String path1 = o1.getString("path");
                        String score = o1.getString("score");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("path",path1);
                        jsonObject.put("score",score);
                        resultStr.add(jsonObject.toString());
                        String here = path1.substring(path1.length()-10,path1.length()-4);
                        if (Math.abs(Integer.parseInt(substring) - Integer.parseInt(here)) < 5) {
                            all ++ ;
                        }
                    }
                }
                if (all > 1) {
                    total++;
                }

                long bb = System.currentTimeMillis();
                System.out.println(path + " index : " + i + " search cost millis : " + (bb - aa) + " all : " + all + " num : " + total );

                String resultHere = path + " - " + all + " - " + resultStr;
                i++;
                bw.write(resultHere);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
