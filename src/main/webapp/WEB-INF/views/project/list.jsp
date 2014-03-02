<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>工程列表</title>
<%@ include file="../include/includeCss.jsp" %>
<style>
.title {
	text-align: center;
	font-family: 微软雅黑;
}
.list-wrapper {
	width: 800px; margin: 10px auto 20px;
}
.list-wrapper {
	margin-top: 30px;
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
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>

<div>
	<h2 class="title">工程列表</h2>
	<div class="list-wrapper">
		<div class="create-btn-wrapper">
			<button id="J_createBtn" class="btn btn-primary">新&nbsp;&nbsp;增</button>
		</div>
		<table class="table table-bordered table-condensed table-hover">
			<thead>
				<tr>
					<th style="width: 50px;">id</th>
					<th style="width: 300px;">工程名称</th>
					<th style="width: 300px;">包名称</th>
					<th style="width: 150px;">操作</th>
				</tr>
			</thead>
			<tbody id="J_tbody">
				<c:forEach items="${list}" var="project">
					<tr>
						<td>${project.id}</td>
						<td>${project.name}</td>
						<td>${project.warName}</td>
						<td>
							<a class="edit-btn" href="javascript:void(0);" data-id="${project.id}">修改</a>
							&nbsp;&nbsp;
							<a class="delete-btn" href="javascript:void(0);" data-id="${project.id}">删除</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	initCreateProjectBtn();
	initDeleteProjectBtn();
	initUpdateProjectBtn();
});

function initCreateProjectBtn() {
	
}

function initUpdateProjectBtn() {
	$('#J_tbody').on('click', 'a.edit-btn', function(){
		var $this = $(this);
		alert($this.attr('data-id'));
	});
}

function initDeleteProjectBtn() {
	$('#J_tbody').on('click', 'a.delete-btn', function(){
		var $this = $(this);
		$.post(CTX_PATH + '/project/delete/' + $this.attr('data-id'), function(data){
			alert(data.message);
			if(data.success === true) {
				location.reload();
			}
		});
	});
}
</script>
</html>