define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initChangePasswordBtn() {
		$('#J_changePasswordBtn').on('click', function(){
			common.openWin({
				url: CTX_PATH + '/user/changePassword',
				height: 300
			});
		});
	}
	
	function init() {
		$(function(){
			initChangePasswordBtn();
		});
	}
	
	module.exports = {init: init};
	
});