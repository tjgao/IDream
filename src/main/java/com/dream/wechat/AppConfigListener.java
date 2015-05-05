package com.dream.wechat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dream.wechat.AppConfig;

public class AppConfigListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		ServletContext sc = arg0.getServletContext();
		AppConfig cfg = AppConfig.getConfig();
	    cfg.put(AppConfig.SERVERNAME, sc.getInitParameter(AppConfig.SERVERNAME));	
		String s = null;
		try{
			s = sc.getInitParameter(AppConfig.ACTIVITYPAGESIZE);
			int l = Integer.parseInt(s);
			cfg.put(AppConfig.ACTIVITYPAGESIZE, s);
		} catch(Exception e) {
			cfg.put(AppConfig.ACTIVITYPAGESIZE,"12");
		}
		try{
			s = sc.getInitParameter(AppConfig.IMAGEPAGESIZE);
			int l = Integer.parseInt(s);
			cfg.put(AppConfig.IMAGEPAGESIZE, s);
		} catch(Exception e) {
			cfg.put(AppConfig.IMAGEPAGESIZE,"12");
		}
		try{
			s = sc.getInitParameter(AppConfig.THUMBNAIL);
			int l = Integer.parseInt(s);
			cfg.put(AppConfig.THUMBNAIL, s);
		} catch (Exception e) {
			cfg.put(AppConfig.THUMBNAIL, "200");
		}
		try{
			s = sc.getInitParameter(AppConfig.USERUPDATETIME);
			int l = Integer.parseInt(s);
			cfg.put(AppConfig.USERUPDATETIME, s);
		} catch(Exception e) {
			//by default, 240 hrs, 10 days
			cfg.put(AppConfig.USERUPDATETIME,"240");
		}
	}
}
