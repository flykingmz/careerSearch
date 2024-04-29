package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.UserLoginRequest;
import com.tencent.wxcloudrun.dto.UserLoginVO;
import com.tencent.wxcloudrun.interceptor.BaseContext;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.service.CareerSearchService;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    final UserService userService;

    @Autowired
    final CareerSearchService careerSearchService;

    final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, CareerSearchService careerSearchService) {
        this.userService = userService;
        this.careerSearchService = careerSearchService;
    }

    @PostMapping(value="/careerSearch/user/login")
    ApiResponse login(@RequestBody UserLoginRequest userLoginRequest) throws Exception {
        logger.info("user login "+userLoginRequest);
        User user = userService.wxLogin(userLoginRequest);
        //为微信用户生成jwt令牌 传入（秘钥|过期时间|用户的唯一标识）
        Map<String , Object> claims = new HashMap<>();
        claims.put("USER_ID",user.getId());
        String token = JwtUtil.genToken(claims);
        UserLoginVO loginVO = new UserLoginVO();
        loginVO.setId(user.getId());
        loginVO.setOpenId(user.getOpenId());
        loginVO.setToken(token);
        return ApiResponse.ok(loginVO);
    }

@GetMapping(value="/careerSearch/user/downloads")
ApiResponse downloads(@RequestParam("docId") Long docId){
    Long userId = BaseContext.getCurrentId();
    if(userId != null){
        boolean canDownload = false;
        //TODO 先判断用户是否是VIP会员，会员是否到期
        //TODO 判断用户是否是单次购买，购买是否余额足够
        //前面条件都满足，则启动给出下载url
        if(!canDownload){
            return ApiResponse.error("您的余额不足，请充值再下载");
        }
        String downloadsUrl = careerSearchService.downloads(docId);
        return ApiResponse.ok(downloadsUrl);
    }
    return ApiResponse.error("请登录后再下载");

}
}
