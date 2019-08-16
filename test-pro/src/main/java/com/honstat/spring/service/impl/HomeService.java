package com.honstat.spring.service.impl;

import com.honstat.spring.annotaiton.JCAutoWrited;
import com.honstat.spring.annotaiton.JCService;
import com.honstat.spring.model.GetUserInfo;
import com.honstat.spring.service.IHomeService;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service
 * @Description: TODO
 * @date 2019/8/13 16:40
 */
@JCService
public class HomeService  implements IHomeService{

    @JCAutoWrited
     StudentService studentService;
    @Override
    public String sayHi() {
      return   studentService.sayHi();
    }

    @Override
    public String getName(Integer id,String no) {
        return "SB0000"+id;
    }

    @Override
    public String getRequestBody(Integer id, String no, GetUserInfo userInfo) {
        return "userName="+userInfo.getName()+" no="+no;
    }
}
