<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户详情</title>
<%@ include file="../include/includeCss.jsp" %>
</head>
<body>
<%@ include file="../include/header.jsp" %>
<div class="wrapper">
	<div class="title">
		<h1>welcome<shiro:user> <shiro:principal/></shiro:user></h1>
		<div style="margin-top: 100px;"></div>
		<h3><a href="javascript:void(0);" id="J_changePasswordBtn">修改密码</a></h3>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/user/detail', function(detail){
	detail.init();
});
</script>
</html>