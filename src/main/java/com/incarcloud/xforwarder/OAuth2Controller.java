package com.incarcloud.xforwarder;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class OAuth2Controller {
    private static Logger s_logger = LoggerFactory.getLogger(OAuth2Controller.class);

    @Autowired
    private ApplicationContext s_appCtx;

    @RequestMapping("/3rd/qq")
    public String forwardQQ(){
        return "/oauth2.html";
    }

    @CrossOrigin()
    @RequestMapping("/user/qq")
    @ResponseBody
    public String getQQUserInfo(@RequestBody AuthReq req){
        OAuth2ForQQ qq = s_appCtx.getBean(OAuth2ForQQ.class);
        String token = qq.getAccessTokenByAuthCode(req.code);
        String openId = qq.getOpenIdByAccessToken(token);
        String info = qq.getUserInfo(token, openId);

        // 把JSON中拼接上openId
        return info.replaceFirst("\\{", String.format("{\n    \"open_id\": \"%s\",", openId));
    }
}

class AuthReq{
    public String code;
}