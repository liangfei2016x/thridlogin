package com.example.thridlogin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.thridlogin.service.AuthService;
import com.example.thridlogin.service.WeChatAuthService;
import com.example.thridlogin.util.HttpTookit;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author fei.liang
 * @data 2018/11/6 11:20
 **/
@Service
public class WeChatAuthServiceImpl implements WeChatAuthService {

    //请求此地址即跳转到二维码登录界面
    private static final String AUTHORIZATION_URL =
            //"https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
            "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
    // 获取用户 openid 和access——toke 的 URL
    private static final String ACCESSTOKE_OPENID_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    private static final String REFRESH_TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    private static final String USER_INFO_URL =
            "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    private static final String APP_ID="xxxxxxxxxx";
    private static final String APP_SECRET="xxxxxxxxxxxx";
    private static final String SCOPE = "snsapi_userinfo";

    private String callbackUrl = "https://9dbd3252.ngrok.io/auth/wechat"; //回调域名

    @Override
    public String getAuthorizationUrl() throws UnsupportedEncodingException {
        callbackUrl = URLEncoder.encode(callbackUrl,"utf-8");
        String url = String.format(AUTHORIZATION_URL,APP_ID,callbackUrl,SCOPE,System.currentTimeMillis());
        return url;
    }


    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESSTOKE_OPENID_URL,APP_ID,APP_SECRET,code);
        String resp=HttpTookit.doGet(url);
        if(resp.contains("openid")){
            JSONObject jsonObject = JSONObject.parseObject(resp);
            String access_token = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");;

            JSONObject res = new JSONObject();
            res.put("access_token",access_token);
            res.put("openId",openId);
            res.put("refresh_token",jsonObject.getString("refresh_token"));

            return res.toJSONString();
        }else{
           return "getAccessToken error";
        }
    }

    /**
     * 微信不用此方法 微信的openId和token一块返回
     * @param accessToken
     * @return
     */
    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public JSONObject getUserInfo(String accessToken, String openId){
        String url = String.format(USER_INFO_URL, accessToken, openId);
        String resp=HttpTookit.doGet(url);
        if(resp.contains("errcode")){
            return JSON.parseObject( "getUserInfo error");
        }else{
            JSONObject data =JSONObject.parseObject(resp);
            System.out.println(data);
            JSONObject result = new JSONObject();
            result.put("id",data.getString("unionid"));
            result.put("nickName",data.getString("nickname"));
            result.put("avatar",data.getString("headimgurl"));
            return result;
        }
    }

}
