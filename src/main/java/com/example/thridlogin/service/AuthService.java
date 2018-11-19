package com.example.thridlogin.service;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * @author fei.liang
 * @date 2018/11/13 14:29
 **/
public interface AuthService {
    String getAuthorizationUrl() throws UnsupportedEncodingException;
    String getAccessToken(String code);
    String getOpenId(String accessToken);
    JSONObject getUserInfo(String accessToken, String openId);
}
