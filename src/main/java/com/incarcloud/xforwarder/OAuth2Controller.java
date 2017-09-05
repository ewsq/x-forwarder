package com.incarcloud.xforwarder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OAuth2Controller {

    @RequestMapping("/3rd/qq")
    public String forwardQQ()
    {
        return "forward:/oauth2.html";
    }
}
