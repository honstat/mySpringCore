package com.honstat.spring.annotaiton;

import java.lang.annotation.*;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: test-pro
 * @Package com.honstat.spring.annotaiton
 * @Description: TODO
 * @date 2019/8/13 11:34
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JCService {
    String value() default "";
}
