package com.example.webapp.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/** JQuery创建Cookie  $.cookie('userid',0,{expires:new Date()-1000, domain:'7k7k.com', path:'/'});
 *        获取Cookie  $.cookie(‘cookieName’); 
 *        删除Cookie  $.cookie(‘cookieName’,null);
 *        删除一个带有效路径的cookie  $.cookie(‘cookieName’,null,{path:’/'});
 * 1)cookie其实是由name,value, expires,path,domain等属性组成
 * 2)当没有指明cookie时间时，所创建的cookie有效期默认到用户浏览器关闭止，故被称为会话cookie
 * 3)expires：7  当指明时间时，故称为持久cookie，并且有效时间为天
 * 4)如果不设置有效路径，在默认情况下，只能在cookie设置当前页面读取该cookie，cookie的路径用于设置能够读取cookie的顶级目录
 * 5）跨域写cookie， 后端 设置domain和 跨域设置 .allowCredentials(true)
 *                 前端：withCredentials: true
 *     //跨域是否发送Cookie
 *       .allowCredentials(true)
 * @author gehaisong 
 *
 */
	public class CookieUtil {
	public static String domainName=null;
	public static String path="/";
	/**
	 * 记录一月
	 */
	public static int maxAge = 3600 * 24*30;
	public static final String JSESSIONID_COOKIE_NAME="JSESSIONID";


/**
 * * @Description: (获取session id)
   *  @param request
   *  @return
   *  @throws Exception
   * @return Cookie    
   * @author: 葛海松
   * @time:    2015年3月16日 下午2:26:36 
   * @throws
 */
public static Cookie getSessionCookie(HttpServletRequest request) throws Exception {
	return readCookie(request, JSESSIONID_COOKIE_NAME);
}

/**
 * * @Description:
   *  @param response
   *  @param  cookieName
   *  @param cookieValue
   *  @throws Exception
   * @return void    
   * @author: 葛海松
   * @time:    2015年3月14日 下午12:13:23 
   * @throws
 */
	public static void writeCookie(HttpServletResponse response, String cookieName, String cookieValue)throws Exception {
		if (StringUtils.isNotBlank(cookieValue)) {
			Cookie cookie = new Cookie(cookieName, cookieValue);
			cookie.setMaxAge(maxAge);
			cookie.setPath(path);
           	if (StringUtils.isNotBlank(domainName)){
				cookie.setDomain(domainName);
			}
			response.addCookie(cookie);
		}
	}
	/**
	 * * @Description: (读取指定的Cookie  path $.cookie('examIdArr',examIdArr,{path:'/'});)
	   *  @param request
	   *  @return
	   *  @throws Exception
	   * @return Cookie    
	   * @author: 葛海松
	   * @time:    2015年3月14日 下午12:20:59 
	   * @throws
	 */
	public static Cookie readCookie(HttpServletRequest request, String cookieName)throws Exception {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				if (c.getName().equals(cookieName)) {
					return c;
				}
			}
		}
		return null;
	}
	/**
	 * * @Description: (删除Cookie  path $.cookie('examIdArr',examIdArr,{path:'/'});
	 *                       如果写cookie的时候设置了path与domain,
	 *                       则清除cookie时也需要设置相同的path,domain,  如果没有设置domain, 即取当前的location.host) 
	   *  @param request
	   *  @param response
	   *  @param cookieName
	   *  @throws Exception
	   * @return void    
	   * @author: 葛海松
	   * @time:    2015年3月14日 下午12:27:32 
	   * @throws
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName)throws Exception {
		Cookie cookie=null;
		if(StringUtils.isNotBlank(cookieName)){
		   cookie=readCookie(request,cookieName);
		}
		if(cookie!=null){
			 cookie.setPath(path);
	         cookie.setValue("");
	         cookie.setMaxAge(0);
	         if (StringUtils.isNotBlank(domainName)){
				 cookie.setDomain(domainName);
			 }
	         response.addCookie(cookie);
		}
	}
	/**
	 * * @Description: (描述这个方法的作用)
	   *  @param request
	   *  @return    
	   * @return String    
	   * @author: 葛海松
	   * @time:    2015年3月14日 下午12:29:47 
	   * @throws
	 */
	public static String getDomainName(HttpServletRequest request) {
	        String domainName = request.getServerName();
	        if (domainName != null) {
	            int pos = domainName.indexOf(".");
	            if (pos > 0) {
	                domainName = domainName.substring(pos);
	                return domainName;
	            }
	        }
	        return null;
	    }
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String name){
	    Map<String,Cookie> cookieMap = ReadCookieMap(request);
	    if(cookieMap.containsKey(name)){
	        Cookie cookie = (Cookie)cookieMap.get(name);
	        return cookie;
	    }else{
	        return null;
	    }   
	}

	public static String getCookieValueByName(HttpServletRequest request, String name){
		Cookie cookie = getCookieByName(request,name);
		if(cookie!=null){
			return  cookie.getValue();
		}
		return null;
	}
	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
	    Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
	    Cookie[] cookies = request.getCookies();
	    if(null!=cookies){
	        for(Cookie cookie : cookies){
	            cookieMap.put(cookie.getName(), cookie);
	        }
	    }
	    return cookieMap;
	}

}
