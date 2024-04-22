package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.config.SearchType;
import com.tencent.wxcloudrun.dto.RecommendationsRequest;
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

    @PostMapping(value = "/api/careersearch/recommend")
    ApiResponse getRecommendations(@RequestBody RecommendationsRequest recommendationsRequest){
        logger.info("/api/careersearch/search request "+recommendationsRequest.getSearchWords());
        String llmParameter = getKimiparameter(recommendationsRequest.getSearchWords(),null,SearchType.RECOMMENDATIONS);
        String recommendations = null;
        try {
            recommendations = careerSearchService.recommend(llmParameter);
        }catch (Exception e){
            logger.error("LLM 3rd exception "+e.getMessage());
        }
        return ApiResponse.ok(recommendations);
    }

    /**
     * 实现搜索相关文件列表获取和下载链接
     * @param searchRequest
     * @return
     */
    @PostMapping(value = "/api/careersearch/getRelatedFile")
    ApiResponse getRelatedFile(@RequestBody SearchRequest searchRequest){
        return null;
    }

    @PostMapping(value = "/api/careersearch/search")
    ResponseEntity<StreamingResponseBody> search(@RequestBody SearchRequest searchRequest){
        logger.info("/api/careersearch/search request "+searchRequest.getSearchWords() +"|"+searchRequest.getCareerType());
        StreamingResponseBody responseBody = null;
        String llmParameter = getKimiparameter(searchRequest.getSearchWords(),searchRequest.getCareerType(),SearchType.SEARCH);
        try {
            responseBody = careerSearchService.search(llmParameter);
        }catch (Exception e){
            logger.error("LLM 3rd exception "+e.getMessage());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(responseBody);
    }
    private String getKimiparameter(String searchWords, String careerType, SearchType searchType){
        KimiParameter kimiParameter = new KimiParameter();
        kimiParameter.setModel("moonshot-v1-32k");
        List kimiMessage = new ArrayList();
        Map system = new HashMap();
        system.put("role","system");
        system.put("content", "你是职场智搜，你是一名具有丰富经验的职场咨询导师，擅长于职场中的各种问题咨询和问题解答，你只回答职场相关问题，如果出现非职场相关问题，请回答：抱歉我不擅长这个问题喔，您可以咨询职场相关问题。如果是职场问题请给出回答，并给出一个示例和3个相关问题推荐。");

        kimiMessage.add(system);
        Map user = new HashMap();
        user.put("role","user");
        if(searchType.equals(SearchType.SEARCH)) {
            user.put("content",searchWords);
        }else if(searchType.equals(SearchType.RECOMMENDATIONS)){
            user.put("content", "请给出和这个问题："+searchWords+" 相关的3个推荐问题");
        }
        kimiMessage.add(user);
        kimiParameter.setMessages(kimiMessage);
        kimiParameter.setTemperature(0.3F);
        if (searchType.equals(SearchType.SEARCH)) {
            kimiParameter.setStream(true);
        }else if (searchType.equals(SearchType.RECOMMENDATIONS)){
            kimiParameter.setStream(false);
        }
        JSONObject jsonObject = (JSONObject)JSON.toJSON(kimiParameter);
        return jsonObject.toJSONString();
    }
}
