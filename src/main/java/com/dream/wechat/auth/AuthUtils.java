package com.dream.wechat.auth;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dream.wechat.AppConfig;
import com.dream.wechat.CommonUtils;
import com.dream.wechat.model.User;


public class AuthUtils {
	private static long tolerantDiff = 10;
	
	public static String authRedirectURL = "https://open.weixin.qq.com/connect/oauth2/authorize";
	public static String oauthTokenURL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static String snsUserInfoURL = "https://api.weixin.qq.com/sns/userinfo";
	public static String jsTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	public static String mediaUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get";
	public static String basicAuthScope = "snsapi_base";
	public static String userInfoAuthScope = "snsapi_userinfo";
	
	private static String accessTokenURL = "https://api.weixin.qq.com/cgi-bin/token";
	private static String accessToken;
	private static String jsTicket;
	private static long jsTicketTimeStamp = 0;
	private static long jsTicketExpiresIn = 7150;
	private static long accessTokenTimeStamp = 0;
	private static long accessTokenExpiresIn = 7150;  
	
	public static final String token = "dreamFactory2015";
	public static final String appId = "wx1e26348024375ca0";
	public static final String appSecret = "6421da95b8eea8cf32c170f09f4d7588";


	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

	private static String byte2hex(byte[] b) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < b.length; i++) {
			tmp = (Integer.toHexString(b[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	/*
	 * Web server obtains authorization by calling this method. Note, we check
	 * timestamp here to fend off "Replay attack"
	 */
	public static boolean verifySignature(String signature, long timestamp,
			String nonce) {
		long diff = java.lang.Math.abs(System.currentTimeMillis() / 1000
				- timestamp);
		if (diff > tolerantDiff)
			return false;
		return signature == genSignature(timestamp, nonce);
	}
	
	public static String genRandomString(int len) {
		char[] a = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		int max = a.length - 1;
		Random rand = new Random(System.currentTimeMillis());
		StringBuffer sb = new StringBuffer();
		for( int i=0; i<len; i++ ) {
			sb.append(a[rand.nextInt(max)]);	
		}
		return sb.toString();
	}
	public static String genJsSignature(String nonce, String ticket, String timestamp, String url ) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			StringBuffer sb = new StringBuffer();
			sb.append("jsapi_ticket="); sb.append(ticket); 
			sb.append("&noncestr="); sb.append(nonce);
			sb.append("&timestamp="); sb.append(timestamp);
			sb.append("&url="); sb.append(url);
			byte[] data = md.digest(sb.toString().getBytes("utf-8"));
			return byte2hex(data);
		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String genSignature(long timestamp, String nonce) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			String[] arr = { token, nonce, Long.toString(timestamp) };
			Arrays.sort(arr);
			StringBuffer sb = new StringBuffer();
			sb.append(arr[0]);
			sb.append(arr[1]);
			sb.append(arr[2]);
			byte[] data = md.digest(sb.toString().getBytes("utf-8"));
			return byte2hex(data);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String HttpsRequest(String ref)  {
		HttpsURLConnection conn = null;
		try{
			URL url = new URL(ref);
			conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setConnectTimeout(3000);
			int respCode = conn.getResponseCode();
			if( respCode == HttpsURLConnection.HTTP_OK ) {
				String ret = IOUtils.toString(conn.getInputStream());
				logger.debug("Request: {} <-> Answer: {}", ref, ret);
				return ret;
			}
		} catch(Exception e ) {
			e.printStackTrace();
		} finally{
			conn.disconnect();
			if( conn != null ) conn.disconnect();
		}
		return "";
	}
	
	public static Oauth getOauth(String code)  {
		String u = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code", oauthTokenURL, 
				appId,appSecret, code);
		try{
			String resp = HttpsRequest(u);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,String> obj = mapper.readValue(resp.getBytes("utf-8"), new TypeReference<HashMap<String,String>>(){});
			if( obj.get("errcode") != null ) {
				logger.info("Failed to get oauth token, error code:{}, errmsg: {} ", obj.get("errcode"), obj.get("errmsg"));
				return null;
			}
			Oauth o = new Oauth();
			o.setOauthToken(obj.get("access_token"));
			o.setOpenid(obj.get("openid"));
			o.setScope(obj.get("scope"));
			o.setRefreshOauthToken(obj.get("refresh_token"));
			o.setExpire(Long.parseLong(obj.get("expires_in")));
			o.setTimeStamp(System.currentTimeMillis() / 1000);
			return o;
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static User getUserInfo(String openid, String token) throws Exception {
		String ref = String.format("%s?access_token=%s&openid=%s&lang=zh_CN", snsUserInfoURL, token, openid);
		String ret = HttpsRequest(ref);
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> obj = mapper.readValue(ret.getBytes("utf-8"), new TypeReference<HashMap<String,Object>>(){});
		if( obj.get("errcode")!=null) {
			logger.info("Fail to get user info, error code:{}, errmsg:{}", obj.get("errcode"), obj.get("errmsg"));
			return null;
		}
		User u = new User();
		u.setCity((String)obj.get("city"));
		u.setCountry((String)obj.get("country"));
		u.setHeadimgurl((String)obj.get("headimgurl"));
		u.setNickname((String)obj.get("nickname"));
		u.setOpenId((String)obj.get("openid"));
		u.setUnionId((String)obj.get("unionid"));
		u.setProvince((String)obj.get("province"));
		u.setSex((Integer)obj.get("sex"));
		
		return u;
	}

	public static String getAccessToken(boolean bForce) throws Exception {
		if( bForce ) accessToken = null;
		long seconds = System.currentTimeMillis() / 1000 - accessTokenTimeStamp;
		logger.debug("ACCESS TOKEN lifetime check: {} seconds", seconds);
		if( (seconds > accessTokenExpiresIn ) || accessToken == null ) {
			logger.debug("ACCESS TOKEN expired, trying to grab a new one.");
			String u = String.format("%s?grant_type=client_credential&appid=%s&secret=%s", accessTokenURL, 
					URLEncoder.encode(appId,"utf-8"), URLEncoder.encode(appSecret,"utf-8"));

			String resp = HttpsRequest(u);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,String> obj = mapper.readValue(resp.getBytes("utf-8"), new TypeReference<HashMap<String,String>>(){});
			if( obj.get("errcode") != null )  {
				String s = String.format("Failed to get access token, error code:%s, errmsg: %s ", obj.get("errcode"), 
						obj.get("errmsg"));
				throw new Exception(s);
			}
			long l = Long.parseLong(obj.get("expires_in"));
			accessTokenExpiresIn = ( l > 50 )?(l-50):l;
			accessToken = obj.get("access_token");
			accessTokenTimeStamp = System.currentTimeMillis()/1000;
			logger.debug("ACCESS TOKEN renewed, exprires in {}", accessTokenExpiresIn);
		}
		return accessToken;
	}
	
	public static String getJsTicket() throws Exception {
		if( (System.currentTimeMillis() / 1000 - jsTicketTimeStamp > jsTicketExpiresIn) || jsTicket == null ) {
			logger.debug("JS TICKET expired, grab a new one.");
			String u = String.format("%s?access_token=%s&type=jsapi", jsTicketUrl, getAccessToken(false));
			String resp = HttpsRequest(u);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> obj = mapper.readValue(resp.getBytes("utf-8"), new TypeReference<HashMap<String,String>>(){});
			if( Integer.parseInt(obj.get("errcode")) == 0 ) {
				jsTicket = obj.get("ticket");
				long l = Long.parseLong(obj.get("expires_in"));
				jsTicketExpiresIn = ( l > 50 ) ? ( l - 50 ) : l;
				jsTicketTimeStamp = System.currentTimeMillis()/1000;
			}
		}
		return jsTicket;
	}

	public static boolean downloadImage(String id, String path)  {
		FileOutputStream fs = null;
		InputStream in = null;
		try {
			String token = getAccessToken(false);
			String ref = String.format("%s?access_token=%s&media_id=%s", mediaUrl, token, id);
			HttpURLConnection conn = null;
			URL url = new URL(ref);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setConnectTimeout(3000);
			Map<String, List<String>> map = conn.getHeaderFields();
			String contentType = conn.getHeaderField("Content-Type");
			int respCode = conn.getResponseCode();
			if (respCode == HttpURLConnection.HTTP_OK) {
				if ("image/jpeg".equals(contentType)) {
					fs = new FileOutputStream(path);
					in = conn.getInputStream();
					int nRead = -1;
					byte[] buf = new byte[1024];
					while ((nRead = in.read(buf)) != -1) {
						fs.write(buf, 0, nRead);
					}
					fs.close(); in.close();
					conn.disconnect();
					return true;
				} else if(contentType != null && contentType.contains("json")) {
					String resp = IOUtils.toString(conn.getInputStream());
					logger.debug("Download image file fail:  {}", resp);
				}
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
}
