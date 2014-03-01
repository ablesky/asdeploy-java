<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include.jsp" %>
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
					<li><a id="J_envLogo" href="#"></a></li>
					<li class="divider-vertical"></li>
					<c:choose>
						<c:when test="${not empty username}">
							<li><a href="/"><strong>主页</strong></a></li>
							<li class="divider-vertical"></li>
							<li><a href="/user/${username}"><strong>${username}</strong></a></li>
							<li class="divider-vertical"></li>
							<li><a href="/logout" class="nav-end"><strong>退出</strong></a></li>
						</c:when>
						<c:otherwise>
							<li><a href="/login"><strong>登录</strong></a></li>
							<li class="divider-vertical"></li>
							<li><a href="/register" class="nav-end"><strong>注册</strong></a></li>
						</c:otherwise>
					</c:choose>
				</ul>
			</div>
		</div>
	</div>
</div>
<div style="margin-bottom: 70px;"></div>