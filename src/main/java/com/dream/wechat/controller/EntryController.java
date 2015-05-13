package com.dream.wechat.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
	public ModelAndView auth(Model model, HttpSession session, @RequestParam(value="code", required=false) String code, 
			@RequestParam(value="state", required=false) String state,
			@RequestParam(value="auth_scope", required=false) String auth_scope,
			@RequestParam(value="go", required=false) String go, final RedirectAttributes redirectAttributes) {
		AppConfig cfg = AppConfig.getConfig();
		boolean isSNSUserInfo = true;
		if( auth_scope != null && auth_scope.equals(AuthUtils.basicAuthScope) )
			isSNSUserInfo = false;
		model.asMap().clear();
		
		if( code == null && state == null ) {
			if( session.getAttribute("USER") == null ) {
				try {
					StringBuffer sb = new StringBuffer();
					StringBuffer url = new StringBuffer();
					url.append("http://").append(cfg.get(AppConfig.SERVERNAME)).append("/auth");
					if( go!=null&&go.length()>0) 
						url.append("?go=").append(go);
					sb.append("redirect:").append(AuthUtils.authRedirectURL).append("?appid=").append(AuthUtils.appId)
					.append("&redirect_uri=").append(URLEncoder.encode(url.toString(), "utf-8"))
					.append("&response_type=code&scope=").append(isSNSUserInfo?AuthUtils.userInfoAuthScope:AuthUtils.basicAuthScope)
					.append("&state=m_idreamfactory_cn#wechat_redirect");
//					String redirectUrl = String.format("redirect:%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect", 
//											AuthUtils.authRedirectURL, AuthUtils.appId, 
//											URLEncoder.encode("http://"+cfg.get(AppConfig.SERVERNAME) + "/auth","utf-8"),
//											(isSNSUserInfo?AuthUtils.userInfoAuthScope:AuthUtils.basicAuthScope), "m_idreamfactory_cn");
					return new ModelAndView(sb.toString());
				} catch(Exception e) {
					e.printStackTrace();
					return new ModelAndView("redirect:main");
				}
			}
		} 
		else if( code != null && state.equals("m_idreamfactory_cn")) {
			ModelAndView m = new ModelAndView("redirect:main");
			if( go != null && go.length() > 0)
				session.setAttribute("go", go);
			Oauth o = AuthUtils.getOauth(code);
			if( o != null ) //wechat server gives proper respond
			{
				User u = uService.getUserByOpenid(o.getOpenid());
				if( u != null )  //user already exists in db
				{
					logger.debug("Registered user log in");
					long diff = (System.currentTimeMillis() - u.getUpdateTime().getTime());
					diff = ( diff > 0 ) ? diff : -diff;
					int hours = Integer.parseInt(cfg.get(AppConfig.USERUPDATETIME));
					if( diff < hours * 3600 * 1000 ) { //if user info is still fresh, let him go
						UserLite ul = new UserLite();
						ul.setId(u.getId());
						session.setAttribute("USER", ul);
						return m;
					}
				} 
				else{
					// Not a registered user and not snsapi_userinfo scope
					if( !isSNSUserInfo ) 
						return m;
					 
				}
				//otherwise, no matter it is a new user or an user info update, the following needs to be done
				int i = 0;
				User ux = null;
				try{
					ux = AuthUtils.getUserInfo(o.getOpenid(), o.getOauthToken());
				} catch(Exception e) {
					e.printStackTrace();
					return m;
				}
				String relative = AppConfig.HEADDIR + File.separator + o.getOpenid() + ".jpg";
				String head = servletCtx.getRealPath("/") + relative;
				try{ 
					if( ux.getHeadimgurl() != null && ux.getHeadimgurl().trim().length() > 0) {
						CommonUtils.downloadImg(ux.getHeadimgurl(), head);
						ux.setHeadimgurl(relative);
					} else {
						ux.setHeadimgurl(AppConfig.HEADDIR + File.separator + "default.jpg");
					}
				} catch(Exception e) {
					e.printStackTrace();
					ux.setHeadimgurl(AppConfig.HEADDIR + File.separator + "default.jpg");
				}

				i = uService.updateOrInsert(ux);				
				logger.debug("User ID (update or insert): {}", i);
				UserLite ul = new UserLite();
				ul.setId(i);
				session.setAttribute("USER", ul);
				return m;
			} 
		} 
		ModelAndView m = new ModelAndView("redirect:main");
		if( go != null && go.length() > 0)
			session.setAttribute("go", go);
		return m;
	}
	
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public ModelAndView test(Model model, HttpSession session, @RequestParam(value="go", required=false) String go) {
		if( go != null && go.length() > 0 )
			session.setAttribute("go", go);
//		model.asMap().clear();
		return new ModelAndView("redirect:main");
	}
	
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public ModelAndView main(Model model, HttpSession session, @RequestParam(value="openid", required=false) String openid,
			@RequestParam(value="from", required=false) String from, 
			@RequestParam(value="isappinstalled", required=false) String isappinstalled) {
		String go = (String)session.getAttribute("go");
		if( openid != null && openid.length() > 0 ) {
			//for test
			User u = uService.getUserByOpenid(openid);
			if( u != null ) {
				logger.info("User id:{}, openid:{} is getting in system using hidden entry", u.getId(), u.getOpenId());
				UserLite ul = new UserLite();
				ul.setId(u.getId());
				session.setAttribute("USER", ul);
			}
		}
//		Map<String,Object> map = model.asMap();
		//generate signature for jssdk
		StringBuffer url = new StringBuffer();
		url.append("http://m.idreamfactory.cn/main");
		boolean start = false;
		if(from!=null&&!from.isEmpty()) {
			url.append("?from=");
			url.append(from);
			start = true;
		}
		if(isappinstalled!=null&&!isappinstalled.isEmpty()) {
			if( start ) url.append("&");
			else url.append("?");
			url.append("isappinstalled=");
			url.append(isappinstalled);
		}
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
				timestamp, url.toString());
		
		HashMap<String,String> mmap = new HashMap<String,String>();
		mmap.put("nonce", nonce);
		mmap.put("timestamp", timestamp);
		mmap.put("signature", signature);
		mmap.put("appId", AuthUtils.appId);
		mmap.put("url", url.toString());
		if( go != null ) {
			mmap.put("go", go);
			session.removeAttribute("go");
		}

		return new ModelAndView("index",mmap);
	}

}
