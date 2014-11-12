define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initDeployOption() {
		var $tdArr = $('#optionWrap td');
		$tdArr.click(function(){
			$tdArr.removeClass('active');
			var $this = $(this).addClass('active');
			//var projectId = $this.children('input:first').val();
			var projectId = $this.attr('data-project-id');
			$('#project').val(projectId);
			// 查看是否有补丁组
			if($('#deployType').val() == 'patch') {
				buildPatchGroupSel(projectId);
			}
			
		});
		$('#deployType').change(function(){
			var projectId = $('#project').val();
			if(!projectId || this.value != 'patch'){
				$('#patchGroupSel').parent().hide().end().val('');
				return;
			} else {
				buildPatchGroupSel($('#project').val());
			}
		});
		$('#submitBtn').click(function(){
			if(!checkBeforeSubmit()){
				return;
			}
			$('#deployInitOptionForm').submit();
		});
	}
	
	function buildPatchGroupSel(projectId){
		var $patchGroupSel = $('#patchGroupSel');
		$patchGroupSel.parent().hide().end().empty();
		$.getJSON(CTX_PATH + '/patchGroup/listData', {
			projectId: projectId,
			status: 'testing'
		}, function(data){
			if(data.success !== true || !data.list || !data.list.length){
				return;
			}
			$patchGroupSel.append('<option value="">请选择补丁组...</option>');
			var patchGroups = data.list;
			$.each(patchGroups, function(i, patchGroup){
				$patchGroupSel.append('<option value="' + patchGroup.id + '">' + patchGroup.name + ' -- ' + patchGroup.checkCode + '</option>');
			});
			$patchGroupSel.parent().show();
		});
	}
	
	function checkBeforeSubmit(){
		if(!$('#project').val()){
			common.alertMsg('请选择项目!');
			return false;
		}
		var $deployType = $('#deployType');
		if(!$deployType.val()){
			common.alertMsg('请选择发布类型!');
			return false;
		}
		if(!/^\d+(\.\d+)+$/.test($('#version').val())){
			common.alertMsg('请输入正确的版本号!\n例如 "5.13"');
			return false;
		}
		var $patchGroupSel = $('#patchGroupSel');
		if($deployType.val() == 'patch' 
				&& $patchGroupSel.children().size() 
				&& !$patchGroupSel.val()){
			common.alertMsg('此工程发布需要选择补丁组!');
			return false;
		}
		return true;
	}
	
	function init() {
		$(function(){
			initDeployOption();
		});
	}
	
	module.exports = {init: init};
	
});