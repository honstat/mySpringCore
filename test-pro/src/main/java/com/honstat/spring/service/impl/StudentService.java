package com.honstat.spring.service.impl;

import com.honstat.spring.annotaiton.JCService;
import com.honstat.spring.service.IStudentService;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service.impl
 * @Description: TODO
 * @date 2019/8/13 16:42
 */
@JCService
public class StudentService  implements IStudentService{
    @Override
    public String sayHi(){
        return "Hello world!";
    }
}
