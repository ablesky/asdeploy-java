define(function(require, exports, module){
	
	var common = require('app/common'),
		$ = require('jquery');
	
	function initSaveBtn() {
		$('#J_saveBtn').on('click', function(){
			var params = {
					userId: $('#J_userId').val(),
					newPassword: $('#J_newPassword').val()
				},
				url = CTX_PATH + '/admin/user/changePassword';
			$.post(url, params, function(data){
				if(data.success === true) {
					common.alertMsg('操作成功!').done(function(){
						window.close();
					});
				} else {
					common.alertMsg(data.message);
				}
			});
		});
	}

	function initCloseBtn() {
		$('#J_closeBtn').on('click', function(){
			window.close();
		});
	}
	
	function init() {
		$(function(){
			initSaveBtn();
			initCloseBtn();
		});
	}
	
	module.exports = {init: init};
	
});