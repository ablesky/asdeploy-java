<%@page import="com.ablesky.asdeploy.util.DeployConfiguration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include.jsp" %>
<input type="hidden" id="J_environment" value="<%=DeployConfiguration.getInstance().getEnvironment()%>" />
<input type="hidden" id="J_releasedVersion" value="<%=DeployConfiguration.getInstance().getVersion() %>" />
<div id="J_topNav" class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="row-fluid">
			<div class="span4"></div>
			<div class="span4">
				<a class="brand">
					<strong>AbleSky代码发布系统 </strong>
				</a>
			</div>
			<div class="span4">
				<ul class="nav pull-right">
					<li><a id="J_envLogo" href="#"><%=DeployConfiguration.getInstance().getEnvironment()%></a></li>
					<li class="divider-vertical"></li>
					<shiro:user>
						<li><a href="${ctx_path}/main"><strong>主页</strong></a></li>
						<li class="divider-vertical"></li>
						<li><a href="${ctx_path}/user/detail"><strong><shiro:principal/></strong></a></li>
						<li class="divider-vertical"></li>
						<li><a href="${ctx_path}/logout" class="nav-end"><strong>退出</strong></a></li>
					</shiro:user>
					<shiro:guest>
						<li><a href="${ctx_path}/login"><strong>登录</strong></a></li>
						<li class="divider-vertical"></li>
						<li><a href="${ctx_path}/register" class="nav-end"><strong>注册</strong></a></li>
					</shiro:guest>
				</ul>
			</div>
		</div>
	</div>
</div>
<%@include file="./msg/alertModal.jsp" %>
<%@include file="./msg/confirmModal.jsp" %>
<div style="margin-bottom: 100px;"></div>