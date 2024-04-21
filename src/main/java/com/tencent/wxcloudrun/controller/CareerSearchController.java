package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.SearchRequest;
import com.tencent.wxcloudrun.model.HotList;
import com.tencent.wxcloudrun.model.KimiParameter;
import com.tencent.wxcloudrun.service.CareerSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class CareerSearchController {

    @Autowired
    final CareerSearchService careerSearchService;
    final Logger logger;

    public CareerSearchController(CareerSearchService careerSearchService) {
        this.careerSearchService = careerSearchService;
        this.logger = LoggerFactory.getLogger(CareerSearchController.class);
    }

    @GetMapping(value = "/api/careersearch/hotlist/v1")
    ApiResponse getHotListV1(@RequestParam("type") Integer type) {
        logger.info("/api/careersearch/hotlist/v1 request");
        List<HotList> hotlist = null;
        try {
            hotlist = careerSearchService.getHotList(type);
        }catch (Exception e){
            logger.error("hotlist exception "+e.getMessage());
        }
        return ApiResponse.ok(hotlist);
    }

    @PostMapping(value = "/api/careersearch/search")
    ResponseEntity<StreamingResponseBody> search(@RequestBody SearchRequest request){
        logger.info("/api/careersearch/search request");
        KimiParameter kimiParameter = new KimiParameter();
        kimiParameter.setModel("moonshot-v1-32k");
        List kimiMessage = new ArrayList();
        Map system = new HashMap();
        system.put("role","system");
        system.put("content","你是职场智搜，你是一名具有丰富经验的职场咨询导师，擅长于职场中的各种问题咨询和问题解答，你只回答职场相关问题，如果出现非职场相关问题，请回答：抱歉我不擅长这个问题喔，您可以咨询职场相关问题。如果是职场问题请给出回答，并给出一个示例和3个相关问题推荐。");
        kimiMessage.add(system);
        Map user = new HashMap();
        user.put("role","user");
        user.put("content",request.getSearchWords());
        kimiMessage.add(user);
        kimiParameter.setMessages(kimiMessage);
        kimiParameter.setTemperature("0.3");
        kimiParameter.setStream(false);
        StreamingResponseBody responseBody = null;
        JSONObject jsonObject = (JSONObject)JSON.toJSON(kimiParameter);
        String llmParameter = jsonObject.toJSONString();
        try {
            responseBody = careerSearchService.search(llmParameter);
        }catch (Exception e){
            logger.error("LLM 3rd exception "+e.getMessage());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }
}
