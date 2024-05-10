package com.tencent.wxcloudrun.dao;


import com.tencent.wxcloudrun.model.HotDownloadsFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotDownloadsMapper {
    List<HotDownloadsFile> getRelatedFile(boolean hot);
    void updateDownloads(Long docId);
    String getDownloadsUrl(Long docId);
    List<HotDownloadsFile> getHotDownloads(int fileType);
}
