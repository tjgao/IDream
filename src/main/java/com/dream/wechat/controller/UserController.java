package com.dream.wechat.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.wechat.AppConfig;
import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;
import com.dream.wechat.model.UserImg;
import com.dream.wechat.model.UserLite;
import com.dream.wechat.model.UserStatus;
import com.dream.wechat.services.UserService;

@Controller
public class UserController {
	@Autowired
	UserService uService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@RequestMapping(value="/api/user/{id}", method=RequestMethod.GET)
	public @ResponseBody UserLite getUser(@PathVariable("id") int id) {
		logger.debug("User {} is showing himself", id);
		return uService.getUserLite(id);
	}
	
	@RequestMapping(value="/api/user/{id}/activity", method=RequestMethod.GET)
	public @ResponseBody List<Activity> getActivity(@PathVariable("id") int id) {
		return uService.getActivities(id);
	}
	
	@RequestMapping(value="/api/user/{id}/status", method=RequestMethod.GET)
	public @ResponseBody UserStatus getUserStatus(@PathVariable("id") int id) {
		return uService.getUserStatus(id);
	}
	
	@RequestMapping(value="/api/user/{id}/following", method=RequestMethod.GET)
	public @ResponseBody List<UserLite> getFollowing(@PathVariable("id") int id) {
		return uService.getFollowing(id);
	}
	
	@RequestMapping(value="/api/user/{id}/following/image", method=RequestMethod.GET) 
	public @ResponseBody List<UserImg> getUserFollowingImg(@PathVariable("id") int id) {
		return uService.getUserFollowingImg(id);
	}
	
	@RequestMapping(value="/api/user/{id}/fan", method=RequestMethod.GET) 
	public @ResponseBody List<UserLite> getFans(@PathVariable("id") int id) {
		return uService.getFans(id);
	}
	
	@RequestMapping(value="/api/user/{id}/thumb", method=RequestMethod.GET)
	public @ResponseBody List<Thumb> getThumbs(@PathVariable("id") int id) {
		return uService.getThumbs(id);
	}
	
	@RequestMapping(value="/api/follow/{followed}", method=RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> follow(HttpSession session, @PathVariable("followed") int followed) {
		HashMap<String, Object> h = new HashMap<String, Object>();
		UserLite o = (UserLite)session.getAttribute("USER");
		if( o == null ) {
			h.put("msg", "NO PERMISSION");
			h.put("retcode", -2);
			return h;
		}
		try{
			if( o.getId() == followed) {
				h.put("retcode", -3);
				h.put("msg", "CANNOT FOLLOW YOUSELF");
			}
			uService.follow(o.getId(), followed);
			h.put("retcode", 0);
			h.put("msg", "SUCCESS");
			return h;
		}catch(Exception e) {
			e.printStackTrace();
			h.put("msg", "FAILURE");
			h.put("retcode", -1);
		}
		return h;
	}
	
	@RequestMapping(value="/api/unfollow/{followed}", method=RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> unfollow(HttpSession session, @PathVariable("followed") int followed) {
		HashMap<String, Object> h = new HashMap<String, Object>();
		UserLite o = (UserLite)session.getAttribute("USER");
		if( o == null) {
			h.put("msg", "NO PERMISSION");
			h.put("retcode", -2);
			return h;
		}
		System.out.println(o.getId() + " unfollow + " + followed);
		if( o.getId() == followed ) {
			h.put("msg", "CANNOT UNFOLLOW YOURSELF");
			h.put("retcode", -3);
			return h;
		}
		try{
			int x = uService.unfollow(o.getId(), followed);
			System.out.println(o.getId()+" unfollow "+ followed + " : "+ x);
			h.put("retcode", 0);
			h.put("msg", "SUCCESS");
		}catch(Exception e) {
			e.printStackTrace();
			h.put("retcode", -1);
			h.put("msg", "FAILURE");
		}
		return h;
	}
	
	@RequestMapping(value="/api/{me}/follow/{her}", method=RequestMethod.GET)
	public @ResponseBody String isFollowing(@PathVariable("me") int me, @PathVariable("her") int her) {
		if( uService.followed(me, her)) return "{\"result\":1}";
		else return "{\"result\":0}";
	}

}
