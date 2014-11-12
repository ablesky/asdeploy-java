<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑用户</title>
<%@ include file="../../include/includeCss.jsp" %>
<style>
.edit-wrapper {
	margin: 0px auto; width: 400px;
}
.label-wrapper {
	width: 120px;
}
.label-wrapper label {
	font-size: 20px;
	line-height: 25px;
	text-align: right;
}
.input-wrapper input[type="text"], .input-wrapper input[type="password"] {
	font-size:16px;
	margin-bottom:0px;
	width: 95%;
}
.btn-wrapper {
	text-align:center !important;
}
</style>
</head>
<body>
<div class="wrap">
	<h3 class="title">修改用户密码</h3>
	<div class="edit-wrapper">
		<table class="table table-bordered table-condensed" style="width: 100%">
			<tbody id="J_tbody">
				<tr>
					<td class="label-wrapper">
						<label>用户id:</label>
					</td>
					<td class="input-wrapper">
						<input type="text" id="J_userId" disabled="disabled" value="${user.id}"/>
					</td>
				</tr>
				<tr>
					<td class="label-wrapper">
						<label>用户名:</label>
					</td>
					<td class="input-wrapper">
						<input type="text" id="J_username" disabled="disabled" value="${user.username}"/>
					</td>
				</tr>
				<tr>
					<td class="label-wrapper">
						<label>新密码:</label>
					</td>
					<td class="input-wrapper">
						<input type="password" id="J_newPassword"/>
					</td>
				</tr>
				<tr>
					<td class="btn-wrapper" colspan="2">
						<button id="J_saveBtn" class="btn btn-inverse">保存</button>
						<div class="btn-sep">&nbsp;</div>
						<button id="J_closeBtn" class="btn">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<%@include file="../../include/msg/alertModal.jsp" %>
</body>
<%@ include file="../../include/includeJs.jsp" %>
<script>
seajs.use('app/admin/user/changePassword', function(changePassword){
	changePassword.init();
});
</script>
</html>