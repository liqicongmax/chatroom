package com.lqc.chatroom.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author liqicong@myhexin.com
 * @date 2019/9/3 17:16
 */
@Slf4j
@Controller
public class IndexController {

    @RequestMapping("{username}")
    public ModelAndView index(@PathVariable String username){
        ModelAndView mav=new ModelAndView();
        mav.setViewName("index");
        mav.addObject("username",username);
        return mav;
    }

}
