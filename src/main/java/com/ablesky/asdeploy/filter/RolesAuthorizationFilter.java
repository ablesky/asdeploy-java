package com.ablesky.asdeploy.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.util.WebUtils;

/**
 * 本filter在向unauthorized页面重定向时，会携带当前请求中“unauthz_”开头的参数
 * unauthz_type=="simple"表示unauthorized页面不需要通用头，对应弹出窗口的情形
 */
public class RolesAuthorizationFilter extends org.apache.shiro.web.filter.authz.RolesAuthorizationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {

        Subject subject = getSubject(request, response);
        // If the subject isn't identified, redirect to login URL
        if (subject.getPrincipal() == null) {
            saveRequestAndRedirectToLogin(request, response);
        } else {
            // If subject is known but not authorized, redirect to the unauthorized URL if there is one
            // If no unauthorized URL is specified, just return an unauthorized HTTP status code
            String unauthorizedUrl = getUnauthorizedUrl();
            //SHIRO-142 - ensure that redirect _or_ error code occurs - both cannot happen due to response commit:
            if (StringUtils.hasText(unauthorizedUrl)) {
            	// carry all parameters with "unauthz_" prefix when redirect to the unauthorizedUrl
            	Map<String, Object> unauthzParams = org.springframework.web.util.WebUtils.getParametersStartingWith(request, "unauthz_");
                WebUtils.issueRedirect(request, response, unauthorizedUrl, unauthzParams, true, true);
            } else {
                WebUtils.toHttp(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        return false;
    }
	
}
