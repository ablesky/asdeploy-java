define(function(require, exports, module){
	
	var common = require('app/common'),
		$ = require('jquery');
	
	function initSaveBtn() {
		$('#J_saveBtn').on('click', function(){
			var params = common.collectParams('#J_tbody input[type=text]'),
				url = CTX_PATH + '/admin/project/edit';
			$.post(url, params, function(data){
				if(data.success === true) {
					common.alertMsg('操作成功!').done(function(){
						opener.location.reload();
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