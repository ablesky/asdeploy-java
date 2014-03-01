<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<%@ include file="./include/includeCss.jsp" %>
<style>
/*input[type="text"], input[type="password"] {
	font-size: 16px;
	width: 180px;
}*/	
</style>
</head>
<body>
<%@ include file="./include/header.jsp" %>
<div class="wrap">
	<h2 style="text-align: center;">用户登录</h2>
	<div style="width: 400px; margin: 50px auto;">
	<c:if test="${true}">
		<div class="alert alert-error" style="width: 280px; margin: 0px auto 30px; text-align: center;">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			&nbsp;&nbsp;&nbsp;&nbsp;用户名或密码错误，请重试！
		</div>
	</c:if>
		<form method="POST" action=".">
			<table style="width: 400px;">
				<tbody>
					<tr>
						<td
							style="padding-bottom: 10px; width: 140px; text-align: right;">
							<label for="id_username" style="font-size: 18px;"> <strong>用户名:&nbsp;&nbsp;</strong>
						</label>
						</td>
						<td><input type="text" name="username" style="font-size: 16px; width: 180px;"/></td>
					</tr>
					<tr>
						<td
							style="padding-bottom: 10px; width: 120px; text-align: right;">
							<label for="id_password" style="font-size: 18px;"> <strong>密&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;&nbsp;</strong>
						</label>
						</td>
						<td><input type="password" name="password" style="font-size: 16px; width: 180px;"/></td>
					</tr>
					<tr>
						<td colspan="2" style="text-align: center; padding-top: 30px;">
							<button class="btn" type="submit" style="width: 80px;">登&nbsp;&nbsp;录</button>
							<div style="width: 10px; display: inline-block;">&nbsp;</div>
							<button class="btn" type="button" style="width: 80px;"
								onclick="location.href='/register/'">注&nbsp;&nbsp;册</button>
						</td>
					</tr>
				</tbody>
			</table>
			<input type="hidden" name="next" value="/" />
		</form>
	</div>
</div>
</body>
<%@ include file="./include/includeJs.jsp" %>
<script type="text/javascript">
</script>
</html>