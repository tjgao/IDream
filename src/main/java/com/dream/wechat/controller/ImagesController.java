package com.dream.wechat.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.wechat.AppConfig;
import com.dream.wechat.CommonUtils;
import com.dream.wechat.auth.AuthUtils;
import com.dream.wechat.model.ImgComment;
import com.dream.wechat.model.ImgUpload;
import com.dream.wechat.model.UserImg;
import com.dream.wechat.model.UserLite;
import com.dream.wechat.services.ImageService;

@Controller
public class ImagesController {
	@Autowired
	ImageService iService;
	
	@Autowired
	ServletContext servletCtx;
	
	private static final Logger logger = LoggerFactory.getLogger(ImagesController.class);

	@RequestMapping(value = "/api/image/{id}", method = RequestMethod.GET)
	public @ResponseBody UserImg getImage(@PathVariable("id") int id) {
		return iService.getImage(id);
	}
	
	@RequestMapping(value="/api/image/{id}/comment", method=RequestMethod.GET)
	public @ResponseBody List<ImgComment> getComment(@PathVariable("id") int id) {
		return iService.getComments(id);
	}

	@RequestMapping(value = "/api/image/comment", method=RequestMethod.POST) 
	public @ResponseBody HashMap<String, Object> comment(HttpSession session, @RequestBody ImgComment c) {
		HashMap<String,Object> h = new HashMap<String,Object>();
		UserLite o = (UserLite)session.getAttribute("USER");
		logger.debug("imgId:{},  content:{}", c.getImgId(), c.getContent());
		logger.debug("userId:{}, o.userId:{}", c.getUserId(), o.getId());
		if( o == null || o.getId() != c.getUserId()) {
			h.put("msg", "NO PERMISSION");
			h.put("retcode", -2);
			return h;
		}
		h.put("msg", "SUCCESS");
		try{
			iService.addComment(c);
			h.put("retcode", 0);
		} catch (Exception e) {
			h.put("msg", "FAILURE");
			h.put("retcode", -1);
		}
		return h;
	}
	
	@RequestMapping(value="/api/image/{id}/like", method=RequestMethod.POST)
	public @ResponseBody HashMap<String, Object> like(HttpSession session, @PathVariable("id") int id) {
		HashMap<String,Object> h = new HashMap<String,Object>();
		UserLite o = (UserLite)session.getAttribute("USER");
		if( o == null) {
			h.put("msg", "NO PERMISSION");
			h.put("retcode", -2);
			return h;
		}
		try{
			iService.like(id, o.getId());
			h.put("retcode", 0);
			h.put("msg", "SUCCESS");
		} catch(Exception e) {
//			e.printStackTrace();
			h.put("msg", "FAILURE");
			h.put("retcode", -1);			
		}
		return h;
	}
	
	@RequestMapping(value="/api/image/upload/{id}", method=RequestMethod.POST)
	public @ResponseBody HashMap<String,Object> upload(HttpSession session, @PathVariable("id") String id, @RequestBody ImgUpload iu) {
		HashMap<String, Object> h = new HashMap<String, Object>();
		UserLite o = (UserLite)session.getAttribute("USER");
		if( o == null) {
			h.put("msg", "NO PERMISSION");
			h.put("retcode", -2);
			return h;
		}
		String fileName = o.getId() + "_" + CommonUtils.md5(o.getNickname() + System.currentTimeMillis() + session.getId()) + ".jpg";
		String thumb = "t_" + fileName;
		String path = servletCtx.getRealPath("/") + AppConfig.UPLOADDIR + File.separator ;
		String thumbpath = servletCtx.getRealPath("/") + AppConfig.THUMBDIR + File.separator ; 
		if( AuthUtils.downloadImage(id, path + fileName) ) {
			AppConfig cfg = AppConfig.getConfig();
			int size = Integer.parseInt(cfg.get(AppConfig.THUMBNAIL));
			try{
				CommonUtils.thumbnail(path+fileName, thumbpath+thumb, size, size);
				UserImg img = new UserImg();
				img.setaId(iu.getaId());
				img.setDescription(iu.getUploadImgDesc());
				img.setName(iu.getUploadImgName());
				img.setAuthorId(o.getId());
				img.setFile(AppConfig.UPLOADDIR + File.separator + fileName);
				img.setThumb(AppConfig.THUMBDIR + File.separator + thumb);
				iService.insertImg(img);
				h.put("msg", "SUCCESS");
				h.put("retcode", 0);
				return h;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		h.put("msg", "FAILURE");
		h.put("retcode", -1);				
		return h;
	}
}
