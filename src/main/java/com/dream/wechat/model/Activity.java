package com.dream.wechat.model;

import java.util.List;

public class Activity {
	private int id;
	private String name;
	private String description;
	private String logo;
	private List<Thumb> thumbs;
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
	public void setDescription(String descripiton) {
		this.description = descripiton;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public List<Thumb> getThumbs() {
		return thumbs;
	}
	public void setThumbs(List<Thumb> thumbs) {
		this.thumbs = thumbs;
	}

}
