package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.SearchRequest;
import com.tencent.wxcloudrun.model.HotList;
import com.tencent.wxcloudrun.model.HotDownloadsFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface CareerSearchService {
    List<HotList> getHotList(Integer type);
    StreamingResponseBody streamSearch(String llmParameter) throws Exception ;

    String search(String llmParameter) throws Exception ;

    String recommend(String llmParameter) throws Exception ;
    void searchRecord(SearchRequest searchRequest,boolean async);
    void like(Long docId);

    List<HotDownloadsFile> getRelatedFile();
    void updateRelatedFileDownloads(Long docId);

    String downloads(Long docId);

    List<HotDownloadsFile> getHotDownloads(int fileType);
}
