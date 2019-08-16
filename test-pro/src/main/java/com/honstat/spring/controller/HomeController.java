package com.honstat.spring.controller;

import com.honstat.spring.annotaiton.JCAutoWrited;
import com.honstat.spring.annotaiton.JCController;
import com.honstat.spring.annotaiton.JCRequestMapping;
import com.honstat.spring.model.GetUserInfo;
import com.honstat.spring.service.IHomeService;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.controller
 * @Description: TODO
 * @date 2019/8/13 16:44
 */
@JCController
@JCRequestMapping("/home")
public class HomeController {
    @JCAutoWrited
    private IHomeService homeService;

    @JCRequestMapping("/sayHi")
    public String sayHi() {
        return homeService.sayHi();
    }

    @JCRequestMapping("/getName")
    public String getName(Integer id,String no) {
        return homeService.getName(id,no);
    }
    @JCRequestMapping("/getRequestBody")
    public String getRequestBody(Integer id, String no, GetUserInfo userInfo) {
        return homeService.getRequestBody(id,no,userInfo);
    }
}
