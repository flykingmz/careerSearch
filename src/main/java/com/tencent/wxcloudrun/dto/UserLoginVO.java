package com.tencent.wxcloudrun.dto;

import lombok.Data;

@Data
public class UserLoginVO {
    Long id;
    String openId;
    String token;
}
