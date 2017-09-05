# OAuth2开发辅助
[![Build Status](https://travis-ci.org/InCar/x-forwarder.svg?branch=master)](https://travis-ci.org/InCar/x-forwarder)

出于安全的原因,大部分的OAuth2限定可以跳转的目标域名.但这样给开发工作带来了困难,通常只能在限定的服务器上进行调试工作.

本辅助工具的目的在于把调试工作从服务器代理到内网开发人员自己的开发机器.

### 通过以下办法来达成这一目标：
1. 开放一个HTML页面用于接收来自OAuth2的跳转,接收AuthorizationCode和一个特别的state参数
2. 当收到来自OAuth2的回调后,HTML中的javascript代码把调用转发到state参数指定的内网URL上,并包含AuthorizationCode
3. 开发者在自己的机器上处理AuthorizationCode