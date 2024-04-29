package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.config.CareerSearchConstants;
import com.tencent.wxcloudrun.dao.HotListMapper;
import com.tencent.wxcloudrun.dao.RelatedFileMapper;
import com.tencent.wxcloudrun.dao.SearchListMapper;
import com.tencent.wxcloudrun.dto.SearchRequest;
import com.tencent.wxcloudrun.model.HotList;
import com.tencent.wxcloudrun.model.RelatedFile;
import com.tencent.wxcloudrun.model.SearchList;
import com.tencent.wxcloudrun.service.CareerSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CareerSearchServiceImpl implements CareerSearchService {

    @Autowired
    final HotListMapper hotListMapper;
    @Autowired
    final RelatedFileMapper relatedFileMapper;
    @Autowired
    final SearchListMapper searchListMapper;

    final ExecutorService executorService = Executors.newFixedThreadPool(5);

    final Logger logger;

    public CareerSearchServiceImpl(HotListMapper hotListMapper, RelatedFileMapper relatedFileMapper, SearchListMapper searchListMapper) {
        this.hotListMapper = hotListMapper;
        this.relatedFileMapper = relatedFileMapper;
        this.searchListMapper = searchListMapper;
        this.logger = LoggerFactory.getLogger(CareerSearchServiceImpl.class);
    }


    @Override
    public List<HotList> getHotList(Integer type) {
        return hotListMapper.getHotList(type);
    }

    @Override
    public StreamingResponseBody search(String llmParameter) throws Exception {
        logger.info("search into 3rd llm " + llmParameter);
        // 获取输出流并写入数据
        HttpURLConnection conn = getConect();
        OutputStream os = conn.getOutputStream();
        byte[] input = llmParameter.getBytes("UTF-8");
        os.write(input, 0, input.length);
        // 获取响应码和响应内容
        StreamingResponseBody responseBody = null;
        logger.info("url return code:" + conn.getResponseCode());
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            responseBody = outputStream -> {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info(line);
                    outputStream.write(line.getBytes());
                }
                reader.close();
                outputStream.close();
            };
        }
        return responseBody;
    }

    private HttpURLConnection getConect() throws Exception {
        URL url = new URL(CareerSearchConstants.KIMICHARTURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + CareerSearchConstants.KIMIKEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        logger.info("search into 3rd llm url " + CareerSearchConstants.KIMICHARTURL);
        logger.info("search into 3rd llm key " + CareerSearchConstants.KIMIKEY);
        return conn;
    }

    @Override
    public String recommend(String llmParameter) throws Exception {
        logger.info("recommend into 3rd llm " + llmParameter);
        // 获取输出流并写入数据
        HttpURLConnection conn = getConect();
        // 获取输出流并写入数据
        OutputStream os = conn.getOutputStream();
        byte[] input = llmParameter.getBytes("UTF-8");
        os.write(input, 0, input.length);
        // 获取响应码和响应内容
        StringBuilder response = new StringBuilder();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    @Override
    public void searchRecord(SearchRequest searchRequest,boolean async) {
        if(async) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String searchWords = searchRequest.getSearchWords();
                    Integer careerType = searchRequest.getCareerType();
                    int docId = searchRequest.getDocId();
                    SearchList searchList = new SearchList();
                    searchList.setTitle(searchWords);
                    searchList.setTypesId(careerType);
                    searchListMapper.insertSearchRecord(searchList);
                    if (docId > 0) {
                        hotListMapper.updateHotListTimes(docId);
                    }
                }
            });
        }else {
            String searchWords = searchRequest.getSearchWords();
            Integer careerType = searchRequest.getCareerType();
            int docId = searchRequest.getDocId();
            SearchList searchList = new SearchList();
            searchList.setTitle(searchWords);
            searchList.setTypesId(careerType);
            searchListMapper.insertSearchRecord(searchList);
            if (docId > 0) {
                hotListMapper.updateHotListTimes(docId);
            }
        }
    }

    @Override
    public void like(Integer docId) {
        /*docId大于0代表的是热点搜索数据，其它普通搜索不具备docId
       只有离线通过搜索流水分析后存在热点的话题，才会进入到hotlist，这时才会出现docId
         */

        if(docId > 0) {
            hotListMapper.updateHotListLikes(docId);
        }
    }

    @Override
    public List<RelatedFile> getRelatedFile() {
        return relatedFileMapper.getRelatedFile(true);
    }

    @Override
    public void updateRelatedFileDownloads(Integer docId) {
        relatedFileMapper.updateDownloads(docId);
    }
}
