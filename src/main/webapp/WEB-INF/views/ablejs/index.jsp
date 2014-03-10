<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AbleSky代码发布系统</title>
<%@ include file="../include/includeCss.jsp" %>
<style>
	form label {
		font-size: 16px;
	}
	.table th, .table td {
		text-align: center;
	}
	.table thead {
		background-color: #eee;
	}
	#tbody td, #tbody th {
		font-size:16px;
	}
	#toggleTypeBtn li {
		text-align: left;
		cursor: pointer;
	}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>

<div class="wrap">
	<h2 class="title">静态文件构建依赖</h2>
	<div style="width: 700px; margin: 20px auto 10px;">
		<form id="patch_group_query_form" style="margin-bottom: 0px;" action="." method="post">
			<table style="width: 100%; margin: 0px auto;">
				<tr>
					<td style="text-align: right; width: 30%;">
						<div class="btn-group" style="margin-bottom: 10px;">
							<a class="btn dropdown-toggle" data-toggle="dropdown">
								<span id="queryType" data-value="hashcode">hashcode</span>
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu" id="toggleTypeBtn">
								<li><a data-type="hashcode">hashcode</a></li>
								<li><a data-type="relativePath">相对路径</a></li>
							</ul>
						</div>
					</td>
					<td style="padding-left: 20px;">	
						<input id="queryValue" name="queryValue" type="text" value="" style="width: 300px;" />
					</td>
				</tr>
				<tr>
					<td colspan="4" style="text-align: center;">
						<button id="queryBtn" type="button" class="btn btn-primary" style="width: 82px;">&nbsp;查&nbsp;&nbsp;询&nbsp;</button>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<button id="clearQueryBtn" type="button" class="btn">清除条件</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<hr/>
	<div style="width:800px; margin: 10px auto 20px;">
		<div class="row-fluid">
			<div class="span12">
				<table class="table table-bordered" style="width: 100%;">
					<thead>
						<tr>
							<th colspan="2">查询结果</th>
						</tr>
					</thead>
					<tbody id="tbody">
						<tr><td colspan="2">请输入hashcode或文件相对路径进行查询</td></tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initQueryBtn();
	initClearQueryBtn();
	initToggleQueryTypeBtn();
});

function initToggleQueryTypeBtn() {
	var $queryType = $('#queryType');
	$('#toggleTypeBtn').on('click', 'a', function(){
		var $this = $(this);
		var type = $this.attr('data-type');
		$queryType.html($this.html()).attr('data-value', type);
	});
}

function initQueryBtn() {
	var $tbody = $('#tbody');
	$('#queryBtn').click(function(){
		var queryType = $('#queryType').attr('data-value') || 'hashcode',
			queryValue = processQueryValue(queryType, $('#queryValue').val());
		if(!queryValue) {
			alert('请输入查询内容!');
			return;
		}
		$('#queryValue').val(queryValue);
		$.getJSON(CTX_PATH + '/ablejs/query', {
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
		})
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
	var re = /([0-9a-zA-Z]{7,9})\.(?:js|css|jpg|gif|png|jpeg|ico)/;
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
	var webrootPos = queryValue.indexOf(webrootFlag)
	var relativePathBeginPos = queryValue.indexOf('/', webrootPos + webrootFlag.length);
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
		tbodyContentArr.push('<tr><td>' + dependencies[i] + '</td></tr>')
	}
	$tbody.append(tbodyContentArr.join(''));
}

</script>
</html>