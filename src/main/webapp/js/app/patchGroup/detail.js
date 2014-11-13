define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initUpdatePatchGroupBtn() {
		$('#J_editBtn').on('click', function(){
			var $this = $(this),
				id = $this.attr('data-id');
			common.openWin ({
				width: 450, 
				height: 320,
				url: CTX_PATH + '/patchGroup/edit/' + id
			});
		});
	}
	
	function highlightConflict(){
		var conflictDict = {};
		$('#J_conflictTbl').children('tbody').find('tr td:nth-child(1)').each(function(i, v){
			conflictDict[v.innerHTML] = true;
		});
		$('#J_fileListTbl').children('tbody').find('tr td:nth-child(1)').each(function(i, v){
			if(conflictDict[v.innerHTML]){
				$(v).parent().addClass('error');
			}
		});
	}
	
	function init() {
		$(function(){
			initUpdatePatchGroupBtn();
			highlightConflict();
		});
	}
	
	module.exports = {init: init};
	
});