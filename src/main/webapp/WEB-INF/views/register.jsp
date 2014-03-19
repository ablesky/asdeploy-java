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
.register-wrapper {
	width: 400px; margin: 50px auto;
}
.register-wrapper .alert {
	width: 220px; margin: 0px auto 10px; text-align: center;
}
.register-wrapper table {
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
</style>
</head>
<body>
<%@ include file="./include/header.jsp" %>
<div class="wrapper">
	<h2 class="title">用户注册</h2>
	<div class="register-wrapper">
		<form method="POST" action="${ctx_path}/register">
			<table>
				<tbody>
					<c:if test="${usernameError != null}">
					<tr>
						<td colspan="2">
							<div class="alert alert-error">${usernameError}</div>
						</td>
					</tr>
					</c:if>
					<tr>
						<td class="label-wrapper" >
							<label for="J_username">
								<strong>用户名:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<td>
							<input type="text" id="J_username" name="username" value="${param.username}"/>
						</td>
					</tr>
					<c:if test="${passwordError != null}">
					<tr>
						<td colspan="2">
							<div class="alert alert-error">${passwordError}</div>
						</td>
					</tr>
					</c:if>
					<tr>
						<td class="label-wrapper">
							<label for="J_password">
								<strong>密&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<td>
							<input type="password" id="J_password" name="password"/>
						</td>
					</tr>
					<c:if test="${confirmedPasswordError != null}">
					<tr>
						<td colspan="2">
							<div class="alert alert-error">${confirmedPasswordError}</div>
						</td>
					</tr>
					</c:if>
					<tr>
						<td class="label-wrapper">
							<label for="J_confirmedPassword">
								<strong>确认密码:&nbsp;&nbsp;</strong>
							</label>
						</td>
						<td>
							<input type="password" id="J_confirmedPassword" name="confirmedPassword"/>
						</td>
					</tr>
					<tr>
						<td colspan="2" class="btn-wrapper">
							<button class="btn" type="submit">注&nbsp;&nbsp;册</button>
							<div style="width: 10px; display: inline-block;">&nbsp;</div>
							<button class="btn" type="button" onclick="window.location.href='${ctx_path}/login/'">返&nbsp;&nbsp;回</button>
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