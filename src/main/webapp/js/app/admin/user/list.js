define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initSuperAdminSwitchCheckbox() {
		$('input.superadmin-switch-cbx').on('change', function(){
			var self = this,
				isSuperAdmin = self.checked;
			$.post(CTX_PATH + '/admin/user/switchSuperAdmin', {
				userId: $(self).attr('data-id'),
				isSuperAdmin: isSuperAdmin
			}, function(data){
				if(data.success !== true) {
					common.alertMsg(data.message).done(function(){
						if(data.needLogin === true) {
							location.reload();
						}
					});
					self.checked = !isSuperAdmin;	// 此处有些不稳
				}
			});
		});
	}

	function initEditUserBtn() {
		$('.edit-btn').on('click', function(){
			var $this = $(this);
			common.openWin({
				url: CTX_PATH + '/admin/user/changePassword/' + $this.attr('data-id') + '?unauthz_type=simple',
				height: 260
			});
		});
	}
	
	function init() {
		$(function(){
			initSuperAdminSwitchCheckbox();
			initEditUserBtn();
		});
	}
	
	module.exports = {init: init};
});