package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.HotList;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CareerSearchMapper {
    List<HotList> getHotList(Integer type);
}
