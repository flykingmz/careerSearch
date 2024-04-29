package com.tencent.wxcloudrun.controller;

import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.config.CareerSearchConstants;
import com.tencent.wxcloudrun.config.SearchType;
import com.tencent.wxcloudrun.dto.RecommendationsRequest;
import com.tencent.wxcloudrun.dto.SearchRequest;
import com.tencent.wxcloudrun.model.HotList;
import com.tencent.wxcloudrun.model.KimiParameter;
import com.tencent.wxcloudrun.model.RelatedFile;
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

    /**
     * 热点话题推荐，依据不同类型推荐，从数据库获取相关话题
     * @param type
     * @return
     */
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

    /**
     * 大模型类似问题推荐列表
     * @param recommendationsRequest
     * @return
     */
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
        //TODO 后续可以基于搜索的关键词解析后做最匹配的相关文件推荐。

        List<RelatedFile> relatedFileList = careerSearchService.getRelatedFile();
        return ApiResponse.ok(relatedFileList);
    }
    @GetMapping(value = "/api/careersearch/updateRelatedFileDownloads")
    ApiResponse updateRelatedFileDownloads(@RequestParam("docId") Integer docId) {
        careerSearchService.updateRelatedFileDownloads(docId);
        return ApiResponse.ok();
    }

    /**
     * 统计点赞次数
     * @param docId
     * @return
     */
    @GetMapping(value = "/api/careersearch/statistics/like")
    ApiResponse like(@RequestParam("docId") int docId){
         careerSearchService.like(docId);
         return ApiResponse.ok();
    }

    /**
     * 统计阅读次数
     * @param docId
     * @return
     */
//    @GetMapping(value = "/api/careersearch/statistics/read")
//    ApiResponse readStatistics(@RequestParam("docId") String docId){
//        return null;
//    }

    /**
     * 大模型搜索主入口
     * @param searchRequest
     * @return
     */
    @PostMapping(value = "/api/careersearch/search")
    ResponseEntity<StreamingResponseBody> search(@RequestBody SearchRequest searchRequest){
        logger.info("/api/careersearch/search request "+searchRequest.getSearchWords() +"|"+searchRequest.getCareerType());
        //调用搜索列表流水记录,可以修改为异步实现
        careerSearchService.searchRecord(searchRequest,true);

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
    private String getKimiparameter(String searchWords, Integer careerType, SearchType searchType){
        KimiParameter kimiParameter = new KimiParameter();
        kimiParameter.setModel("moonshot-v1-32k");
        List kimiMessage = new ArrayList();
        Map system = new HashMap();
        system.put("role","system");
        system.put("content", CareerSearchConstants.ADMINPROMT);

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
