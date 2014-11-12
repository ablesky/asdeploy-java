define(function(require, exports, module){
	
	require('bootstrap.pageBar');
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initQueryBtn() {
		$('#J_queryBtn').on('click', function(){
			$('#J_deployRecordQueryForm').submit();
		});
	}

	function initClearBtn() {
		$('#J_clearBtn').on('click', function(){
			$('#J_deployRecordQueryForm input, #J_deployRecordQueryForm select').val('');
			$('#J_deployRecordQueryForm').submit();
		});
	}

	function initPageBar() {
		var start = parseInt($('#J_pageStart').val()),
			limit = parseInt($('#J_pageLimit').val()),
			totalCount = parseInt($('#J_pageCount').val());
		common.buildPageBar('#J_pageBar', start, limit, totalCount, function(i, pageNum){
			start = (pageNum - 1) * limit;
			$('#J_start').val(start);
			$('#J_limit').val(limit);
			$('#J_deployRecordQueryForm').submit();
		});
	}
	
	function init() {
		$(function(){
			initQueryBtn();
			initClearBtn();
			initPageBar();
		});
	}
	
	module.exports = {init: init};
	
});