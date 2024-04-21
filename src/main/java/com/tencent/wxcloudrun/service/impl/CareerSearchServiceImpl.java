package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.config.CareerSearchConstants;
import com.tencent.wxcloudrun.controller.CareerSearchController;
import com.tencent.wxcloudrun.dao.CareerSearchMapper;
import com.tencent.wxcloudrun.model.HotList;
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

@Service
public class CareerSearchServiceImpl implements CareerSearchService {

    @Autowired
    final CareerSearchMapper careerSearchMapper;

    final Logger logger;

    public CareerSearchServiceImpl(CareerSearchMapper careerSearchMapper) {
        this.careerSearchMapper = careerSearchMapper;
        this.logger = LoggerFactory.getLogger(CareerSearchServiceImpl.class);
    }


    @Override
    public List<HotList> getHotList(Integer type) {
        return careerSearchMapper.getHotList(type);
    }

    @Override
    public StreamingResponseBody search(String llmParameter) throws Exception {
        URL url = new URL(CareerSearchConstants.KIMICHARTURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + CareerSearchConstants.KIMIKEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        logger.info("search into 3rd llm " + llmParameter);
        // 获取输出流并写入数据
        OutputStream os = conn.getOutputStream();
        byte[] input = llmParameter.getBytes("UTF-8");
        os.write(input, 0, input.length);
        // 获取响应码和响应内容
        StreamingResponseBody responseBody = null;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            responseBody = outputStream -> {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputStream.write(line.getBytes());
                }
                reader.close();
                outputStream.close();
            };
        }
        return responseBody;
    }
}
