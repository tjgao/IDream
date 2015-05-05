package com.dream.wechat;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dream.wechat.auth.AuthorityCheck;

public class SessionInterceptor implements HandlerInterceptor {
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		HandlerMethod handler = (HandlerMethod)arg2;
		AuthorityCheck a = handler.getMethodAnnotation(AuthorityCheck.class);
		if( a == null ) return true;
		if( arg0.getSession().getAttribute("OAUTH") != null )
			return true;
		return false;
	}
}
