package com.dream.wechat.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dream.wechat.AppConfig;
import com.dream.wechat.CommonUtils;
import com.dream.wechat.auth.AuthUtils;
import com.dream.wechat.comm.WeChatXML;
import com.dream.wechat.model.User;
import com.dream.wechat.model.UserImg;
import com.dream.wechat.services.ImageService;
import com.dream.wechat.services.UserService;

@Controller
public class DockInController {
	private static final String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create";
	private static final String MENU_DEL_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete";
	private static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info";
	
	@Autowired
	private UserService uService;
	
	@Autowired
	private ImageService iService;
	
	@Autowired
	private ServletContext servletCtx;
	
	private static final Logger logger = LoggerFactory.getLogger(DockInController.class);
	
	
	private User getUserInfo2(String openid) {
		try{
			String token = AuthUtils.getAccessToken();
			String s = String.format("%s?access_token=%s&openid=%s&lang=zh_CN", USER_INFO_URL, token, openid);		
			String ret = AuthUtils.HttpsRequest(s);
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, String> obj = mapper.readValue(ret.getBytes("utf-8"), new TypeReference<HashMap<String,String>>(){});
			if( obj.get("errcode") != null )
				return null;
			User u = new User();
			u.setCity(obj.get("city"));
			u.setCountry(obj.get("country"));
			u.setHeadimgurl(obj.get("headimgurl"));
			u.setLanguage(obj.get("language"));
			u.setNickname(obj.get("nickname"));
			u.setOpenId(obj.get("openid"));
			u.setProvince(obj.get("province"));
			u.setSex(Integer.parseInt(obj.get("sex")));
			u.setSubscribe(Integer.parseInt(obj.get("subscribe")));
			u.setSubscribe_time(Long.parseLong(obj.get("subscribe_time")));
			return u;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//prepare text message
	private String replyText(String me, String openid) throws Exception {
		AppConfig cfg = AppConfig.getConfig();
		WeChatXML m = new WeChatXML();
		m.setRoot("xml");
		m.addCDATA("FromUserName", me);
		m.addCDATA("ToUserName", openid);
		m.addText("CreateTime", Long.toString(System.currentTimeMillis()/1000));
		m.addCDATA("MsgType", WeChatXML.TEXT_MSG);
		m.addCDATA("Content", cfg.get(AppConfig.TEXTMSG));
		return m.toXML();
	}
	
	// Prepare news 
	private String replySubscribe(String me, String openid) throws Exception {
		List<UserImg> ul = iService.getHottestTopN(4);
		String p = servletCtx.getRealPath("/") + "WEB-INF" + File.separator + "message.xml";

		WeChatXML m = new WeChatXML();
		try{
			String s = IOUtils.toString(new FileInputStream(new File(p)), "utf-8");
			m = WeChatXML.fromXML(s);
		} catch(Exception e) {
			e.printStackTrace();
		}

		m.addCDATA("FromUserName", me);
		m.addCDATA("ToUserName", openid);
		m.addText("CreateTime", Long.toString(System.currentTimeMillis()/1000));
		m.addCDATA("MsgType", WeChatXML.NEWS_MSG);
		
		
		int cnt = m.getEleCount("Articles");
		for( UserImg i : ul ) {
			WeChatXML ai = new WeChatXML();
			ai.setRoot("item");
			ai.addCDATA("Title", i.getName() + ": " + i.getDescription());
			ai.addCDATA("Description", i.getDescription());
			ai.addCDATA("Url", "http://m.idreamfactory.cn/main#/image/" + i.getId());
			ai.addCDATA("PicUrl", "http://m.idreamfactory.cn/" + i.getThumb());
			m.addItem("Articles", ai);
		}

		m.addText("ArticleCount", Integer.toString(ul.size()+cnt));
		return m.toXML();
	}

	
	@RequestMapping(value = "/dockIn.do", method = RequestMethod.GET)
	public @ResponseBody String dockIn(@RequestParam("signature") String signature, 
			@RequestParam("timestamp") long timestamp, @RequestParam("nonce") String nonce, 
			@RequestParam("echostr") String echostr) {
		if( AuthUtils.verifySignature(signature, timestamp, nonce))
			return echostr;
		return "AUTH FAILED!";
	}
	
	@RequestMapping(value = "/dockIn.do", method = RequestMethod.POST, produces="text/html;charset=utf-8")
	public @ResponseBody String messageEntry(HttpServletRequest request, HttpServletResponse response) {
		try {
			WeChatXML m = WeChatXML.fromXML(request.getInputStream());
			logger.debug("Message received. type:{}, content: {}",(String)m.get("MsgType"), m.toXML());
			String msgType = (String)m.get("MsgType");
			if( msgType == null ) throw new Exception("Unrecognised data");
			if( msgType.equals(WeChatXML.EVENT_MSG)) {
				String event = (String)m.get("Event");
				if (event.equals(WeChatXML.EVENT_SUBSCRIBE)) {
					// A new user just subscribed us, set him up now.
					String openid = (String) m.get("FromUserName");
					if (openid == null)
						throw new Exception("The message is definitely wrong");

					User u = getUserInfo2(openid);
					String relative = AppConfig.HEADDIR + File.separator
							+ u.getOpenId() + ".jpg";
					String head = servletCtx.getRealPath("/") + relative;
					try {
						if( u.getHeadimgurl()!=null ) {
							CommonUtils.downloadImg(u.getHeadimgurl(), head);
							u.setHeadimgurl(relative);
						} else {
							u.setHeadimgurl( AppConfig.HEADDIR + File.separator + "default.jpg");
						}
					} catch (Exception e) {
						u.setHeadimgurl(AppConfig.HEADDIR + File.separator + "default.jpg");
					}
					uService.updateOrInsert(u);
//					// send back news
					return replySubscribe((String) m.get("ToUserName"),
							(String) m.get("FromUserName"));
				}
				if (event.equals(WeChatXML.EVENT_UNSUNSCRIBE)) {
					uService.unsubscribe((String) m.get("FromUserName"));
				}
			}
			else if(msgType.equals(WeChatXML.TEXT_MSG)) {
				return replyText((String)m.get("ToUserName"), (String)m.get("FromUserName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@RequestMapping(value = "/menuDel.do", method=RequestMethod.GET)
	public @ResponseBody String menuDel() {
		try{
			String token = AuthUtils.getAccessToken();
			URL u = new URL(MENU_DEL_URL + "?access_token=" + token);
			HttpsURLConnection conn = (HttpsURLConnection)u.openConnection();
			conn.setReadTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			int respCode = conn.getResponseCode();
			if( respCode == HttpsURLConnection.HTTP_OK) {
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, String> obj = mapper.readValue(conn.getInputStream(), new TypeReference<HashMap<String, String>>(){});
				String errCode = obj.get("errcode");
				if( errCode != null && errCode.equals("0")) {
					return "SUCCESS";
				}
				else {
					return "FAILED: " + obj.get("errcode") + " " + obj.get("errmsg");
				}				
			}
		} catch(Exception e) {
			e.printStackTrace();
			return "Exception happens";
		}
		return "";
	}
	
	@RequestMapping(value = "/menu.do", method=RequestMethod.POST)
	public @ResponseBody String menu(HttpServletRequest request, HttpServletResponse response) {
		String p = servletCtx.getRealPath("/") + "WEB-INF" + File.separator + "menu.json";
		try{
			InputStreamReader is = new InputStreamReader(new FileInputStream(new File(p)));
			BufferedReader reader = new BufferedReader(is);
			String line;
			String token = AuthUtils.getAccessToken();
			URL u = new URL(MENU_CREATE_URL + "?access_token=" + token);
			HttpsURLConnection conn = (HttpsURLConnection)u.openConnection();
			conn.setReadTimeout(3000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(os);
			while( (line = reader.readLine()) != null )
				writer.write(line);
			writer.flush();
			writer.close();
			int respCode = conn.getResponseCode();
			is.close();
			reader.close();
			if( respCode == HttpsURLConnection.HTTP_OK) {
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String, String> obj = mapper.readValue(conn.getInputStream(), new TypeReference<HashMap<String,String>>(){});
				if( obj.get("errcode").equals("0")) {
					return "SUCCESS";
				}
				else {
					return "FAILED: " + obj.get("errcode") + " " + obj.get("errmsg") ;
				}
			} else {
				return "FAILED: Wechat server is not happy";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "Exception happens";
		}
	}
}
