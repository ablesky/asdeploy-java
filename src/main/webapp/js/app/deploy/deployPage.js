define(function(require, exports, module){
	
	require('jquery.tmpl');
	require('bootstrap.fileInput'); 
	require('bootstrap.fileUploadBtn');
	var $ = require('jquery'),
		common = require('app/common').init();
	
	function initFileUploadWidget(){
		var deployType = $('#J_deployType').val(),
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
			url: CTX_PATH + '/deploy/uploadItem',
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
					common.alertMsg('请先选择要上传的文件!');
					return false;
				}
				if(deployType == 'patch' && !(/.zip$/i).test(deployItemName)){
					common.alertMsg('请选择zip压缩格式的补丁文件!');
					return false;
				}
				if(deployType == 'war' && !(/.war$/i).test(deployItemName)){
					common.alertMsg('请选择war包进行上传!');
					return false;
				}
				return true;
			},
			success: function (data, status, ev){
				if(data.success === true){
					var sizeUnits = ['byte', 'kb', 'MB', 'GB'];
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
			url: CTX_PATH + '/deploy/uploadStaticTar',
			data: {
				projectId: projectId,
				version: version
			},
			validator: function() {
				var deployItemName = $('#J_staticTarFile').val();
				if(!deployItemName){
					common.alertMsg('请先选择要上传的文件!');
					return false;
				}
				if(!(/.tar(.gz)?$/i).test(deployItemName)){
					common.alertMsg('请选择tar包进行上传!');
					return false;
				}
				return true;
			},
			success: function (data, status){
				if(data.success === true){
					var sizeUnits = ['byte', 'kb', 'MB', 'GB'];
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
			common.confirmMsg('确认要解锁本次发布并离开?').done(function(result){
				if(!result) {
					return;
				}
				window.onbeforeunload = null;
				location.href = CTX_PATH + '/deploy/unlockDeployRedirect';
			});
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
					common.alertMsg(data.message || '解压缩失败!');
					return;
				}
				renderFilePathList(data.filePathList);
				renderConflictInfoList(data.conflictInfoList);
				renderReadme(data.readme);
				common.alertMsg('解压缩成功!');
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
				common.alertMsg('日志实时读取出错了!');
				showDeployResultFailed();
				return;
			}
			var $logContent = $('#J_logContent');
			if(data.deployLogContent) {
				$logContent.append(data.deployLogContent);
				$logContent.scrollTop($logContent[0].scrollHeight - $logContent.height());
			}
			if(data.isFinished == true) {
				blurTitle('【发布已完成】');
				common.alertMsg('发布已完成!');
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
		var conflictDict = {};
		$('#J_conflictInfoListTbody').find('tr td:nth-child(1)').each(function(i, v){
			conflictDict[v.innerHTML] = true;
		});
		$('#J_filePathListTbody').find('tr td:nth-child(1)').each(function(i, v){
			if(conflictDict[v.innerHTML]){
				$(v).parent().addClass('error');
			}
		});
	}

	function blurTitle(msg, title) {
		title || (title = document.title);
		if(!document.hidden){
			document.title = title;
			return;
		}
		document.title = document.title == title? msg: title;
		setTimeout(function(){
			blurTitle(msg, title);
		}, 500);
	}
	
	function init() {
		$(function(){
			initOnBeforeUnload();
			initFileUploadWidget();
			initStaticFileUploadWidget();
			initUnlockAndLeaveBtn();
			initDecompressBtn();
			initStartDeployBtn();
			initRollbackDeployBtn();
		});
	}
	
	module.exports = {init: init};
	
});