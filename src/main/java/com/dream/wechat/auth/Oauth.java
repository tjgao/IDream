package com.dream.wechat.auth;

public class Oauth {
	private String oauthToken;
	private String refreshOauthToken;
	private long timeStamp;
	private long expire;
	private String openid;
	private String scope;
	private String unionid;
	private int userId;
	public String getOauthToken() {
		return oauthToken;
	}
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	public String getRefreshOauthToken() {
		return refreshOauthToken;
	}
	public void setRefreshOauthToken(String refreshOauthToken) {
		this.refreshOauthToken = refreshOauthToken;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expired) {
		this.expire = expired;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
