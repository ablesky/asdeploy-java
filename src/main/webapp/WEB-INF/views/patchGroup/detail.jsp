<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AbleSky代码发布系统</title>
<%@ include file="../include/includeCss.jsp" %>
<style>
.wrapper {
	margin-bottom: 100px;
}
h3.title {
	text-align: left;
}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>
<div class="wrapper">
	<h2 class="title">补丁组详情</h2>
	<div style="width: 400px; text-align: left; margin: 30px auto 10px;">
		<table class="table table-bordered">
			<tbody>
				<tr>
					<td style="width: 40%;">补丁组名称:</td>
					<td>${patchGroup.name}</td>
				</tr>
				<tr>
					<td>工程:</td>
					<td>${patchGroup.project.name}</td>
				</tr>
				<tr>
					<td>创建者:</td>
					<td>${patchGroup.creator.username}</td>
				</tr>
				<tr>
					<td>标识码:</td>
					<td>${patchGroup.checkCode}</td>
				</tr>
				<tr>
					<td>状态:</td>
					<td>
						<c:if test="${'testing' == patchGroup.status}">测试中</c:if>
						<c:if test="${'finished' == patchGroup.status}">已完成</c:if>
					</td>
				</tr>
				<tr>
					<td>创建时间:</td>
					<td><fmt:formatDate value="${patchGroup.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				</tr>
				<tr>
					<td>完成时间:</td>
					<td><fmt:formatDate value="${patchGroup.finishTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;">
						<button id="J_editBtn" class="btn btn-primary" onclick="javascript:void(0);" data-id="${patchGroup.id}">修改</button>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	
	<div style="width: 800px; margin: 10px auto 10px;">
		<h3 class="title">已关联文件列表</h3>
		<table id="fileListTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
			<thead>
				<tr><th>文件路径</th></tr>
			</thead>
			<tbody>
			<c:forEach items="${patchFileList}" var="patchFile">
				<tr>
					<td>${patchFile.filePath}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		<!-- 
		{% if patch_group_conflict_file_list %}
		<h3>文件冲突详情</h3>
		<table id="conflictTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
			<thead>
				<tr>
					<th width="400">文件名</th>
					<th width="60">类型</th>
					<th width="150">冲突组名</th>
					<th width="60">组状态</th>
				</tr>
			</thead>
			<tbody>
			{% for conflict_file_info in patch_group_conflict_file_list %}
				<tr>
					<td>{{conflict_file_info.conflict_patch_file_path}}</td>
					<td>{{conflict_file_info.conflict_patch_file_type}}</td>
					<td>
						<a href="/patchGroupDetail/{{conflict_file_info.conflict_patch_group_id}}/" target="_blank">
						{{conflict_file_info.conflict_patch_group_name}}
						</a>
					</td>
					<td>测试中</td>
				</tr>
			{% endfor %}
			</tbody>
		</table>
		{% endif %}
		 -->
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initUpdatePatchGroupBtn();
});
function initUpdatePatchGroupBtn() {
	$('#J_editBtn').on('click', function(){
		var $this = $(this),
			id = $this.attr('data-id');
			openEditPatchGroupWin(id, {
				width: 450, 
				height: 320,
				url: CTX_PATH + '/patchGroup/edit/' + id
			});
	});
}

function openEditPatchGroupWin(projectId, options) {
	options = options || {};
	var width = options.width || 420,
		height = options.height || 300;
	var screenWidth = window.screen.availWidth,
		screenHeight = window.screen.availHeight,
		left = (screenWidth - width) / 2,
		top = (screenHeight - height) / 2;
	var winConfig = [
		'width=' + width,
		'height=' + height,
		'left=' + left,
		'top=' + top
	].join(',');
	var url = options.url;
	window.open(url, '_blank', winConfig);
}

function reloadPage() {
	location.reload();
}
</script>
</html>