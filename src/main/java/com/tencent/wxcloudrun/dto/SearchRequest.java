package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class SearchRequest {
    String searchWords;
    Integer careerType;
    Long docId;
}
