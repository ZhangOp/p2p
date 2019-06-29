package com.bjpowernode.p2p.interceptor;

import com.bjpowernode.p2p.constants.Constants;
import com.bjpowernode.p2p.model.user.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.USER);

        //判断用户是否登录
        if (null == sessionUser) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
