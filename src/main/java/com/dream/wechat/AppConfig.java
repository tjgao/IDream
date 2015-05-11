package com.dream.wechat;

import java.util.HashMap;

public class AppConfig extends HashMap<String, String> {
	public static final String THUMBDIR = "resources/images/thumb";
	public static final String UPLOADDIR = "resources/images/upload";
	public static final String HEADDIR = "resources/images/headimg";
	
	public static final String THUMBNAIL="thumbnail-max";
	public static final String ACTIVITYPAGESIZE="ActivityPageSize";
	public static final String IMAGEPAGESIZE="ImagePageSize";
	public static final String SERVERNAME="ServerName";
	public static final String USERUPDATETIME="UserUpdateTime";
	public static final String TEXTMSG = "replyText";

	private static final AppConfig m = new AppConfig();
	private AppConfig(){}
	
	public static AppConfig getConfig() { return m; }
	
	public String put(String k, String v) {
		if( k == null || v == null ) return null;
		if( super.get(k) != null ) return null; //once put, exists and does not change forever
		return super.put(k, v);
	}
}
