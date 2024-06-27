package com.polaris.subject.common.util;


import com.polaris.subject.common.context.LoginContextHolder;

/**
 * 用户登录util
 *
 * @author Polaris
 * @date 2023/11/26
 */
public class LoginUtil {

    public static String getLoginId() {
        return LoginContextHolder.getLoginId();
    }


}
