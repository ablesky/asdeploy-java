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
<div class="wrap">
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
			</tbody>
		</table>
	</div>
	<div style="width: 800px; margin: 20px auto 10px;">
		<table style="width: 800px; margin: 30px auto;">
			<tbody>
				<%-- 只有as-web需要无宕机选项 --%>
				<tr id="J_serverGroupWrap" <c:if test="${project.name != 'as-web'}">style="display: none;"</c:if>>
					<td style="width: 170px; font-size: 16px; padding-bottom: 10px;">
						<strong>无宕机选项:&nbsp;&nbsp;</strong>
					</td>
					<td>
						<select id="serverGroupSel" style="font-size: 16px;">
							<option value="ab" selected="selected">全部</option>
							<option value="a">a组</option>
							<option value="b">b组</option>
						</select>
					</td>
				</tr>
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
			</tbody>
		</table>
		
		<!-- 显示补丁或war的上传结果 -->
		<div id="J_uploadResultWrap" style="text-align: center;"></div>
	
		<c:if test="${deployType == 'patch'}"> <!-- 只有发补丁的时候，需要以下这些栏目 -->
			<div style="text-align: center;">
				<button type="button" class="btn btn-primary" id="J_decompressBtn">解压补丁文件</button>
			</div>
			
			<!-- 文件列表 -->
			<div id="fileListWrap">
				<h3>文件列表</h3>
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
				<h3>冲突详情</h3>
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
			<button type="button" class="btn btn-primary" style="width:100px; margin: 0px 10px;" id="J_startRollbackBtn">回&nbsp;&nbsp;滚</button>
			<div style="margin-top: 20px;" id="J_deployStatus"></div>
		</div>
		
		<!-- 文件冲突列表 -->
		<h3>日志</h3>
		<pre id="J_logContent" style="width: 781px; height: 400px; overflow: auto; font-size: 15px;"></pre>
		<div style="text-align: center; margin: 30px auto;">
			<button type="button" class="btn btn-primary" id="J_unlockAndLeave">解锁并返回首页</button>
		</div>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrapFileInput.js"></script>
<script type="text/javascript" src="${ctx_path}/js/jquery/ajaxfileupload.js"></script>
<script type="text/javascript" src="${ctx_path}/js/jquery/jquery.tmpl.js"></script>
<script>
$(function(){
	initOnBeforeUnload();
	initFileUploadWidget();
	initUnlockAndLeaveBtn();
	initDecompressBtn();
	initStartDeployBtn();
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
	$('#J_uploadBtn').on('click', function(){
		var $this = $(this);
		var deployItemName = $('#J_deployItemField').val();
		if(!deployItemName){
			alert('请先选择要上传的文件!');
			return false;
		}
		// static的情形只能发版本，上传tar.gz包
		if( projectName != 'as-static') {
			if(deployType == 'patch' && !(/.zip$/i).test(deployItemName)){
				alert('请选择zip压缩格式的补丁文件!');
				return false;
			}
			if(deployType == 'war' && !(/.war$/i).test(deployItemName)){
				alert('请选择war包进行上传!');
				return false;
			}
		} else {
			if(!(/.tar.gz/i).test(deployItemName)) {
				alert('请注意，【static】工程只能上传tar.gz包发版本!!!\请不要发补丁!!!');
				return false;
			}
		}
		$this.html('上传中').attr({disabled: true});
		var $uploadResultWrap = $('#J_uploadResultWrap');
		$.ajaxFileUpload({
			url: '/deploy/uploadItem',
			secureuri: false, 
			fileElementId:'J_deployItemField',
			dataType: 'json',
			data: {
				projectId: projectId,
				version: version,
				deployType: deployType,
				deployRecordId: deployRecordId,
				patchGroupId: patchGroupId
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
					return;
				}
				$this.html('上&nbsp;&nbsp;传').attr({disabled: false});
			},
			error: function(data, status, e){
				showAlert($uploadResultWrap, data.message || '文件上传失败!', 'error');
				$this.html('上&nbsp;&nbsp;传').attr({disabled: false});
			}
		});
		return false;
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
			if(data.success !== true) {
				alert('解压缩失败!');
				return;
			}
			$this.html('解压补丁文件').attr({disabled:false});
			renderFilePathList(data.filePathList);
			renderConflictInfoList(data.conflictInfoList);
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

function initStartDeployBtn() {
	$('#J_startDeployBtn').on('click', function(){
		var $this = $(this),
			$deployBtnWrapper = $('#J_deployBtnWrapper');
		$deployBtnWrapper.children('button').attr({disabled: true});
		$this.html('发布中');
		$.post(CTX_PATH + '/deploy/startDeploy', {
			deployRecordId: $('#J_deployRecordId').val(),
			patchGroupId: $('#J_patchGroupId').val(),
			deployManner: 'deploy'
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