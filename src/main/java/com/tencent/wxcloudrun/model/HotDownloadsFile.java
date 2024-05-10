package com.tencent.wxcloudrun.model;
import java.io.Serializable;

import lombok.Data;

@Data
public class HotDownloadsFile implements Serializable{
    private Integer id;
    private String title;
    private String brief;
    private String downloadUrl;
    private float cost;
    private String imageUrl;
    private Integer downloads;
    private int fileType;
}
