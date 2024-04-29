package com.tencent.wxcloudrun.model;
import java.io.Serializable;

import lombok.Data;

@Data
public class RelatedFile implements Serializable{
    private Integer id;
    private String title;
    private String url;
    private Integer downloads;
}
