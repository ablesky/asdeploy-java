package com.ablesky.asdeploy.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ablesky.asdeploy.security.jcaptcha.JCaptcha;
import com.ablesky.asdeploy.service.IDeployService;
import com.ablesky.asdeploy.service.IUserService;
import com.ablesky.asdeploy.util.AuthUtil;
import com.ablesky.asdeploy.util.ImageUtil;
import com.alibaba.fastjson.JSON;

@Controller
public class IndexController {
	
	public static final String DEFAULT_SUCCESS_URL = "/main";
	
	@Deprecated
	public static final String REGISTER_VERIFY_CODE = "registerVerifyCode";
	
	@Autowired
	private IUserService userService;

	@Autowired
	private IDeployService deployService;
	
	@RequestMapping({ "/", "/main"})
	public String main(Model model) {
		model.addAttribute("deployLock", deployService.checkCurrentLock())
			.addAttribute("currentUser", AuthUtil.getCurrentUser())
			.addAttribute("isSuperAdmin", AuthUtil.isSuperAdmin());
		return "main";
	}
	
	/**
	 * 登录页面
	 * 需要重新确认身份时，也会跳转此接口
	 * @throws IOException 
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(isAjax(request)) {
			writeJsonResponse(new ModelMap()
					.addAttribute("success", false)
					.addAttribute("needLogin", true)
					.addAttribute("message", "需要登录或重新确认身份!")
				, response);
			return null;
		} 
		if(AuthUtil.isAuthenticated()) {
			org.apache.shiro.web.util.WebUtils.redirectToSavedRequest(request, response, DEFAULT_SUCCESS_URL);
			return null;
		}
		return "login";
	}
	
	/**
	 * 注册成功后，重定向到登录页面
	 */
	@Deprecated
	@RequestMapping(value="/login/{msg}", method=RequestMethod.GET)
	public String login(@PathVariable("msg") String msg, Model model) {
		if("registerSuccess".equals(msg)) {
			model.addAttribute("successMessage", "注册成功, 请登录!");
		}
		return "login";
	}
	
	/**
	 * <p>登录操作(实际的登录操作在FormAuthenticationFilter中进行)</p>
	 * <p>登录失败后，会进入到此方法中</p>
	 * <p>如果已经是isAuthenticated的状态，也会进入此方法</p>
	 * <p>见{@link org.apache.shiro.web.filter.AccessControlFilter}的onPreHandle方法</p>
	 * @throws IOException 
	 */
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String afterLoginSubmission(String username, String password, Model model, 
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		if(AuthUtil.isAuthenticated()) {
			org.apache.shiro.web.util.WebUtils.redirectToSavedRequest(request, response, DEFAULT_SUCCESS_URL);
			return null;
		} 
		
		if(AuthUtil.isRemembered()) {
			model.addAttribute("errorMessage", "密码错误，请重试!");
		} else {
			model.addAttribute("errorMessage", "用户名或密码错误，请重试!");
		}
		return "login";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String register() {
		return "register";
	}
	
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String register(String username, String password, String confirmedPassword, String verifyCode, HttpSession session, 
			Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelMap validateResult = new ModelMap();
		if(StringUtils.isBlank(username)) {
			validateResult.addAttribute("usernameError", "用户名不能为空!");
		} else if(userService.getUserByUsername(username) != null) {
			validateResult.addAttribute("usernameError", "用户名已被占用!");
		}
		if(StringUtils.isBlank(password)) {
			validateResult.addAttribute("passwordError", "密码不能为空!");
		} else if (!password.equals(confirmedPassword)) {
			validateResult.addAttribute("confirmedPasswordError", "两次输入的密码不一致!");
		}
		if(StringUtils.isBlank(verifyCode)) {
			validateResult.addAttribute("verifyCodeError", "验证码不能为空!");
//		} else if(!verifyCode.toLowerCase().equals(session.getAttribute(REGISTER_VERIFY_CODE))) {
//			validateResult.addAttribute("verifyCodeError", "验证码输入错误!");
//		}
		} else if(!JCaptcha.validateResponse(session, verifyCode)) {
			validateResult.addAttribute("verifyCodeError", "验证码输入错误!");
		}
		if(validateResult.size() > 0) {
			model.addAllAttributes(validateResult);
			return "register";
		}
		userService.createNewUser(username, password);
		AuthUtil.login(username, password, true);
		return "redirect:" + DEFAULT_SUCCESS_URL;
	}
	
	@RequestMapping("/register/verifyImage")
	@Deprecated
	public void getVerifyImage(HttpSession session, HttpServletResponse response) {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 2000);

		String verifyCode = this.getRandomString(4);
		session.setAttribute(REGISTER_VERIFY_CODE, verifyCode);
		ServletOutputStream outStream = null;
		try {
			outStream = response.getOutputStream();
			BufferedImage image = ImageUtil.generateTextImage(18, 25, verifyCode, new Font("Corbe", Font.BOLD, 25),  new Color(0, 153, 255), new Color(238, 238, 238));
			ImageIO.write(image, "jpeg", outStream);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(outStream);
		}
	}
	
	private String getRandomString(int num) {
		if(num < 1 || num > 32) {
			throw new IllegalArgumentException("The lenght of random string should neither bigger than 32 nor less then 1!");
		}
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, num);
	}
	
	@RequestMapping("/unauthorized")
	public String unauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException { 
		if(isAjax(request)) {
			writeJsonResponse(new ModelMap().addAttribute("success", false).addAttribute("message", "没有权限!"), response);
			return null;
		} else {
			return "unauthorized";
		}
	}
	
	private boolean isAjax(HttpServletRequest request) {
		return BooleanUtils.toBoolean(request.getHeader("isAjax"));
	}
	
	private void writeJsonResponse(Map<String, Object> result, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(JSON.toJSONString(result));
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
}
