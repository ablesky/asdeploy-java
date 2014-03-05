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
#J_pageBar {
	margin-top: 10px;
	margin-bottom: 10px;
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

<div class="wrapper">
	<h2 class="title">发布历史列表</h2>
	
	<div class="query-form-wrapper">
		<form id="J_deployRecordQueryForm" action="${ctx_path}/deployRecord/list" method="GET">
			<input type="hidden" id="J_start" name="start" value="" />
			<input type="hidden" id="J_limit" name="limit" value="" />
			<table>
				<tr>
					<td class="label-wrapper">
						<label for="J_username">
							<strong>用户名:&nbsp;</strong>
						</label>
					</td>
					<td class="input-wrapper">	
						<input id="J_username" name="username" type="text" class="input-medium" value="${param.username}" />
					</td>
					<td class="label-wrapper">
						<label for="J_deployType">
							<strong>发布类型:&nbsp;</strong>
						</label>
					</td>
					<td>
						<select id="J_deployType" name="deployType" class="input-medium">
							<option value="">全部</option>
							<option value="patch" <c:if test="${param.deployType == 'patch'}">selected="selected"</c:if>>补丁</option>
							<option value="war" <c:if test="${param.deployType == 'war'}">selected="selected"</c:if>>war包</option>
						</select>
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
						<label for="J_version">
							<strong>版本:&nbsp;</strong>
						</label>
					</td>
					<td>
						<input id="J_version" name="version" type="text" class="input-medium" value="${param.version}" />
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
			<div class="span2">
			</div>
			<div class="span10">
				<div id="J_pageBar"></div>
			</div>
		</div>
		<table class="table table-bordered table-condensed table-hover">
			<thead>
				<tr>
					<th width="40">id</th>
					<th width="100">用户名</th>
					<th width="180">发布日期</th>
					<th width="120">项目名称</th>
					<th width="80">版本</th>
					<th width="80">类型</th>
					<th width="400">文件名</th>
					<th width="100">发布状态</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody id="J_tbody">
				<c:forEach items="${page.list}" var="deployRecord">
					<tr>
						<td>${deployRecord.id}</td>
						<td>${deployRecord.user.username}</td>
						<td><fmt:formatDate value="${deployRecord.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
						<td>${deployRecord.project.name}</td>
						<td>${deployRecord.deployItem.version}</td>
						<td>${deployRecord.deployItem.deployType}</td>
						<td>${deployRecord.deployItem.fileName}</td>
						<td>${deployRecord.status}</td>
						<td>
							<a class="detail-btn" href="${ctx_path}/deployRecord/detail/${deployRecord.id}">详情</a>
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
	initQueryBtn();
	initClearBtn();
	initPageBar();
});

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
			$('#J_deployRecordQueryForm').submit();
		}
	});
}
</script>
</html>