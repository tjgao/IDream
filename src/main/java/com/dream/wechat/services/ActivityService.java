package com.dream.wechat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dream.wechat.mapper.ActivityMapper;
import com.dream.wechat.mapper.UserImgMapper;
import com.dream.wechat.model.Activity;
import com.dream.wechat.model.Thumb;

@Service
public class ActivityService {
	@Autowired
	private ActivityMapper aMapper;
	
	@Autowired
	private UserImgMapper imMapper;
	
	public Activity getActivity(int id) {
		return aMapper.getActivity(id);
	}
	
	public List<Thumb> getLatestThumbs(int id) {
		return aMapper.getLatestThumbs(id);
	}
	
	public List<Thumb> getHottestThumbs(int id) {
		return aMapper.getHottestThumbs(id);
	}
	
	public List<Activity> getActivities() {
		List<Activity> l = aMapper.getActivities();
		for( Activity a : l ) {
			List<Thumb> lt = imMapper.getThumbsByActivityIdPage(a.getId(), 0,3);
			a.setThumbs(lt);
		}
		return l;
	}

	public List<Activity> getActivities(int start, int limit) {
		List<Activity> l = aMapper.getActivitiesByPage(start, limit);
		for( Activity a : l ) {
			List<Thumb> lt = imMapper.getThumbsByActivityIdPage(a.getId(), 0, 3);
			a.setThumbs(lt);
		}
		return l;
	}
}
