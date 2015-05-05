package com.dream.wechat.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.wechat.AppConfig;
import com.dream.wechat.auth.AuthorityCheck;
import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;
import com.dream.wechat.services.ActivityService;
import com.dream.wechat.services.UserService;

@Controller
public class ActivitiesController {
	@Autowired
	UserService uService;
	@Autowired
	ActivityService aService;
	private static final Logger logger = LoggerFactory.getLogger(DockInController.class);
	
	@RequestMapping(value = "/api/activity", method=RequestMethod.GET)
	public @ResponseBody List<Activity> getActivities() {
		return aService.getActivities();
	}
	
	@RequestMapping(value="/api/activity/{id}", method=RequestMethod.GET)
	public @ResponseBody Activity getActivity(@PathVariable("id") int id) {
		return aService.getActivity(id);
	}
	
	@RequestMapping(value="/api/activity/{id}/thumb/latest", method=RequestMethod.GET)
	public @ResponseBody List<Thumb> getLatestThumbs(@PathVariable("id") int id) {
		return aService.getLatestThumbs(id);
	}

	@RequestMapping(value="/api/activity/{id}/thumb/hottest", method=RequestMethod.GET)
	public @ResponseBody List<Thumb> getHottestThumbs(@PathVariable("id") int id) {
		return aService.getHottestThumbs(id);
	}
	
	@RequestMapping(value="/api/activity/page/{pg}", method=RequestMethod.GET) 
	public @ResponseBody List<Activity> getActivities(@PathVariable("pg") int page) {
		AppConfig cfg = AppConfig.getConfig();
		int size = Integer.parseInt(cfg.get(AppConfig.ACTIVITYPAGESIZE));
		int start = (page -1)*size; 
		return aService.getActivities(start, size);
	}
}
