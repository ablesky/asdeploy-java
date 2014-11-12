define(function(require, exports, module){
	
	var common = require('app/common').init(),
		$ = require('jquery');
	
	function initToggleQueryTypeBtn() {
		var $queryType = $('#queryType');
		$('#toggleTypeBtn').on('click', 'a', function(){
			var $this = $(this);
			var type = $this.attr('data-type');
			$queryType.html($this.html()).attr('data-value', type);
		});
	}

	function initSelectProjectBtn() {
		var $projectName = $('#projectName');
		$('#selectProjectBtn').on('click', 'a', function(){
			var $this = $(this);
			var projectName = $this.attr('data-value');
			$projectName.html($this.html()).attr('data-value', projectName);
		});
	}

	function initQueryBtn() {
		var $tbody = $('#tbody');
		$('#queryBtn').click(function(){
			var queryType = $('#queryType').attr('data-value') || 'hashcode',
				queryValue = processQueryValue(queryType, $('#queryValue').val()),
				projectName = $('#projectName').attr('data-value') || 'as-web';
			if(!queryValue) {
				common.alertMsg('请输入查询内容!');
				return;
			}
			$('#queryValue').val(queryValue);
			$.getJSON(CTX_PATH + '/ablejs/query', {
				projectName: projectName,
				queryType: queryType,
				queryValue: queryValue
			}, function(data){
				if(!data) {
					fillEmptyResult($tbody);
				} else {
					$tbody.empty();
					var list = data.list;
					if($.isArray(list)) {
						fillQueryResultList(list, $tbody);
					} else {
						fillQueryResult(data, $tbody);
					}
				}
			});
		});
	}

	function processQueryValue(queryType, queryValue) {
		queryValue = processHashcodeQueryValue(queryType, queryValue);
		queryValue = processRelativePathQueryValue(queryType, queryValue);
		return queryValue;
	}

	function processHashcodeQueryValue(queryType, queryValue) {
		if(!queryValue || queryType != 'hashcode') {
			return queryValue;
		}
		if(/^[0-9a-zA-Z]{7,9}$/.test(queryValue)) {
			return queryValue;
		}
		var re = /(?:http.+?(?:images|js_optimize|css_optimize)\/)?(.+)\.(?:js|css|jpg|gif|png|jpeg|ico)/;
		var result = re.exec(queryValue);
		return result? result[1]: queryValue;
	}

	function processRelativePathQueryValue(queryType, queryValue) {
		if(!queryValue || queryType != 'relativePath') {
			return queryValue;
		}
		if(queryValue.indexOf('jsp') >= 0) {
			return queryValue;
		}
		queryValue = queryValue.replace(/\\/g, '/');
		var webrootFlag = 'WebRoot/';
		var webrootPos = queryValue.indexOf(webrootFlag);
		var relativePathBeginPos = webrootPos >= 0? queryValue.indexOf('/', webrootPos + webrootFlag.length): -1;
		return queryValue.substring(relativePathBeginPos + 1);
	}

	function initClearQueryBtn() {
		$('#clearQueryBtn').click(function(){
			$('#queryValue').val('');
			$('#tbody').empty().append('<tr><td colspan="2">请输入hashcode或文件相对路径进行查询</td></tr>');
		});
	}


	function fillEmptyResult($tbody) {
		$tbody.empty().append('<tr><td colspan="2">未查询到任何结果</td></td>');
	}

	function fillQueryResultList(list, $tbody) {
		if(list.length == 0) {
			fillEmptyResult($tbody);
			return;
		}
		$.each(list, function(i, obj){
			fillQueryResult(obj, $tbody);
		});
	}

	function fillQueryResult(data, $tbody) {
		if(!data.identifier && !data.fingerprint) {
			fillEmptyResult($tbody);
			return;
		}
		if(data.identifier) {
			$tbody.append('<tr><td>identifier</td><td>' + data.identifier + '</td></tr>');
		} else if(data.fingerprint) {
			$tbody.append('<tr><td>fingerprint</td><td>' + data.fingerprint + '</td></tr>');
		}
		var dependencies = data.dependencies;
		if(!dependencies || dependencies.length == 0) {
			return;
		}
		var len = dependencies.length;
		var tbodyContentArr = [	
			'<tr>',
				'<td rowspan="' + dependencies.length + '" style="line-height: ' + (len * 34) + 'px">dependencies</td>',
				'<td>' + dependencies[0] + '</td>',
			'</tr>'
		];
		for(var i=1; i < len; i++) {
			tbodyContentArr.push('<tr><td>' + dependencies[i] + '</td></tr>');
		}
		$tbody.append(tbodyContentArr.join(''));
	}
	
	function init() {
		$(function(){
			initQueryBtn();
			initClearQueryBtn();
			initToggleQueryTypeBtn();
			initSelectProjectBtn();
		});
	}
	
	module.exports = {init: init};
	
});