package com.dream.wechat.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.classic.Logger;

import com.dream.wechat.AppConfig;
import com.dream.wechat.CommonUtils;
import com.dream.wechat.auth.AuthUtils;
import com.dream.wechat.auth.Oauth;
import com.dream.wechat.model.User;
import com.dream.wechat.model.UserLite;
import com.dream.wechat.services.UserService;


@Controller
public class EntryController {
	@Autowired
	private UserService uService;
	
	@Autowired
	private ServletContext servletCtx;
	
	private static final Logger logger = (Logger) LoggerFactory.getLogger(EntryController.class);
		
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public ModelAndView auth(HttpSession session, @RequestParam(value="code", required=false) String code, 
			@RequestParam(value="state", required=false) String state) {
		AppConfig cfg = AppConfig.getConfig();
		if( code == null && state == null ) {
			if( session.getAttribute("USER") == null ) {
				try {
					String redirectUrl = String.format("redirect:%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect", 
											AuthUtils.authRedirectURL, AuthUtils.appId, 
											URLEncoder.encode("http://"+cfg.get(AppConfig.SERVERNAME) + "/auth","utf-8"),
											AuthUtils.userInfoAuthScope, "m_idreamfactory_cn");
					logger.debug("redirect url: {}", redirectUrl);
					return new ModelAndView(redirectUrl);
				} catch(Exception e) {
					e.printStackTrace();
					return new ModelAndView("redirect:main");
				}
			}
		} 
		else if( code != null && state.equals("m_idreamfactory_cn")) {
			logger.debug("Redirected sucessfully");
			Oauth o = AuthUtils.getOauth(code);
			if( o != null ) //wechat server gives proper respond
			{
				User u = uService.getUser(o.getUserId());
				User ux = null;
				if( u != null )  //user already exists in db
				{
					long diff = (System.currentTimeMillis() - u.getUpdateTime().getTime());
					diff = ( diff > 0 ) ? diff : -diff;
					int hours = Integer.parseInt(cfg.get(AppConfig.USERUPDATETIME));
					if( diff < hours * 3600 * 1000 ) { //if user info is still fresh, let it go
						UserLite ul = new UserLite();
						ul.setId(u.getId());
						session.setAttribute("USER", ul);
						return new ModelAndView("redirect:main");
					}
				} 
				//otherwise, no matter it is a new user or an user info update, the following needs to be done
				int i = 0;
				try{
					ux = AuthUtils.getUserInfo(o.getOpenid(), o.getOauthToken());
					logger.debug("User ID (update or insert): {}", i);
				} catch(Exception e) {
					e.printStackTrace();
					return new ModelAndView("redirect:main");
				}
				String relative = AppConfig.HEADDIR + File.separator + o.getOpenid() + ".jpg";
				String head = servletCtx.getRealPath("/") + relative;
				try{ 
					if( ux.getHeadimgurl() != null ) {
						CommonUtils.downloadImg(ux.getHeadimgurl(), head);
						ux.setHeadimgurl(relative);
					} else
						ux.setHeadimgurl(AppConfig.HEADDIR + File.separator + "default.jpg");
				} catch(Exception e) {
					e.printStackTrace();
					ux.setHeadimgurl(AppConfig.HEADDIR + File.separator + "default.jpg");
				}

				i = uService.updateOrInsert(ux);				
				UserLite ul = new UserLite();
				ul.setId(i);
				session.setAttribute("USER", ul);
			} 
		} 
		return new ModelAndView("redirect:main");
	}
	
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public ModelAndView main() {
		String nonce = AuthUtils.genRandomString(10);
		String timestamp = Long.toString(System.currentTimeMillis()/1000);
		String ticket = null;
		try{
			ticket = AuthUtils.getJsTicket();
		} catch(Exception e) {
			e.printStackTrace();
			return new ModelAndView("index");
		}
		String signature = AuthUtils.genJsSignature(nonce, ticket, 
				timestamp, "http://m.idreamfactory.cn/main");
		
		HashMap<String,String> model = new HashMap<String,String>();
		model.put("nonce", nonce);
		model.put("timestamp", timestamp);
		model.put("signature", signature);
		model.put("appId", AuthUtils.appId);
		return new ModelAndView("index", model);
	}

}
