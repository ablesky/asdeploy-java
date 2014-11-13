define(function(require, exports, module){
	
	require('bootstrap.pageBar');
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initQueryBtn() {
		$('#J_queryBtn').on('click', function(ev){
			common.submitForm('#J_deployRecordQueryForm', true);
			ev.preventDefault();
		});
	}

	function initClearBtn() {
		$('#J_clearBtn').on('click', function(ev){
			common.clearForm('#J_deployRecordQueryForm', true);
			ev.preventDefault();
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
			common.submitForm('#J_deployRecordQueryForm', true);
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