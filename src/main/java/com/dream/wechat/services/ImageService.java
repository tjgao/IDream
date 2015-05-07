package com.dream.wechat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dream.wechat.mapper.UserImgMapper;
import com.dream.wechat.model.ImgComment;
import com.dream.wechat.model.UserImg;

@Service
public class ImageService {
	@Autowired
	UserImgMapper uiMapper;
	public UserImg getImage(int id) {
		return uiMapper.getImage(id); 
	}
	
	public List<ImgComment> getComments(int id) {
		return uiMapper.getImgComments(id);
	}
	
	@Transactional
	public int addComment(ImgComment i) {
		uiMapper.plusComments(i.getImgId());
		return uiMapper.putComment(i);
	}
	
	@Transactional
	public void like(int imgId, int uId) {
		uiMapper.putLikes(imgId, uId);
		uiMapper.plusLikes(imgId);
	}
	
	public List<UserImg> getHottestTopN(int n) {
		return uiMapper.getHottestTopN(n);
	}
	
	public void insertImg(UserImg u) {
		uiMapper.insertUserImg(u);
	}
}
