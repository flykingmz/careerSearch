package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class HotList implements Serializable {
    private Integer id;
    private String title;
    private String brief;
    private Integer times;
    private Integer likes;
    private String typesId;
}
