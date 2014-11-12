define(function(require, exports, module){
	
	require('app/common').init();
	var $ = require('jquery');
	
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
			highlightConflict();
		});
	}
	
	module.exports = {init: init};
	
});