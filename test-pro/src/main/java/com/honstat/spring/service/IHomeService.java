package com.honstat.spring.service;

import com.honstat.spring.model.GetUserInfo;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.service
 * @Description: TODO
 * @date 2019/8/13 16:41
 */
public interface IHomeService {
    String sayHi();
    String getName(Integer id,String no);
    String getRequestBody(Integer id, String no, GetUserInfo userInfo);
}
