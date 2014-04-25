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
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrapFileInput.js"></script>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrapFileUploadBtn.js"></script>
<script type="text/javascript" src="${ctx_path}/js/jquery/jquery.tmpl.js"></script>
<script>
$(function(){
	initOnBeforeUnload();
	initFileUploadWidget();
	initStaticFileUploadWidget();
	initUnlockAndLeaveBtn();
	initDecompressBtn();
	initStartDeployBtn();
	initRollbackDeployBtn();
});
function initFileUploadWidget(){
	var projectName = $('#J_projectName').val(),
		deployType = $('#J_deployType').val(),
		version = $('#J_version').val(),
		projectId = $('#J_projectId').val(),
		deployRecordId = $('#J_deployRecordId').val(),
		patchGroupId = $('#J_patchGroupId').val() || 0;
	
	$('#J_fileUploadWidget').bootstrapFileInput({
		width: '500px',
		btnWidth: '80px',
		fileInputId: 'J_deployItemField',
		fileInputName: 'deployItemField'
	});
	
	var $uploadResultWrap = $('#J_uploadResultWrap');
	
	$('#J_uploadBtn').bootstrapFileUploadBtn({
		progressBar: '#J_uploadProgressBar',
		fileInput: '#J_deployItemField',
		url: '${ctx_path}/deploy/uploadItem',
		data: {
			projectId: projectId,
			version: version,
			deployType: deployType,
			deployRecordId: deployRecordId,
			patchGroupId: patchGroupId 
		},
		validator: function() {
			var deployItemName = $('#J_deployItemField').val();
			if(!deployItemName){
				alert('请先选择要上传的文件!');
				return false;
			}
			if(deployType == 'patch' && !(/.zip$/i).test(deployItemName)){
				alert('请选择zip压缩格式的补丁文件!');
				return false;
			}
			if(deployType == 'war' && !(/.war$/i).test(deployItemName)){
				alert('请选择war包进行上传!');
				return false;
			}
			return true;
		},
		success: function (data, status, ev){
			if(data.success === true){
				var sizeUnits = ['byte', 'kb', 'MB', 'GB']
				var size = data.size;
				for(var i=0; i <=sizeUnits.length && size > 1024; size = (size/1024).toFixed(2), i++);
				var sizeStr = size + sizeUnits[i];
				showAlert($uploadResultWrap, [
					'文件上传成功!',
					'文  件  名: <strong>' + data.filename + '</strong>',
					'文件大小: <strong>' + sizeStr + '</strong>'
				].join('<br/>'), 'success');
			}else{
				this.error(data, status);
				return;
			}
		},
		error: function(data, status, ev){
			showAlert($uploadResultWrap, data.message || '文件上传失败!', 'error');
		}
	});
	
}
/**
 * 仅在as-web发布版本时需要
 */
function initStaticFileUploadWidget(){
	var projectName = $('#J_projectName').val(),
		version = $('#J_version').val(),
		projectId = $('#J_projectId').val(),
		deployType = $('#J_deployType').val();
	if(deployType != 'war' || projectName != 'as-web') {
		return;
	}
	$('#J_staticFileUploadWidget').bootstrapFileInput({
		width: '500px',
		btnWidth: '80px',
		fileInputId: 'J_staticTarFile',
		fileInputName: 'staticTarFile'
	});
	
	var $uploadResultWrap = $('#J_uploadResultWrap');
	
	$('#J_uploadStaticBtn').bootstrapFileUploadBtn({
		progressBar: '#J_uploadStaticProgressBar',
		fileInput: '#J_staticTarFile',
		url: '${ctx_path}/deploy/uploadStaticTar',
		data: {
			projectId: projectId,
			version: version
		},
		validator: function() {
			var deployItemName = $('#J_staticTarFile').val();
			if(!deployItemName){
				alert('请先选择要上传的文件!');
				return false;
			}
			if(!(/.tar(.gz)?$/i).test(deployItemName)){
				alert('请选择tar包进行上传!');
				return false;
			}
			return true;
		},
		success: function (data, status){
			if(data.success === true){
				var sizeUnits = ['byte', 'kb', 'MB', 'GB']
				var size = data.size;
				for(var i=0; i <=sizeUnits.length && size > 1024; size = (size/1024).toFixed(2), i++);
				var sizeStr = size + sizeUnits[i];
				showAlert($uploadResultWrap, [
					'文件上传成功!',
					'文  件  名: <strong>' + data.filename + '</strong>',
					'文件大小: <strong>' + sizeStr + '</strong>'
				].join('<br/>'), 'success');
			}else{
				this.error(data, status);
			}
		},
		error: function(data, status, e){
			showAlert($uploadResultWrap, data.message || '文件上传失败!', 'error');
		}
	});
	
}

function initUnlockAndLeaveBtn() {
	$('#J_unlockAndLeave').on('click', function(){
		if(!confirm('确认要解锁本次发布并离开?')){
			return;
		}
		window.onbeforeunload = null;
		location.href = CTX_PATH + '/deploy/unlockDeployRedirect';
	});
}
function initOnBeforeUnload() {
	window.onbeforeunload = function(){
		var alarmStr = '发布过程中，请不要离开!\n请点击 [ 取消 ] 或 [ 留在此页 ] ';
		var rmozilla = /(mozilla)(?:.*? rv:([\w.]+))?/;
		if(rmozilla.test(navigator.userAgent) === true && !confirm(alarmStr)){
			return false;
		}
		window.event.returnValue = alarmStr;
		return alarmStr;
	};
}

function initDecompressBtn() {
	$('#J_decompressBtn').on('click', function(){
		var $this = $(this);
		$this.html('解&nbsp;压&nbsp;中').attr({disabled:true});
		$.post(CTX_PATH + '/deploy/decompressItem', {
			deployRecordId: $('#J_deployRecordId').val(),
			patchGroupId: $('#J_patchGroupId').val() || 0
		}, function(data){
			$this.html('解压补丁文件').attr({disabled:false});
			if(data.success !== true) {
				alert(data.message || '解压缩失败!');
				return;
			}
			renderFilePathList(data.filePathList);
			renderConflictInfoList(data.conflictInfoList);
			renderReadme(data.readme);
			alert('解压缩成功!');
			return;
		});
	});
}

function renderFilePathList(fileList) {
	if(!$.isArray(fileList)) {
		return;
	}
	fileList = $.map(fileList, function(filePath){return {filePath: filePath};});
	var $filePathListTmpl = $('#J_filePathListTmpl');
	$('#J_filePathListTbody').empty().append($filePathListTmpl.tmpl(fileList));
}

function renderConflictInfoList(conflictInfoList) {
	if(!$.isArray(conflictInfoList)) {
		return;
	}
	var $conflictInfoListTmpl = $('#J_conflictInfoListTmpl');
	$('#J_conflictInfoListTbody').empty().append($conflictInfoListTmpl.tmpl(conflictInfoList));
	highlightConflict();
}

function renderReadme(readme) {
	readme && $('#J_readmeContent').html(readme);
}

function initStartDeployBtn() {
	$('#J_startDeployBtn').on('click', function(){
		var $this = $(this),
			$deployBtnWrapper = $('#J_deployBtnWrapper');
		$deployBtnWrapper.children('button').attr({disabled: true});
		$this.html('发布中');
		$.post(CTX_PATH + '/deploy/startDeploy', {
			deployRecordId: $('#J_deployRecordId').val(),
			patchGroupId: $('#J_patchGroupId').val(),
			deployManner: 'deploy',
			serverGroupParam: $('#J_serverGroupSel').val()
		}, function(data){
			if(!data || data.success !== true) {
				showDeployResultFailed('发布启动失败! ' + (data.message || ''));
				return;
			}
			showDeployInfo('发布启动成功!');
			$('#J_logContent').empty();
			setTimeout(readDeployLogOnRealtime, 1500);
		});
	});
}

function initRollbackDeployBtn() {
	$('#J_startRollbackBtn').on('click', function(){
		var $this = $(this),
			$deployBtnWrapper = $('#J_deployBtnWrapper');
		$deployBtnWrapper.children('button').attr({disabled: true});
		$this.html('回滚中');
		$.post(CTX_PATH + '/deploy/startDeploy', {
			deployRecordId: $('#J_deployRecordId').val(),
			patchGroupId: $('#J_patchGroupId').val(),
			deployManner: 'rollback',
			serverGroupParam: $('#J_serverGroupSel').val()
		}, function(data){
			if(!data || data.success !== true) {
				showDeployResultFailed('回滚启动失败! ' + (data.message || ''));
				return;
			}
			showDeployInfo('回滚启动成功!');
			$('#J_logContent').empty();
			setTimeout(readDeployLogOnRealtime, 1500);
		});
	});
}

function readDeployLogOnRealtime() {
	$.getJSON(CTX_PATH + '/deploy/readDeployLogOnRealtime', {
		deployRecordId: $('#J_deployRecordId').val()
	}, function(data){
		if(!data) {
			alert('日志实时读取出错了!');
			showDeployResultFailed();
			return;
		}
		var $logContent = $('#J_logContent');
		if(data.deployLogContent) {
			$logContent.append(data.deployLogContent);
			$logContent.scrollTop($logContent[0].scrollHeight - $logContent.height());
		}
		if(data.isFinished == true) {
			alert('发布已完成!');
			data.deployResult === true? showDeployResultSuccess(): showDeployResultFailed();
			return;
		}
		setTimeout(readDeployLogOnRealtime, 1500);
	});
}

function showDeployResultSuccess(message) {
	message || (message = '发布成功!');
	showDeployResult(message, 'success');
}

function showDeployResultFailed(message) {
	message || (message = '发布失败，请重新发布!');
	showDeployResult(message, 'error');
}

function showDeployResult(message, status) {
	$('#J_startDeployBtn').html('发布').attr({disabled: false});
	$('#J_startRollbackBtn').html('回滚').attr({disabled: false});
	showDeployInfo(message, status);
}

function showDeployInfo(message, status) {
	status || (status = 'info');
	showAlert('#J_deployStatus', message, status);
}

function showAlert(wrapper, msg, status, closable){
	var $wrapper = $.type(wrapper) == 'string'? $(wrapper): wrapper;
	if($wrapper.size() == 0) {
		return;
	}
	$wrapper.empty();
	var $alert = $('<div class="alert">');
	$alert.html(msg);
	if(closable !== false) {
		$alert.prepend('<button type="button" class="close" data-dismiss="alert">&times;</button>');
	}
	if(status) {
		$alert.addClass('alert-' + status);
	}
	$wrapper.append($alert);
}

function highlightConflict(){
	var conflictDict = {}
	$('#J_conflictInfoListTbody').find('tr td:nth-child(1)').each(function(i, v){
		conflictDict[v.innerHTML] = true;
	});
	$('#J_filePathListTbody').find('tr td:nth-child(1)').each(function(i, v){
		if(conflictDict[v.innerHTML]){
			$(v).parent().addClass('error');
		}
	});
}

</script>
</html>