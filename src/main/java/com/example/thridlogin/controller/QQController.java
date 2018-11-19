package com.example.thridlogin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.thridlogin.service.QQAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fei.liang
 * @date 2018/11/13 9:05
 **/
@RequestMapping("/qq")
@Controller
public class QQController {

    @Autowired
    private QQAuthService qqAuthService;

    @GetMapping("/qqLogin")
    public String login(){
        return "login";
    }

    //访问登陆页面，然后会跳转到 QQ 的登陆页面
    @GetMapping(value = "/qqLoginPage")
    public void qqLogin(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String uri = qqAuthService.getAuthorizationUrl();
        System.out.println(uri);
        response.sendRedirect(uri);
    }

    //qq授权后会回调此方法，并将code传过来
    @RequestMapping("/callback")
    public void getQQCode(String code, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //根据code获取token
        String accessToken = qqAuthService.getAccessToken(code);
        // 保存 accessToken 到 cookie，过期时间为 30 天，便于以后使用
        // Cookie cookie = new Cookie("accessToken", accessToken);
        // cookie.setMaxAge(60 * 24 * 30);
        // response.addCookie(cookie);
        //本网站是将用户的唯一标识存在用户表中，大家也可以加一张表，存储用户和QQ的对应信息。
        //根据openId判断用户是否已经绑定过
        String openId = qqAuthService.getOpenId(accessToken);

        JSONObject userInfo = qqAuthService.getUserInfo(accessToken, openId);

        System.out.println(userInfo);

        response.sendRedirect(request.getContextPath()+"/index.html");

    }

}
