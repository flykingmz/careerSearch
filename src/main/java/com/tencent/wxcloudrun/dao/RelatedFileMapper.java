package com.tencent.wxcloudrun.dao;


import com.tencent.wxcloudrun.model.RelatedFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelatedFileMapper {
    List<RelatedFile> getRelatedFile(boolean hot);
    void updateDownloads(Integer docId);
}
