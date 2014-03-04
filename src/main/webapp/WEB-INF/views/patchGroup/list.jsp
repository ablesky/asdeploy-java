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
.wrapper {
	margin-bottom: 100px;
}
.title {
	text-align: center;
	font-family: 微软雅黑;
}
.list-wrapper {
	width: 1250px; margin: -10px auto 20px;
}
.list-wrapper .table {
	width: 100%;
}
.list-wrapper th, .list-wrapper td {
	text-align: center;
}
.create-btn-wrapper {
	text-align: center;
	margin: 10px auto 10px;
}
.create-btn-wrapper .btn {
	width: 80px;
}
#J_pageBar {
	margin-top: 10px;
	margin-bottom: 0px;
}
.query-form-wrapper {
	width: 700px; margin: 20px auto 10px;
}
.query-form-wrapper form {
	margin-bottom: 0px;
}
.query-form-wrapper table {
	width: 100%; margin: 0px auto;
}
.query-form-wrapper .label-wrapper {
	text-align: right; width: 20%;
}
.query-form-wrapper .input-wrapper {
	width: 20%;
}
.query-form-wrapper select {
	width: 165px;
}
.query-form-wrapper .btn-wrapper {
	text-align: center;
}
.query-form-wrapper .btn {
	width: 82px;
}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>

<div id="wrapper">
	<h2 class="title">补丁组列表</h2>
	
	<div class="query-form-wrapper">
		<form id="J_patchGroupQueryForm" action="${ctx_path}/patchGroup/list" method="GET">
			<input type="hidden" id="J_start" name="start" value="" />
			<input type="hidden" id="J_limit" name="limit" value="" />
			<table>
				<tr>
					<td class="label-wrapper">
						<label for="J_creatorName">
							<strong>创建者:&nbsp;</strong>
						</label>
					</td>
					<td class="input-wrapper">	
						<input id="J_creatorName" name="creatorName" type="text" class="input-medium" value="${param.creatorName}" />
					</td>
					<td class="label-wrapper">
						<label for="J_patchGroupName">
							<strong>补丁组名:&nbsp;</strong>
						</label>
					</td>
					<td>
						<input id="J_patchGroupName" name="patchGroupName" type="text" class="input-medium" value="${param.patchGroupName}" />
					</td>
				</tr>
				<tr>
					<td class="label-wrapper">
						<label for="J_projectSel">
							<strong>工程:&nbsp;</strong>
						</label>
					</td>
					<td>
						<select id="J_projectSel" name="projectId" class="input-medium">
							<option value="0">全部</option>
							<c:forEach var="project" items="${projectList}">
								<option value="${project.id}" <c:if test="${param.projectId == project.id}">selected="selected"</c:if>>${project.name}</option>
							</c:forEach>
						</select>
					</td>
					<td class="label-wrapper">
						<label for="J_status">
							<strong>状态:&nbsp;</strong>
						</label>
					</td>
					<td>
						<select id="J_status" name="status" class="input-medium">
							<option value="">全部</option>
							<option value="testing" <c:if test="${param.status == 'testing'}">selected="selected"</c:if>>测试中</option>
							<option value="finished" <c:if test="${param.status == 'finished'}">selected="selected"</c:if>>已完成</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="btn-wrapper" colspan="4">
						<button id="J_queryBtn" class="btn btn-primary">&nbsp;查&nbsp;&nbsp;询&nbsp;</button>
						<div class="btn-sep">&nbsp;</div>
						<button id="J_clearBtn" class="btn">清除条件</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<hr/>
	<div class="list-wrapper">
		<div class="row-fluid">
			<div class="span2 create-btn-wrapper">
				<button id="J_createBtn" class="btn btn-primary pull-left">新&nbsp;&nbsp;增</button>
			</div>
			<div class="span10">
				<div id="J_pageBar"></div>
			</div>
		</div>
		<table class="table table-bordered table-condensed table-hover">
			<thead>
				<tr>
					<th width="40">id</th>
					<th width="120">工程名称</th>
					<th width="220">补丁组名称</th>
					<th width="200">标识码</th>
					<th width="100">创建者</th>
					<th width="180">创建时间</th>
					<th width="180">完成时间</th>
					<th width="100">状态</th>
					<th width="110">操作</th>
				</tr>
			</thead>
			<tbody id="J_tbody">
				<c:forEach items="${page.list}" var="patchGroup">
					<tr>
						<td>${patchGroup.id}</td>
						<td>${patchGroup.project.warName }</td>
						<td>${patchGroup.name}</td>
						<td>${patchGroup.checkCode}</td>
						<td>${patchGroup.creator.username}</td>
						<td><fmt:formatDate value="${patchGroup.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${patchGroup.finishTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>${patchGroup.status == 'testing'? '测试中': '已完成'}</td>
						<td>
							<a class="detail-btn" href="javascript:void(0);" data-id="${patchGroup.id}">详情</a>
							&nbsp;&nbsp;
							<a class="edit-btn" href="javascript:void(0);" data-id="${patchGroup.id}">修改</a>
							<!-- 
							&nbsp;&nbsp;
							<a class="delete-btn" href="javascript:void(0);" data-id="${project.id}">删除</a>
							 -->
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrapPageBar.js"></script>
<script>
$(function(){
	initCreatePatchGroupBtn();
	initUpdatePatchGroupBtn();
	initQueryBtn();
	initClearBtn();
	initPageBar();
});

function initQueryBtn() {
	$('#J_queryBtn').on('click', function(){
		$('#J_patchGroupQueryForm').submit();
		/*var params = collectParams('#J_patchGroupQueryForm input, #J_patchGroupQueryForm select');
		var paramArr = [];
		for(var key in params) {
			if(!params[key]) {
				continue;
			}
			paramArr.push(key + '=' + encodeURIComponent(params[key]));
		}
		location.href = CTX_PATH + '/patchGroup/list' + (paramArr.length > 0? '?' + paramArr.join('&'): '');
		*/
	});
}

function initClearBtn() {
	$('#J_clearBtn').on('click', function(){
		$('#J_patchGroupQueryForm input, #J_patchGroupQueryForm select').val('');
		$('#J_patchGroupQueryForm').submit();
	});
}

function initCreatePatchGroupBtn() {
	$('#J_createBtn').on('click', function(){
		openEditPatchGroupWin(0, {
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
			openEditPatchGroupWin(id, {
				width: 450, 
				height: 320,
				url: CTX_PATH + '/patchGroup/edit/' + id
			});
	});
}

function openEditPatchGroupWin(projectId, options) {
	options = options || {};
	var width = options.width || 420,
		height = options.height || 300;
	var screenWidth = window.screen.availWidth,
		screenHeight = window.screen.availHeight,
		left = (screenWidth - width) / 2,
		top = (screenHeight - height) / 2;
	var winConfig = [
		'width=' + width,
		'height=' + height,
		'left=' + left,
		'top=' + top
	].join(',');
	var url = options.url;
	window.open(url, '_blank', winConfig);
}

function initPageBar() {
	var start = parseInt('${page.start}'),
		limit = parseInt('${page.limit}'),
		totalCount = parseInt('${page.count}');
	buildPageBar('#J_pageBar', start, limit, totalCount);
}

function buildPageBar(pageBarEl, start, limit, totalCount){
	pageBarEl = $(pageBarEl);
	if(pageBarEl.size() == 0) {
		return;
	}
	var curPage = Math.floor(start / limit) + 1;
	var totalPage = Math.floor(totalCount / limit) + (totalCount % limit > 0? 1: 0);
	pageBarEl.empty();
	pageBarEl.bootstrapPageBar({
		curPageNum: curPage,
		totalPageNum: totalPage,
		maxBtnNum: 10,
		pageSize: limit,
		siblingBtnNum: 2,
		paginationCls: 'pagination-right',
		click: function(i, pageNum){
			start = (pageNum - 1) * limit;
			$('#J_start').val(start);
			$('#J_limit').val(limit);
			$('#J_patchGroupQueryForm').submit();
		}
	});
}

function reloadPage() {
	location.reload();
}

function collectParams(selector) {
	var params = {};
	if(!selector) {
		return params;
	}
	$(selector).each(function(i, input){
		var $input = $(input);
		var key = $input.attr('name'),
			value = $input.val();
		key && (params[key] = value);
	});
	return params;
}
</script>
</html>