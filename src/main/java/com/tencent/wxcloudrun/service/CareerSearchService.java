package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.HotList;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

public interface CareerSearchService {
    List<HotList> getHotList(Integer type);
    StreamingResponseBody search(String llmParameter) throws Exception ;
}
