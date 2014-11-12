define(function(require, exports, module){
	
	require('app/common').init();
	var $ = require('jquery');
	
	function initUnlockDeployBtn () {
		$('#J_unlockDeployBtn').on('click', function(){
			$.post(CTX_PATH + '/deploy/unlockDeploy', function(data){
				if(data.success !== true) {
					alertMsg(data.message || '解锁失败!');
				} else {
					location.reload();
				}
			});
		});
	};
	
	function init () {
		$(function(){
			initUnlockDeployBtn();
		});
	};
	
	module.exports = {init: init};
	
});