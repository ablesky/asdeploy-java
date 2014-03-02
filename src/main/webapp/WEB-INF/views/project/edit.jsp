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
.input-wrapper input[type="text"] {
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
	<h3 class="title">
		<c:choose>
			<c:when test="${project != null}">修改项目</c:when>
			<c:otherwise>新增项目</c:otherwise>
		</c:choose>
	</h3>
	<div class="edit-wrapper">
		<table class="table table-bordered table-condensed" style="width: 100%">
			<tbody id="J_tbody">
				<c:if test="${project != null}">
				<tr>
					<td class="label-wrapper">
						<label>项目id:</label>
					</td>
					<td class="input-wrapper">
						<input type="text" name="id" disabled="disabled" value="${project.id}"/>
					</td>
				</tr>
				</c:if>
				<tr>
					<td class="label-wrapper">
						<label>项目名称:</label>
					</td>
					<td class="input-wrapper">
						<input type="text" name="name" value="${project.name}"/>
					</td>
				</tr>
				<tr>
					<td class="label-wrapper">
						<label>包名称:</label>
					</td>
					<td class="input-wrapper">
						<input type="text" name="warName" value="${project.warName}"/>
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
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initSaveBtn();
	initCloseBtn();
});

function initSaveBtn() {
	$('#J_saveBtn').on('click', function(){
		var params = collectParams('#J_tbody input[type=text]'),
			url = CTX_PATH + '/project/edit';
		$.post(url, params, function(data){
			if(data.success === true) {
				alert('操作成功!');
				opener.reloadPage();
				window.close();
			} else {
				alert(data.message);
			}
		});
	});
}

function collectParams(selector) {
	var params = {};
	if(!selector) {
		return params;
	}
	$(selector).each(function(i, input){
		var $input = $(input);
		var key = $input.attr('name'),
			value = $input.val();
		key && (params[key] = value);
	});
	return params;
}

function initCloseBtn() {
	$('#J_closeBtn').on('click', function(){
		window.close();
	});
}
</script>
</html>