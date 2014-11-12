package com.ablesky.asdeploy.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

public class UriPatternExcludedOpenSessionInViewFilter extends OpenSessionInViewFilter {
	
	private static final String EXCLUDED_URI_PATTERNS = "excludedUriPatterns";
	
	private List<Pattern> excludedUriPatternList;
	
	public List<Pattern> getExcludedUriPatternList() {
		return excludedUriPatternList;
	}

	@Override
	protected void initFilterBean() throws ServletException {
		excludedUriPatternList = buildExcludedPatternsList(getFilterConfig().getInitParameter(EXCLUDED_URI_PATTERNS));
	}
	
	/**
	 * 如果当前uri与excludedUriPatterns相匹配，
	 * 则跳过openSession的步骤
	 */
	@Override
	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(isUriExcluded(request.getRequestURI())) {
			filterChain.doFilter(request, response);
		} else {
			super.doFilterInternal(request, response, filterChain);
		}
	}
	
	private boolean isUriExcluded(String uri) {
		if(excludedUriPatternList == null) {
			return false;
		}
		for(Pattern pattern: excludedUriPatternList) {
			if(pattern.matcher(uri).matches()) {
				return true;
			}
		}
		return false;
	}
	
	private List<Pattern> buildExcludedPatternsList(String patterns) {
		if (null != patterns && patterns.trim().length() != 0) {
			List<Pattern> list = new ArrayList<Pattern>();
			String[] tokens = patterns.split(",");
			for (String token : tokens) {
				list.add(Pattern.compile(token.trim()));
			}
			return Collections.unmodifiableList(list);
		} else {
			return null;
		}
	}
}
