package com.spider.unidbgserver.controller;

import com.crack.Ate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/unidbg")
public class AteController {
    @Autowired(required = false)
    Ate instance;

    @RequestMapping(value = "ate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dySign(String sum) {
        synchronized (this) {
            return instance.sha(sum);
        }
    }
}