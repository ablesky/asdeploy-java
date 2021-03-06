<%@page import="com.ablesky.asdeploy.util.AuthUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AbleSky代码发布系统</title>
<%@ include file="./include/includeCss.jsp" %>
<style>
.wrapper {
	text-align: center;
}
.lock-info {
	width: 400px; height: 100px; margin: 30px auto 10px; text-align: center;
}
.lock-info .alert {
	font-size:16px;
}
.main-btn-wrapper {
	width: 300px; margin: 10px auto 50px;
}
.main-btn-wrapper > a {
	margin-bottom: 25px;
}
</style>
</head>
<body>
<%@ include file="./include/header.jsp" %>
<div class="wrapper">
	<div class="title">
		<h1>welcome<shiro:user> <shiro:principal/></shiro:user></h1>
	</div>
	<div class="lock-info">
		<c:if test="${deployLock != null}">
			<div class="alert alert-error">
			  发布流程已被<strong>${deployLock.user.username}</strong>锁定
			</div>
			<c:if test="${isSuperAdmin || currentUser.id == deployLock.user.id }"> <!-- 本人或超级管理员 -->
				<button id="J_unlockDeployBtn" class="btn btn-primary">解锁</button>
			</c:if>
		</c:if>
	</div>
	<div class="main-btn-wrapper">
		<a type="button" class="btn btn-large btn-block" href="${ctx_path}/deploy/initOption">新的发布</a>
		<a type="button" class="btn btn-large btn-block" href="${ctx_path}/patchGroup/list">管理补丁组</a>
		<a type="button" class="btn btn-large btn-block" href="${ctx_path}/deployRecord/list">查看发布历史</a>
		<a type="button" class="btn btn-large btn-block" href="${ctx_path}/ablejs/index">查看静态文件构建依赖</a>
		<shiro:hasRole name="super_admin">
		<a type="button" class="btn btn-inverse btn-large btn-block" href="${ctx_path}/admin/project/list">管理项目</a>
		<a type="button" class="btn btn-inverse btn-large btn-block" href="${ctx_path}/admin/user/list">管理用户</a>
		</shiro:hasRole>
	</div>
</div>
</body>
<%@ include file="./include/includeJs.jsp" %>
<script>
seajs.use('app/main', function(main){
	main.init();
});
</script>
</html>