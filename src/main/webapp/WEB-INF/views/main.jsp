<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
<%@ include file="./include/includeCss.jsp" %>
<style>
.wrapper {
	text-align: center;
}
.welcome {
	margin-top: 30px;
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
	<div class="welcome">
		<h1>welcome <c:if test="${not empty username}">${username}</c:if></h1>
	</div>
	<div class="lock-info">
		<c:if test="${true}">
			<div class="alert alert-error">
			  发布流程已被<strong>{{curLock.user.username}}</strong>锁定
			</div>
			<c:if test="${true}">
				<button id="J_unlockDeployBtn" class="btn btn-primary">解锁</button>
			</c:if>
		</c:if>
	</div>
	<div class="main-btn-wrapper">
		<a type="button" class="btn btn-large btn-block" href="/deployInitOption/">新的发布</a>
		<a type="button" class="btn btn-large btn-block" href="/patchGroupList/1/">管理补丁组</a>
		<a type="button" class="btn btn-large btn-block" href="/deployRecordList/1/">查看发布历史</a>
		<a type="button" class="btn btn-large btn-block" href="/queryAblejsDependencyPage/">查看静态文件构建依赖</a>
	</div>
</div>
</body>
<%@ include file="./include/includeJs.jsp" %>
<script type="text/javascript">
seajs.use('main');
</script>
</html>