define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initStatusSel() {
		var status = $('#J_patchGroupStatus').val();
		status && $('#J_statusSel').val(status);
	}

	function initSaveBtn() {
		$('#J_saveBtn').on('click', function(){
			var params = common.collectParams('#J_tbody input[type=text],#J_tbody input[type=hidden], #J_tbody select'),
				url = CTX_PATH + '/patchGroup/edit';
			$.post(url, params, function(data){
				if(data.success === true) {
					common.alertMsg({message: '操作成功!', width: 250}).done(function(){
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
			initStatusSel();
			initSaveBtn();
			initCloseBtn();
		});
	}
	
	module.exports = {init: init};
	
});