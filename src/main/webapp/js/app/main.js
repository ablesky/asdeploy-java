define(function(require, exports, module){
	'use strict';
	require('app/common').init();
	var $ = require('jquery');
	
	exports.initUnlockDeployBtn = function () {
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
	exports.init = function() {
		$(function(){
			this.initUnlockDeployBtn();
		});
	};
});