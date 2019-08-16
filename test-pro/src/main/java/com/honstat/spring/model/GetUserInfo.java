package com.honstat.spring.model;

import java.math.BigDecimal;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.model
 * @Description: TODO
 * @date 2019/8/15 10:44
 */
public class GetUserInfo {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(BigDecimal growthValue) {
        this.growthValue = growthValue;
    }

    private String name;
    private Integer age;
    private BigDecimal growthValue;

}
