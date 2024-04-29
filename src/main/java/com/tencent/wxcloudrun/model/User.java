package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class User implements Serializable {
    String openId;
    String name;
    String phone;
    String sex;
    Long id;
    /**
     * 余额，单词下载直接扣除
     */
    Integer balance;
    /**
     * 0 月卡
     * 1 年卡
     */
    Integer vipType;
    /**
     * vip截止日期
     */
    Date vipDeadline;
}
