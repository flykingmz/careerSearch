package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.SearchList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchListMapper {
    void insertSearchRecord(SearchList searchList);
}
