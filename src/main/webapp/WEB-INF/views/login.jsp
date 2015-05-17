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
.login-wrapper {
	width: 400px; margin: 50px auto;
}
.login-wrapper .alert {
	width: 240px; margin: 0px auto 30px; text-align: center;
}
.login-wrapper table {
	width: 100%;
}
.label-wrapper {
	padding-bottom: 10px; width: 140px; text-align: right;
}
.label-wrapper label {
	font-size: 18px;
}
input[type="text"], input[type="password"] {
	font-size: 16px; width: 140px;
}
.btn-wrapper {
	text-align: center; padding-top: 30px;
}
.btn-wrapper button {
	width: 80px;
}
.btn-wrapper a {
	width: 60px;
}
</style>
</head>
<body>
<%@ include file="./include/header.jsp" %>
<div>
	<shiro:guest>
		<h2 class="title">用户登录</h2>
	</shiro:guest>
	<shiro:user>
		<h2 class="title">确认身份</h2>
	</shiro:user>
	<div class="login-wrapper">
		<c:if test="${not empty successMessage}">
			<div class="alert alert-success">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<span style="margin-left: 40px;">${successMessage}</span>
			</div>
		</c:if>
		<c:if test="${not empty errorMessage}">
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<span style="margin-left: 40px;">${errorMessage}</span>
			</div>
		</c:if>
		<form method="POST" action="${ctx_path}/login">
			<input type="hidden" name="rememberMe" value="on" />
			<table>
				<tbody>
					<tr>
						<td class="label-wrapper">
							<label for="J_username">
								<strong>用户名:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<shiro:guest>
						<td>
							<input type="text" id="J_username" name="username" value="${username}"/>
						</td>
						</shiro:guest>
						<shiro:user>
						<td class="label-wrapper">
							<input type="hidden" id="J_username" name="username" value="<shiro:principal />">
							<label for="J_username" style="text-align: left; padding-left: 5px;">
								<strong><shiro:principal /></strong>
							</label>
						</td>
						</shiro:user>
					</tr>
					<tr>
						<td class="label-wrapper">
							<label for="J_password" style="font-size: 18px;">
								<strong>密&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<td><input type="password" id="J_password" name="password"/></td>
					</tr>
					<tr>
						<td colspan="2" class="btn-wrapper">
							<shiro:guest>
								<button class="btn" type="submit">登&nbsp;&nbsp;录</button>
								<div class="btn-sep">&nbsp;</div>
								<a class="btn" href="${ctx_path}/register">注&nbsp;&nbsp;册</a>
							</shiro:guest>
							<shiro:user>
								<button class="btn btn-primary" type="submit">确&nbsp;&nbsp;认</button>
							</shiro:user>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
</body>
<%@ include file="./include/includeJs.jsp" %>
<script type="text/javascript">
seajs.use('app/common', function(common){
	common.init();
});
</script>
</html>