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
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>
</body>

<div class="wrapper">
	<h2 class="title">发布详情</h2>
	<div style="width: 500px; text-align: center; margin: 30px auto 10px;">
		<table class="table table-bordered">
			<tbody>
				<tr>
					<td style="width: 120px;">发布者:</td>
					<td>${deployRecord.user.username}</td>
				</tr>
				<tr>
					<td>发布类型:</td>
					<td>${deployRecord.deployItem.deployType}</td>
				</tr>
				<tr>
					<td>发布结果:</td>
					<td>
						<c:choose>
							<c:when test="${deployRecord.status == 'prepare' }"><span class="badge">准备中</span></c:when>
							<c:when test="${deployRecord.status == 'uploaded' }"><span class="badge badge-info">已上传</span></c:when>
							<c:when test="${deployRecord.status == 'deploying' }"><span class="badge badge-warning">发布中</span></c:when>
							<c:when test="${deployRecord.status == 'deploy_success' }"><span class="badge badge-success">发布成功</span></c:when>
							<c:when test="${deployRecord.status == 'deploy_failure' }"><span class="badge badge-important">发布失败</span></c:when>
							<c:when test="${deployRecord.status == 'rollbacking' }"><span class="badge badge-warning">回滚中</span></c:when>
							<c:when test="${deployRecord.status == 'rollback_success' }"><span class="badge badge-success">回滚成功</span></c:when>
							<c:when test="${deployRecord.status == 'rollback_failure' }"><span class="badge badge-important">回滚失败</span></c:when>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td>上传文件名:</td>
					<td>${deployRecord.deployItem.fileName}</td>
				</tr>
				<c:if test="${deployRecord.deployItem.patchGroup != null}">
				<tr>
					<td>所属补丁组:</td>
					<td>
						<a href="${ctx_path}/patchGroup/detail/${deployRecord.deployItem.patchGroup.id}" target="_blank">
						${deployRecord.deployItem.patchGroup.name}
						</a>
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	
	<div style="width: 800px; margin: 10px auto 10px;">
		<h3 class="title">README</h3>
		<pre style="height: 300px; overflow: auto; font-size:15px;">${readme}</pre>
		
		<h3 class="title">发布文件列表</h3>
		<table id="J_fileListTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
			<thead>
				<tr><th>文件路径</th></tr>
			</thead>
			<tbody>
			<c:forEach items="${filePathList}" var="filePath">
				<tr>
					<td>${filePath}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		
		<c:if test="${fn:length(conflictDetailList) > 0}" >
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
			<c:forEach items="${conflictDetailList}" var="conflictDetail">
				<c:set var="conflictInfo" value="${conflictDetail.conflictInfo}" />
				<c:set var="relatedPatchGroupStatus" value="${conflictInfo.relatedPatchGroup.status}" />
				<tr>
					<td>${conflictInfo.patchFile.filePath}</td>
					<td>
						<a href="${ctx_path}/patchGroup/detail/${conflictInfo.relatedPatchGroup.id}/" target="_blank">
						${conflictInfo.relatedPatchGroup.name}
						</a>
					</td>
					<td>
						<c:if test="${relatedPatchGroupStatus == 'testing'}">测试中</c:if>
						<c:if test="${relatedPatchGroupStatus == 'finished'}">已完成</c:if>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</c:if>
	</div>
</div>

<%@ include file="../include/includeJs.jsp" %>
<script type="text/javascript">
$(function(){
	highlightConflict();
});

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
</script>
</html>