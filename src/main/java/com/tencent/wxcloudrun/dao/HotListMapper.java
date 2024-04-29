package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.HotList;
import com.tencent.wxcloudrun.model.RelatedFile;
import com.tencent.wxcloudrun.model.SearchList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotListMapper {
    List<HotList> getHotList(Integer typesId);
    void updateHotListTimes(Long docId);
    void updateHotListLikes(Long docId);


}
