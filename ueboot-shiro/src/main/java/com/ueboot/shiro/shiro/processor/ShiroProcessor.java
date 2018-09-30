package com.ueboot.shiro.shiro.processor;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;


/**
 * @author yangkui
 */
@Slf4j
@Component
public class ShiroProcessor {

	public void login(String username, String password) {

		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject currentUser = SecurityUtils.getSubject();
		//在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
		//每个Realm都能在必要时对提交的AuthenticationTokens作出反应
		//所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
		log.info("对用户[" + username + "]进行登录验证..验证开始");
		try{
			log.info("验证用户和密码开始...");
			currentUser.login(token);
			log.info("验证用户和密码结束...");
		}catch(UnknownAccountException e){
			//model.addAttribute(Constants.LOGIN_ERROR_MSG,"用户或密码不正确.");
			log.error(e.getMessage(),e);
			//return R.error("用户不存在");
			throw new AuthenticationException("用户不存在");
		}catch(IncorrectCredentialsException e){
			log.error(e.getMessage(),e);
//			return R.error("登录的用户密码不正确");
			throw new AuthenticationException("用户的密码不正确");
		}catch(LockedAccountException e){
			log.error(e.getMessage(),e);
//			return R.error("用户已锁定");
			throw new AuthenticationException("用户已锁定");
		}catch(ExpiredCredentialsException e){
			log.error(e.getMessage(),e);
//			return R.error("密码已过期，请联系管理员");
			throw new AuthenticationException("密码已过期，请联系管理员");
		}catch(ExcessiveAttemptsException e){
			log.error(e.getMessage(),e);
			//return R.error("输入密码次超5次，账号已经锁定");
			throw new ExcessiveAttemptsException("输入密码次超5次，账号已经锁定");
		}catch(AuthenticationException e){
			log.error(e.getMessage(),e);
			//return R.error("用户的密码不正确");
			throw new AuthenticationException("用户或密码不正确");
		}

		log.info("对用户[" + username + "]进行登录验证..验证通过");
		//验证是否登录成功
		if (!currentUser.isAuthenticated()) {
			token.clear();
			throw new AuthenticationException();
		}
		log.info("用户[" + username + "]登录认证通过");
	}

	public void logout() {
		//使用权限管理工具进行用户的退出，跳出登录，给出提示信息
		SecurityUtils.getSubject().logout();
	}

}
