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
#toggleTypeBtn li, #selectProjectBtn li {
	text-align: left;
	cursor: pointer;
}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>

<div class="wrapper">
	<h2 class="title">静态文件构建依赖</h2>
	<div style="width: 700px; margin: 20px auto 10px;">
		<form id="patch_group_query_form" style="margin-bottom: 0px;" action="." method="post">
			<table style="width: 100%; margin: 0px auto;">
				<tr>
					<td style="text-align: center; width: 120px;">
						<div class="btn-group" style="margin-bottom: 10px;">
							<a class="btn dropdown-toggle" data-toggle="dropdown" style="width: 80px;">
								<span id="projectName" data-value="as-web">as-web</span>
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu" id="selectProjectBtn">
								<c:forEach var="project" items="${projectList}">
									<li><a data-value="${project.name}">${project.name}</a></li>
								</c:forEach>
							</ul>
						</div>
					</td>
					<td style="text-align: center; width: 120px;">
						<div class="btn-group" style="margin-bottom: 10px;">
							<a class="btn dropdown-toggle" data-toggle="dropdown" style="width: 80px;">
								<span id="queryType" data-value="hashcode">hashcode</span>
								<span class="caret"></span>
							</a>
							<ul class="dropdown-menu" id="toggleTypeBtn">
								<li><a data-type="hashcode">hashcode</a></li>
								<li><a data-type="relativePath">相对路径</a></li>
							</ul>
						</div>
					</td>
					<td style="padding-left: 20px; text-align: left;">	
						<input id="queryValue" name="queryValue" type="text" value="" style="width: 400px;" />
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
<%@include file="../include/msg/alertModal.jsp" %>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
seajs.use('app/ablejs/index', function(index){
	index.init();
});
</script>
</html>