<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<%@ include file="./include/includeCss.jsp" %>
<style>
.login-wrapper {
	width: 400px; margin: 50px auto;
}
.login-wrapper .alert {
	width: 280px; margin: 0px auto 30px; text-align: center;
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
	font-size: 16px; width: 180px;
}
.btn-wrapper {
	text-align: center; padding-top: 30px;
}
.btn-wrapper button {
	width: 80px;
}
.btn-sep {
	width: 10px; display: inline-block;
}
</style>
</head>
<body>
<%@ include file="./include/header.jsp" %>
<div>
	<h2 class="title">用户登录</h2>
	<div class="login-wrapper">
		<c:if test="${true}">
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				&nbsp;&nbsp;&nbsp;&nbsp;用户名或密码错误，请重试！
			</div>
		</c:if>
		<form method="POST" action=".">
			<table>
				<tbody>
					<tr>
						<td class="label-wrapper">
							<label for="J_username">
								<strong>用户名:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<td><input type="text" id="J_username" name="username"/></td>
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
							<button class="btn" type="submit">登&nbsp;&nbsp;录</button>
							<div class="btn-sep">&nbsp;</div>
							<button class="btn" onclick="location.href='/register/'">注&nbsp;&nbsp;册</button>
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
</script>
</html>