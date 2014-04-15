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
h3.title {
	text-align: left;
}
#J_conflictTbl th, #J_conflictTbl td {
	text-align: center;
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
						<c:choose>
							<c:when test="${patchGroup.status == 'testing'}"><span class="badge badge-info">测试中</span></c:when>
							<c:when test="${patchGroup.status == 'finished'}"><span class="badge badge-success">已完成</span></c:when>
						</c:choose>
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
				<c:if test="${isSuperAdmin || patchGroup.creator.id == currentUser.id}"> 
				<tr>
					<td colspan="2" style="text-align: center;">
						<button id="J_editBtn" class="btn btn-primary" onclick="javascript:void(0);" data-id="${patchGroup.id}">修改</button>
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	
	<div style="width: 800px; margin: 10px auto 10px;">
		<h3 class="title">已关联文件列表</h3>
		<table id="J_fileListTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
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
		
		<c:if test="${fn:length(conflictInfoList) > 0}">
		<h3 class="title">文件冲突详情</h3>
		<table id="J_conflictTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
			<thead>
				<tr>
					<th width="460">文件名</th>
					<th width="150">冲突组名</th>
					<th width="60">组状态</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${conflictInfoList}" var="conflictInfo">
				<c:set var="relatedPatchGroupStatus" value="${conflictInfo.relatedPatchGroup.status}" />
				<tr>
					<td style="text-align: left;">${conflictInfo.patchFile.filePath}</td>
					<td>
						<a href="${ctx_path}/patchGroup/detail/${conflictInfo.relatedPatchGroupId}" target="_blank">
						${conflictInfo.relatedPatchGroup.name}
						</a>
					</td>
					<td>
						<c:choose>
							<c:when test="${patchGroup.status == 'testing'}"><span class="badge badge-info">测试中</span></c:when>
							<c:when test="${patchGroup.status == 'finished'}"><span class="badge badge-success">已完成</span></c:when>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</c:if>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initUpdatePatchGroupBtn();
	highlightConflict();
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

function highlightConflict(){
	var conflictDict = {}
	$('#J_conflictTbl').children('tbody').find('tr td:nth-child(1)').each(function(i, v){
		conflictDict[v.innerHTML] = true;
	});
	$('#J_fileListTbl').children('tbody').find('tr td:nth-child(1)').each(function(i, v){
		if(conflictDict[v.innerHTML]){
			$(v).parent().addClass('error');
		}
	});
}

function reloadPage() {
	location.reload();
}
</script>
</html>