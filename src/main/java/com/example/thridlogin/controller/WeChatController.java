package com.example.thridlogin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.thridlogin.service.WeChatAuthService;
import com.example.thridlogin.util.CheckoutUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author fei.liang
 * @data 2018/11/5 17:46
 **/
@RestController
//@Controller
public class WeChatController {

    @Autowired
    WeChatAuthService weChatAuthService;

    /**
     * 自我验证 验证成功才能接入微信链接
     * @param request
     * @param response
     */
    @GetMapping(value = "/ownerCheck")
    public void ownerCheck(HttpServletRequest request,HttpServletResponse response){
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            try {
                PrintWriter print = response.getWriter();
                print.write(echostr);
                print.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping(value = "/wxLoginPage",method = RequestMethod.GET)
    public void wxLoginPage(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String uri = weChatAuthService.getAuthorizationUrl();
        System.out.println(uri);
        response.sendRedirect(uri);
    }

    @RequestMapping(value = "/auth/wechat")
    public void callback(String code,HttpServletRequest request,HttpServletResponse response) throws Exception {

        String result = weChatAuthService.getAccessToken(code);
        JSONObject jsonObject = JSONObject.parseObject(result);

        String accessToken = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openId");

        JSONObject userInfo=weChatAuthService.getUserInfo(accessToken, openId);
        System.out.println(userInfo);
        //    String refresh_token = jsonObject.getString("refresh_token");

        // 保存 access_token 到 cookie，两小时过期
//        Cookie accessTokencookie = new Cookie("accessToken", access_token);
//        accessTokencookie.setMaxAge(60 *2);
//        response.addCookie(accessTokencookie);
//
//        Cookie openIdCookie = new Cookie("openId", openId);
//        openIdCookie.setMaxAge(60 *2);
//        response.addCookie(openIdCookie);

        //根据openId判断用户是否已经登陆过
       // KmsUser user = userService.getUserByCondition(openId);

//        if (user == null) {
//            response.sendRedirect(request.getContextPath() + "/student/html/index.html.min.html#/bind?type="+Constants.LOGIN_TYPE_WECHAT);
//        } else {
//            //如果用户已存在，则直接登录
//            response.sendRedirect(request.getContextPath() + "/student/html/index.html.min.html#/app/home?open_id=" + openId);
//        }
        response.sendRedirect(request.getContextPath()+"/index.html");
    }

}
