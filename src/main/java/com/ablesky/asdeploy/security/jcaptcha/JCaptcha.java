package com.ablesky.asdeploy.security.jcaptcha;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

public class JCaptcha {
	
	public static final DefaultManageableImageCaptchaService captchaService = new DefaultManageableImageCaptchaService(
			new FastHashMapCaptchaStore(), 
			new MyGmailEngine(),
			180, 
			100000, 
			75000);
	
	public static boolean validateResponse(HttpSession session, String verifyCode) {
		if(session == null) {
			return false;
		}
		try {
			return captchaService.validateResponseForID(session.getId(), verifyCode);
		} catch (CaptchaServiceException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean validateResponse(HttpServletRequest request, String verifyCode) {
		return validateResponse(request.getSession(false), verifyCode);
	}
}
