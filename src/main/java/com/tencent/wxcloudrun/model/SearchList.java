package com.tencent.wxcloudrun.model;
import java.io.Serializable;

import lombok.Data;

@Data
public class SearchList implements Serializable{
    private String title;
    private Integer typesId;
}
