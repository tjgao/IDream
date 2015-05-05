package com.dream.wechat.model;

import java.util.Date;

public class UserImg {
	private int id;
	private String name;
	private String description;
	private String file;
	private int authorId;
	private String authorName;
	private int authorSex;
	private String authorHead;
	private Date uploadTime;
	private int likes;
	private int shared;
	private int comments;
	private int aId;
	private String thumb;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
	public int getShared() {
		return shared;
	}
	public void setShared(int shared) {
		this.shared = shared;
	}
	public int getaId() {
		return aId;
	}
	public void setaId(int aId) {
		this.aId = aId;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public int getAuthorSex() {
		return authorSex;
	}
	public void setAuthorSex(int authorSex) {
		this.authorSex = authorSex;
	}

	public String getAuthorHead() {
		return authorHead;
	}
	public void setAuthorHead(String authoHead) {
		this.authorHead = authoHead;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
}
