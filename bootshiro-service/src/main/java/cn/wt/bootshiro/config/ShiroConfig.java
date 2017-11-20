package cn.wt.bootshiro.config;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cn.wt.bootshiro.shiro.job.DefaultQuartzSessionValidationScheduler;
import cn.wt.bootshiro.shiro.realm.MemberRealm;
import cn.wt.bootshiro.shiro.realm.matcher.DefaultCredentialsMatcher;
import cn.wt.bootshiro.shiro.util.cache.manager.RedisCacheManager;
@Configuration
public class ShiroConfig {
	public static final String LOGOUT_URL = "/logout.action" ;
	public static final String LOGIN_URL = "/loginPage" ;
	public static final String UNAUTHORIZED_URL = "/unauth" ;
	public static final String SUCCESS_URL = "/pages/back/welcome" ;
	
	@Resource(name = "redisConnectionFactory")
	private RedisConnectionFactory redisConnectionFactoryAuthentication;
	@Resource(name = "redisConnectionFactoryAuthorization")
	private RedisConnectionFactory redisConnectionFactoryAuthorization;
	@Resource(name = "redisConnectionFactoryActiveSessionCache")
	private RedisConnectionFactory redisConnectionFactoryActiveSessionCache; 
	
	
	@Bean
	public MemberRealm getRealm() {// 1、获取配置的Realm，之所以没使用注解配置，是因为此处需要考虑到加密处理
		MemberRealm realm = new MemberRealm();
		realm.setCredentialsMatcher(new DefaultCredentialsMatcher());	
		realm.setAuthenticationCachingEnabled(true);
		realm.setAuthenticationCacheName("authenticationCache");
		realm.setAuthorizationCachingEnabled(true);
		realm.setAuthorizationCacheName("authorizationCache");
		return realm;
	}
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}
	@Bean
	public CacheManager getCacheManager(
			@Qualifier("redisConnectionFactory")
			RedisConnectionFactory redisConnectionFactoryAuthentication ,
			@Qualifier("redisConnectionFactoryAuthorization")
			RedisConnectionFactory redisConnectionFactoryAuthorization ,
			@Qualifier("redisConnectionFactoryActiveSessionCache")
			RedisConnectionFactory redisConnectionFactoryActiveSessionCache
			) {// 2、缓存配置
		RedisCacheManager cacheManager = new RedisCacheManager();
		Map<String,RedisConnectionFactory> map = new HashMap<>() ;
		map.put("authenticationCache", redisConnectionFactoryAuthentication) ;
		map.put("authorizationCache", redisConnectionFactoryAuthorization) ;
		map.put("activeSessionCache", redisConnectionFactoryActiveSessionCache) ;
		cacheManager.setConnectionFactoryMap(map);
		return cacheManager;
	}
	@Bean
	public SessionIdGenerator getSessionIdGenerator() { // 3
		return new JavaUuidSessionIdGenerator();
	}
	@Bean
	public SessionDAO getSessionDAO(SessionIdGenerator sessionIdGenerator) { // 4
		EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
		sessionDAO.setActiveSessionsCacheName("activeSessionCache");
		sessionDAO.setSessionIdGenerator(sessionIdGenerator);
		return sessionDAO;
	}
	@Bean
	public RememberMeManager getRememberManager() { // 5
		CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
		SimpleCookie cookie = new SimpleCookie("MLDNJAVA-RememberMe");
		cookie.setHttpOnly(true); 
		cookie.setMaxAge(3600);
		rememberMeManager.setCookie(cookie);
		return rememberMeManager;
	}
	@Bean
	public DefaultQuartzSessionValidationScheduler getQuartzSessionValidationScheduler(DefaultWebSessionManager sessionManager) {
		DefaultQuartzSessionValidationScheduler sessionValidationScheduler = new DefaultQuartzSessionValidationScheduler();
		sessionValidationScheduler.setSessionValidationInterval(100000);
		sessionValidationScheduler.setSessionManager(sessionManager);
		return sessionValidationScheduler;
	}
	 
//	@Bean
//	public QuartzSessionValidationScheduler getQuartzSessionValidationScheduler() {
//		QuartzSessionValidationScheduler sessionValidationScheduler = new QuartzSessionValidationScheduler();
////		sessionValidationScheduler.setSessionValidationInterval(100000);
//		return sessionValidationScheduler;
//	}	
	
	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa; 
	}

	@Bean
	public ShiroDialect shiroDialect() { // 追加配置，否则无法启动Thymeleaf模版支持
		return new ShiroDialect();
	}
	@Bean
	public DefaultWebSessionManager getSessionManager(SessionDAO sessionDAO) { // 6
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//		sessionManager.setGlobalSessionTimeout(1000000);
		sessionManager.setDeleteInvalidSessions(true);
		//sessionManager.setSessionValidationScheduler(sessionValidationScheduler);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setSessionDAO(sessionDAO);
		SimpleCookie sessionIdCookie = new SimpleCookie("mldn-session-id");
		sessionIdCookie.setHttpOnly(true);
		sessionIdCookie.setMaxAge(-1);
		sessionManager.setSessionIdCookie(sessionIdCookie);
		sessionManager.setSessionIdCookieEnabled(true);
		return sessionManager;
	}
	@Bean
	public DefaultWebSecurityManager getSecurityManager(Realm memberRealm, CacheManager cacheManager,
			SessionManager sessionManager, RememberMeManager rememberMeManager) {// 7
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(memberRealm);
		securityManager.setCacheManager(cacheManager);
		securityManager.setSessionManager(sessionManager);
		securityManager.setRememberMeManager(rememberMeManager);
		return securityManager;
	}
	public FormAuthenticationFilter getLoginFilter() { // 在ShiroFilterFactoryBean中使用
		FormAuthenticationFilter filter = new FormAuthenticationFilter();
		filter.setUsernameParam("mid");
		filter.setPasswordParam("password");
		filter.setRememberMeParam("rememberMe");
		filter.setLoginUrl(LOGIN_URL);	// 登录提交页面
		filter.setFailureKeyAttribute("error");
		return filter;
	}
	public LogoutFilter getLogoutFilter() { // 在ShiroFilterFactoryBean中使用
		LogoutFilter logoutFilter = new LogoutFilter();
		logoutFilter.setRedirectUrl("/");	// 首页路径，登录注销后回到的页面
		return logoutFilter;
	}
	@Bean
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl(LOGIN_URL);	// 设置登录页路径
		shiroFilterFactoryBean.setSuccessUrl(SUCCESS_URL);	// 设置跳转成功页
		shiroFilterFactoryBean.setUnauthorizedUrl(UNAUTHORIZED_URL);	// 授权错误页
		Map<String, Filter> filters = new HashMap<String, Filter>();
		filters.put("authc", this.getLoginFilter());
		filters.put("logout", this.getLogoutFilter());
		shiroFilterFactoryBean.setFilters(filters);
		Map<String, String> filterChainDefinitionMap = new HashMap<String, String>();
		filterChainDefinitionMap.put("/logout.page", "logout");
		filterChainDefinitionMap.put("/loginPage", "authc");	// 定义内置登录处理
		filterChainDefinitionMap.put("/pages/**", "authc");
		filterChainDefinitionMap.put("/*", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}

