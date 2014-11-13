define(function(require, exports, module){
	
	require('bootstrap.pageBar');
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initQueryBtn() {
		$('#J_queryBtn').on('click', function(ev){
			common.submitForm('#J_patchGroupQueryForm', true);
			ev.preventDefault();
		});
	}

	function initClearBtn() {
		$('#J_clearBtn').on('click', function(ev){
			common.clearForm('#J_patchGroupQueryForm', true);
			ev.preventDefault();
		});
	}

	function initCreatePatchGroupBtn() {
		$('#J_createBtn').on('click', function(){
			common.openWin({
				width: 450, 
				height: 290,
				url: CTX_PATH + '/patchGroup/edit'
			});
		});
	}

	function initUpdatePatchGroupBtn() {
		$('#J_tbody').on('click', 'a.edit-btn', function(){
			var $this = $(this),
				id = $this.attr('data-id');
			common.openWin({
				width: 450, 
				height: 320,
				url: CTX_PATH + '/patchGroup/edit/' + id
			});
		});
	}

	function initPageBar() {
		var start = parseInt($('#J_pageStart').val()),
			limit = parseInt($('#J_pageLimit').val()),
			totalCount = parseInt($('#J_pageCount').val());
		common.buildPageBar('#J_pageBar', start, limit, totalCount, function(i, pageNum) {
			start = (pageNum - 1) * limit;
			$('#J_start').val(start);
			$('#J_limit').val(limit);
			common.submitForm('#J_patchGroupQueryForm', true);
		});
	}
	
	function init() {
		$(function(){
			initCreatePatchGroupBtn();
			initUpdatePatchGroupBtn();
			initQueryBtn();
			initClearBtn();
			initPageBar();
		});
	}
	
	module.exports = {init: init};
	
});