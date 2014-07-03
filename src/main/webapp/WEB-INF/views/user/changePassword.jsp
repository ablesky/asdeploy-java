<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑项目</title>
<%@ include file="../include/includeCss.jsp" %>
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
<div>
	<h3 class="title">修改密码</h3>
	<div class="edit-wrapper">
		<table class="table table-bordered table-condensed" style="width: 100%">
			<tbody id="J_tbody">
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
						<label>原密码:</label>
					</td>
					<td class="input-wrapper">
						<input type="password" id="J_oldPassword"/>
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
					<td class="label-wrapper">
						<label>确认新密码:</label>
					</td>
					<td class="input-wrapper">
						<input type="password" id="J_confirmedNewPassword"/>
					</td>
				</tr>
				<tr>
					<td class="btn-wrapper" colspan="2">
						<button id="J_saveBtn" class="btn btn-primary">保存</button>
						<div class="btn-sep">&nbsp;</div>
						<button id="J_closeBtn" class="btn">关闭</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<div id="J_alertModal" class="modal hide" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		提示
	</div>
	<div class="modal-body" style="text-align: center;">
		<p></p>
	</div>
	<div class="modal-footer">
		<button class="btn btn-primary" data-dismiss="modal" aria-hidden="true">确定</button>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initSaveBtn();
	initCloseBtn();
});

function initSaveBtn() {
	$('#J_saveBtn').on('click', function(){
		var oldPassword = $('#J_oldPassword').val(),
			newPassword = $('#J_newPassword').val(),
			confirmedNewPassword = $('#J_confirmedNewPassword').val();
		if(newPassword != confirmedNewPassword) {
			alertMsg({message: '两次输入的新密码不一致!', width: 250});
			return;
		}
		var params = {
				oldPassword: oldPassword,
				newPassword: newPassword
			},
			url = CTX_PATH + '/user/changePassword';
		$.post(url, params, function(data){
			if(data.success === true) {
				alertMsg({message: '操作成功!', width: 250}).done(function(){
					window.close();
				});
			} else {
				alertMsg({message: data.message, width: 250});
			}
		});
	});
}

function initCloseBtn() {
	$('#J_closeBtn').on('click', function(){
		window.close();
	});
}
</script>
</html>