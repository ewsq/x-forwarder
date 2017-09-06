# OAuth2开发辅助
[![Build Status](https://travis-ci.org/InCar/x-forwarder.svg?branch=master)](https://travis-ci.org/InCar/x-forwarder)

出于安全的原因,大部分的OAuth2限定可以跳转的目标域名.但这样给开发工作带来了困难,通常只能在限定的服务器上进行调试工作.

本辅助工具的目的在于把调试工作从服务器代理到内网开发人员自己的开发机器.

### 通过以下办法来达成这一目标：
1. 开放一个HTML页面用于接收来自OAuth2的跳转,接收AuthorizationCode和一个特别的state参数
2. 当收到来自OAuth2的回调后,HTML中的javascript代码把调用转发到state参数指定的内网URL上,并包含AuthorizationCode
3. 开发者在自己的机器上把AuthorizationCode发往后台服务
4. 后台服务用AuthorizationCode合并自己的AppSecret向QQ服务器查询AccessToken以及UserInfo

### 具体步骤
1. 在前端页面上放置一个A标签链接,指向QQ的登录授权引导页面

```HTML
<a href="https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101419871&redirect_uri=http%3a%2f%2fapi.incarcloud.com%2f3rd%2fqq&state=%7b&quot;state&quot;%3a&quot;once114257&quot;,&quot;url&quot;%3a&quot;http%3a//127.0.0.1:8080/3rd/qq&quot;%7d">
    <img src="http://qzonestyle.gtimg.cn/qzone/vas/opensns/res/img/Connect_logo_5.png">
</a>
```
参数填写请参考QQ的[]官方指导](http://wiki.connect.qq.com/%E5%BC%80%E5%8F%91%E6%94%BB%E7%95%A5_server-side)

这里特别的地方在于`state`参数,需要是一个JSON,其它属性随意,但必须包含一个`url`,
这个url可以是本机127或者是内网地址,一小节JS代码会在浏览器里执行一个跳转
```JSON
{
    "state": "once114257",
    "url": "http://127.0.0.1:8080/3rd/qq"
}
```

2. 在自己的开发机器上部署一个页面,它的URL恰好就是上面第1步里的url

用户在QQ的登录授权引导页面授权后,这个页面会被浏览器访问,查询参数里包含一个`code`参数，即是`AuthorizationCode`

3. 向服务器查询用户信息

HTTP POST http://api.incarcloud.com/user/qq

POST消息体是一个JSON,内部包含一个code属性,其值为`AuthorizationCode`的值

返回的结果中包含了用户的open_id和nickname等信息

```javascript
var code = (new URL(window.location)).searchParams.get("code");
$.ajax({
    method: "POST",
    url: "http://api.incarcloud.com/user/qq",
    data: JSON.stringify({code: code}),
    contentType: "application/json",
    dataType: "json"
}).then(function(user){
    console.info(user);
}, function(ex){
    console.error(ex.responseJSON);
});
```

### 示例参考
 [http://api.incarcloud.com](http://api.incarcloud.com)