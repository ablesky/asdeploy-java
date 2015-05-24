define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	require('bootstrap.switch');

	function initCreateProjectBtn() {
		$('#J_createBtn').on('click', function(){
			common.openWin({
				width: 420, 
				height: 240,
				url: CTX_PATH + '/admin/project/edit'
			});
		});
	}

	function initUpdateProjectBtn() {
		$('#J_tbody').on('click', 'a.edit-btn', function(){
			var $this = $(this),
				id = $this.attr('data-id');
			common.openWin({
				width: 420, 
				height: 280,
				url: CTX_PATH + '/admin/project/edit/' + id + '?unauthz_type=simple'
			});
		});
	}

	

	function initDeleteProjectBtn() {
		$('#J_tbody').on('click', 'a.delete-btn', function(){
			var $this = $(this);
			$.post(CTX_PATH + '/admin/project/delete/' + $this.attr('data-id'), function(data){
				common.alertMsg(data.message).done(function(){
					if(data.success === true) {
						location.reload();
					}
				});
			});
		});
	}

	function initDeployScriptTypeSwitch() {
		var states = {};
		$('#J_tbody').on('switch-change', '.switch', function(e, data){
			var $this = $(this),
				prevValue = !data.value,
				projectId = $this.attr('data-id');
			if(states[projectId]) {	// 正在请求中
				return;
			}
			states[projectId] = true;
			var deployScriptType = data.value? 1: 0;	// 1 means the new script while 0 means the old one
			$.ajax({
				url: CTX_PATH + '/admin/project/switch/' + projectId,
				type: 'POST',
				data: {deployScriptType: deployScriptType}
			}).done(function(data){
				data = data || {};
				if(!data.success) {
					$this.bootstrapSwitch('setState', prevValue);
					common.alertMsg(data.message || '操作失败!').done(function(){
						if(data.needLogin === true) {
							location.reload();
						}
					});
					return;
				}
				if(data.deployScriptType !== deployScriptType) {
					$this.bootstrapSwitch('setState', data.deployScriptType); 
				}
			}).fail(function(){
				common.alertMsg('操作失败!');
				$this.bootstrapSwitch('setState', prevValue); 
			}).always(function(){
				states[projectId] = false;
			});
		});
	}
	
	function init() {
		$(function(){
			initCreateProjectBtn();
			initDeleteProjectBtn();
			initUpdateProjectBtn();
			initDeployScriptTypeSwitch();
		});
	}
	
	module.exports = {init: init};
	
});