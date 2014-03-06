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
.wrapper {
	margin-bottom: 100px;
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
					<td>${deployRecord.status}</td>
				</tr>
				<tr>
					<td>上传文件名:</td>
					<td>${deployRecord.deployItem.fileName}}</td>
				</tr>
				<c:if test="${deployRecord.deployItem.patchGroup != null}">
				<tr>
					<td>所属补丁组:</td>
					<td>
						<a href="/patchGroupDetail/${deployRecord.deployItem.patchGroup.id}" target="_blank">
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
		<table id="fileListTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 100%;">
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
		
		<c:if test="${deployRecord.isConflictWithOthers == true && conflictDetail != null}" >
		<h3 class="title">文件冲突详情</h3>
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
			{% for conflict_info in conflict_detail.conflict_infos.all %}
				<tr>
					<td>{{conflict_info.conflict_patch_file.file_path}}</td>
					<td>{{conflict_info.conflict_patch_file.file_type}}</td>
					<td>
						<a href="/patchGroupDetail/{{conflict_info.conflict_patch_group.id}}/" target="_blank">
						{{conflict_info.conflict_patch_group.name}}
						</a>
					</td>
					<td>
					{% if conflict_info.conflict_patch_group.status == 'created' %}
						已创建
					{% endif %}
					{% if conflict_info.conflict_patch_group.status == 'testing' %}
						测试中
					{% endif %}
					{% if conflict_info.conflict_patch_group.status == 'stoped' %}
						已完成
					{% endif %}
					{% if conflict_info.conflict_patch_group.status == 'finished' %}
						已终结
					{% endif %}
					</td>
				</tr>
			{% endfor %}
			</tbody>
		</table>
		</c:if>
	</div>
</div>

<%@ include file="../include/includeJs.jsp" %>
</html>