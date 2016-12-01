package com.efeiyi.website.util;

import com.efeiyi.website.entity.User;
import com.efeiyi.website.service.inter.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/8/17 0017.
 */
public class RoleInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    IUserService userService;

    //请求成功后拦截
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object o, ModelAndView mav)
            throws Exception {
    }

    //请求之前拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = Util.getRelativePath(request);
        String sessionId = request.getRequestedSessionId();
        User user = null;
        if (sessionId != null) {
            user = userService.currentUser(sessionId); //获取当前用户
        }


        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");

        boolean hasAuthority;  //是否有权限
        try {
            hasAuthority = userService.hasAuthority(uri, user);  //判断用户是否有权限
        } catch (Exception e) {
            response.getWriter().write(new ApplicationException(ApplicationException.INNER_ERROR).toJsonString());    //内部错误
            return false;
        }
        if (!hasAuthority) {
            response.getWriter().write(new ApplicationException(ApplicationException.INNER_ERROR).toJsonString());  //没有权限
            return false;
        }

        return true;
    }

}
