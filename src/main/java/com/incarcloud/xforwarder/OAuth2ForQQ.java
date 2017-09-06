package com.incarcloud.xforwarder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class OAuth2ForQQ {
    private String _urlToken = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";
    private String _urlOpenId = "https://graph.qq.com/oauth2.0/me?access_token=%s";
    private String _urlUserInfo = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    @Value("${qq.appId}")
    private String appId;

    @Value("${qq.appSecret}")
    private String appSecret;

    @Value("${qq.appCallback}")
    private String appCallback;

    public String getAccessTokenByAuthCode(String code){
        String url = String.format(this._urlToken, this.appId, this.appSecret, code, this.appCallback);
        RestTemplate tokenRequest = new RestTemplate();
        String response  = tokenRequest.getForObject(url, String.class);

        // access_token=2BC81F9C6DB6030897F84DBDDA244E4C&expires_in=7776000&refresh_token=5F98892EB399BA4F276E3DCB2A894C13
        // callback( {"error":100019,"error_description":"code to access token error"} );

        if(!response.startsWith("access_token=")){
            // failed
            throw new RuntimeException(response);
        }

        HashMap<String ,String> mapToken = new HashMap<>();
        for(String pair : response.split("&")){
            String[] kv = pair.split("=");
            if(kv.length == 2){
                mapToken.put(kv[0], kv[1]);
            }
        }

        return mapToken.get("access_token");
    }

    public String getOpenIdByAccessToken(String token){
        String url = String.format(this._urlOpenId, token);
        RestTemplate tokenRequest = new RestTemplate();
        String response  = tokenRequest.getForObject(url, String.class);

        // callback( {"client_id":"101419871","openid":"FC190FD4232665BF6C0BAB4EF3EC4A0F"} );

        Pattern rgxOpenId = Pattern.compile("\"openid\":\"(\\w+)\"");
        Matcher matcher = rgxOpenId.matcher(response);
        if(!matcher.find()) return response;

        return matcher.group(1);
    }

    public String getUserInfo(String token, String openId){
        String url = String.format(this._urlUserInfo, token, this.appId, openId);
        RestTemplate tokenRequest = new RestTemplate();
        String response  = tokenRequest.getForObject(url, String.class);
        return response;
    }
}
