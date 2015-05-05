package com.dream.wechat.model;

import java.util.Date;

public class ImgComment {
	private int id;
	private int imgId;
	private int userId;
	private String userName;
	private int userSex;
	private String userHead;
	private Date createTime;
	private String content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getImgId() {
		return imgId;
	}
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String conent) {
		this.content = conent;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserSex() {
		return userSex;
	}
	public void setUserSex(int userSex) {
		this.userSex = userSex;
	}
	public String getUserHead() {
		return userHead;
	}
	public void setUserHead(String userHead) {
		this.userHead = userHead;
	}
}
