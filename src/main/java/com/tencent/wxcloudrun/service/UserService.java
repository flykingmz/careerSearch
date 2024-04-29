package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.dto.UserLoginRequest;
import com.tencent.wxcloudrun.model.User;

public interface UserService {
    User wxLogin(UserLoginRequest userLoginRequest) throws Exception;
}
