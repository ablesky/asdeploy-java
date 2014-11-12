define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initSaveBtn() {
		$('#J_saveBtn').on('click', function(){
			var oldPassword = $('#J_oldPassword').val(),
				newPassword = $('#J_newPassword').val(),
				confirmedNewPassword = $('#J_confirmedNewPassword').val();
			if(newPassword != confirmedNewPassword) {
				common.alertMsg({message: '两次输入的新密码不一致!', width: 250});
				return;
			}
			var params = {
					oldPassword: oldPassword,
					newPassword: newPassword
				},
				url = CTX_PATH + '/user/changePassword';
			$.post(url, params, function(data){
				if(data.success === true) {
					common.alertMsg({message: '操作成功!', width: 250}).done(function(){
						window.close();
					});
				} else {
					common.alertMsg({message: data.message, width: 250});
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