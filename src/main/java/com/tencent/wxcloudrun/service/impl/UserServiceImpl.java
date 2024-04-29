package com.tencent.wxcloudrun.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.wxcloudrun.config.WeChatProperties;
import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.dto.UserLoginRequest;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    final UserMapper userMapper;

    final Logger logger = LoggerFactory.getLogger(CareerSearchServiceImpl.class);

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User wxLogin(UserLoginRequest userLoginRequest) throws Exception {
        //调用方法得到微信接口返回的OpenId
        JSONObject jSONObject = getLoginInfoFromWX(userLoginRequest.getCode());
        String openid = jSONObject.getString("openid");

        //判断返回的OpenId的合法性
        if(openid == null){
            //失败抛出业务异常
            throw new Exception("wx login fail");
        }
        //合法的微信用户，判断数据库中存在该openid吗？没有则是新用户，完成自动注册
        //根据openid到User表中查询数据
        User user = userMapper.selectByOpenId(openid);
        //判断user是不是新用户，即是不是为空
        if(user == null){
            //是新用户，自动注册，存储到user表中
            user = new User();
            user.setOpenId(openid);
            userMapper.insert(user);
        }
        //最后将user对象返回给Controller层，实现业务相应
        return user;
    }

    private JSONObject getLoginInfoFromWX(String code){
        //向微信接口发送请求，获取openId 和session_key
        //调用HttpClientUtil工具类的方法实现
        Map<String,String> claims = new HashMap<>();
        //参数传递
        claims.put("appid", WeChatProperties.APPID);
        claims.put("secret",WeChatProperties.APPSECCRET);
        claims.put("js_code",code);
        claims.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WeChatProperties.WX_LOGIN,claims);
        return JSON.parseObject(json);//是将Json字符串转化为相应的对象；
    }
}
