package com.efeiyi.website.util;

import com.efeiyi.website.service.Session;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wangtao on 2016/7/14 0014.
 * 通用拦截器
 */
public class Interceptor extends HandlerInterceptorAdapter {

    //请求成功后拦截
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object o, ModelAndView mav)
            throws Exception {
    }

    //请求之前拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Session session = new Session(request);

        return true;
    }

}
