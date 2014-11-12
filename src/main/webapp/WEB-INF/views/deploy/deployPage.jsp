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
.table > thead {
	background-color: #eee;
}
.alert {
	font-size: 16px;
}
#logContent > br {
	font-size: 0px;
	line-height: 0px;
}
h3.title {
	text-align: left;
}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>
<input type="hidden" id="J_deployType" value="${deployType}" />
<input type="hidden" id="J_version"  value="${version}" />
<input type="hidden" id="J_projectId" value="${project.id}" />
<input type="hidden" id="J_projectName" value="${project.name}" />
<input type="hidden" id="J_deployRecordId" value="${deployRecord.id}" />
<input type="hidden" id="J_patchGroupId" value="${patchGroup.id}" />
<div class="wrapper">
	<h2 class="title">发布工程</h2>
	<div style="width: 490px; text-align: left; margin:30px auto 10px;">
		<table class="table table-bordered">
			<tbody>
				<tr>
					<td>发布工程:</td>
					<td>${project.name}</td>
				</tr>
				<tr>
					<td>版本号:</td>
					<td>${version}</td>
				</tr>
				<tr>
					<td>发布方式:</td>
					<td>${deployType}</td>
				</tr>
				<c:if test="${deployType == 'patch' }" >
				<tr>
					<td>补丁分组:</td>
					<td>
					<c:choose>
						<c:when test="${patchGroup != null}">
							<a href="/patchGroup/detail/${patchGroup.id}" target="_blank">
							${patchGroup.name} (${patchGroup.checkCode})
							</a>
						</c:when>
						<c:otherwise>无</c:otherwise>
					</c:choose>
						
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	<div style="width: 800px; margin: 20px auto 10px;">
		<table style="width: 800px; margin: 30px auto 0px;">
			<tbody>
				<%-- 只有as-web需要无宕机选项 --%>
				<c:if test="${project.name == 'as-web'}">
				<tr id="J_serverGroupWrap">
					<td style="width: 170px; font-size: 16px; padding-bottom: 10px;">
						<strong>无宕机选项:&nbsp;&nbsp;</strong>
					</td>
					<td>
						<select id="J_serverGroupSel" style="font-size: 16px;">
							<option value="ab" selected="selected">全部</option>
							<option value="a">a组</option>
							<option value="b">b组</option>
						</select>
					</td>
				</tr>
				</c:if>
				<tr>
					<td style="font-size: 16px; padding-bottom: 10px;">
						<strong>上传文件:&nbsp;&nbsp;</strong>
					</td>
					<td>
						<div style="display: inline-block;" id="J_fileUploadWidget"></div>
						<div style="display:inline-block;">
							<button type="button" id="J_uploadBtn" class="btn btn-primary" style="width: 80px; margin-bottom: 10px;">上&nbsp;&nbsp;传</button>
						</div>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div id="J_uploadProgressBar" class="progress progress-striped" style="width: 582px;">
						</div>
					</td>
				</tr>
				<c:if test="${project.name == 'as-web' and deployType == 'war' }">
				<tr>
					<td style="font-size: 16px; padding-bottom: 10px;">
						<strong>上传静态压缩包:&nbsp;&nbsp;</strong>
					</td>
					<td>
						<div style="display: inline-block;" id="J_staticFileUploadWidget"></div>
						<div style="display:inline-block;">
							<button type="button" id="J_uploadStaticBtn" class="btn btn-primary" style="width: 80px; margin-bottom: 10px;">上&nbsp;&nbsp;传</button>
						</div>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div id="J_uploadStaticProgressBar" class="progress progress-striped" style="width: 582px;" title=""></div>
					</td>
				</tr>
				</c:if>
			</tbody>
		</table>
		
		<!-- 显示补丁或war的上传结果 -->
		<div id="J_uploadResultWrap" style="text-align: center;"></div>
	
		<c:if test="${deployType == 'patch'}"> <!-- 只有发补丁的时候，需要以下这些栏目 -->
			<div style="text-align: center;">
				<button type="button" class="btn btn-primary" id="J_decompressBtn">解压补丁文件</button>
			</div>
			
			<div>
				<h3 class="title">README</h3>
				<pre id="J_readmeContent" style="height: 300px; overflow: auto; font-size:15px;"></pre>
			</div>
			
			<!-- 文件列表 -->
			<div id="fileListWrap">
				<h3 class="title">文件列表</h3>
				<table class="table table-bordered table-condensed table-hover table-striped" style="width: 800px;;">
					<thead>
						<tr><th>文件路径</th></tr>
					</thead>
					<tbody id="J_filePathListTbody">
					</tbody>
					<script type="text/x-jquery-tmpl" id="J_filePathListTmpl">
						<tr>
		 					<td>${'${'}filePath}</td>
		 				</tr>
					</script>
				</table>
			</div>
			
			
			<!-- 文件冲突列表 -->
			<div id="J_conflictFileInfoWrapper">
				<h3 class="title">冲突详情</h3>
				<table id="conflictFileInfoTbl" class="table table-bordered table-condensed table-hover table-striped" style="width: 800px;">
					<thead>
						<tr>
							<th width="600">文件路径</th>
							<th width="200">冲突补丁组</th>
						</tr>
					</thead>
					<tbody id="J_conflictInfoListTbody">
					</tbody>
					<script type="text/x-jquery-tmpl" id="J_conflictInfoListTmpl">
						<tr>
		 					<td>${'${'}filePath}</td>
		 					<td><a href="${ctx_path}/patchGroup/detail/${'${'}relatedPatchGroupId}" target="_blank">${'${'}relatedPatchGroupName}</a></td>
		 				</tr>
					</script>
				</table>
			</div>
		</c:if>
		
		<!-- 发布按钮 -->
		<div id="J_deployBtnWrapper" style="text-align: center;">
			<button type="button" class="btn btn-primary" style="width:100px; margin: 0px 10px;" id="J_startDeployBtn">发&nbsp;&nbsp;布</button>
			<c:if test="${deployType == 'patch' }">
			<button type="button" class="btn btn-primary" style="width:100px; margin: 0px 10px;" id="J_startRollbackBtn">回&nbsp;&nbsp;滚</button>
			</c:if>
			<div style="margin-top: 20px;" id="J_deployStatus"></div>
		</div>
		
		<!-- 文件冲突列表 -->
		<h3 class="title">日志</h3>
		<pre id="J_logContent" style="width: 781px; height: 400px; overflow: auto; font-size: 15px;"></pre>
		<div style="text-align: center; margin: 30px auto;">
			<button type="button" class="btn btn-primary" id="J_unlockAndLeave">解锁并返回首页</button>
		</div>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/deploy/deployPage', function(deployPage){
	deployPage.init();
});
</script>
</html>