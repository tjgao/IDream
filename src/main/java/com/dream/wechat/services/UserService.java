package com.dream.wechat.services;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dream.wechat.AppConfig;
import com.dream.wechat.mapper.UserImgMapper;
import com.dream.wechat.mapper.UserMapper;
import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;
import com.dream.wechat.model.User;
import com.dream.wechat.model.UserImg;
import com.dream.wechat.model.UserLite;
import com.dream.wechat.model.UserStatus;

@Service
public class UserService  {
	@Autowired
	UserMapper mapper;
	@Autowired
	UserImgMapper uiMapper;
	
	public void insertUser(User u) {
		mapper.insertUser(u);
	}
	
	public int updateOrInsert(User u) {
		Integer i = null;
		try{
			if( u.getSubscribe() == 0 && u.getSubscribe_time() == 0 )
				mapper.updateUserPartByOpenid(u);
			else
				mapper.updateUserByOpenid(u);
			return mapper.getUserByOpenid(u.getOpenId()).getId();
		} catch (Exception e) {
			e.printStackTrace();
			i = mapper.insertUser(u);
		}
		return -1;
	}
	
	public void updateUserByOpenid(User u) {
		mapper.updateUserByOpenid(u);
	}
	
	public User getUser(int id) {
		return mapper.getUser(id); 
	}
	
//	public void deleteUser(int id) {
//		mapper.deleteUser(id);
//	}
	
	public User getUserByOpenid(String openid) {
		return mapper.getUserByOpenid(openid);
	}
	
	public void unsubscribe(String openid) {
		mapper.unsubscribe(openid);
	}
	
	public UserLite getUserLite(int id) {
		UserLite u = mapper.getUserLite(id);
		return u;
	}
	
	public List<Activity> getActivities(int id) {
		List<Activity> l = mapper.getActivitiesByUser(id);
		for( Activity a : l) {
			a.setThumbs(uiMapper.getThumbsByActivityIdPage(a.getId(), 0, 1));
		}
		return l;
	}
	
	public UserStatus getUserStatus(int id) {
		UserStatus us = new UserStatus();
		us.setTotalActivities(mapper.getTotalActivities(id));
		us.setTotalFans(mapper.getTotalFans(id));
		us.setTotalLikes(mapper.getTotalLikes(id));
		us.setTotalFollowing(mapper.getTotalFollow(id));
		us.setTotalImages(mapper.getTotalImages(id));
		return us;
	}
	
	public List<UserLite> getFollowing(int id) {
		return mapper.getFollowing(id); 
	}
	
	public List<UserLite> getFans(int id) {
		return mapper.getFans(id);
	}
	
	public List<Thumb> getThumbs(int id) {
		return mapper.getThumbsByUser(id);
	}
	
	public List<UserImg> getUserFollowingImg(int id) {
		return mapper.getUserFollowingImg(id);
	}
	
	public int follow(int id, int followed) {
		return mapper.follow(id, followed);
	}
	
	public int unfollow(int id, int followed) {
		return mapper.unfollow(id, followed);
	}
	
	public boolean followed(int me, int her) {
		return !(mapper.followed(me, her) == 0);
	}
}
