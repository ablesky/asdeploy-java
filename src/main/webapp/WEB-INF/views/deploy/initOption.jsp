<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择工程</title>
<%@ include file="../include/includeCss.jsp" %>
<style>
.form-horizontal .control-label {
	width: 140px;
	font-size: 16px;
}
#optionWrap {
	width: 320px; 
	font-size: 16px; 
	text-align: center; 
	margin: 0px auto;
}
#optionWrap td {
	text-align: center;
	cursor: pointer;
}

#optionWrap td.active {
	color: #fff;
	font-weight: bold;
	background-color: #ccc;
}
</style>
</head>
<body>
<%@ include file="../include/header.jsp" %>
<div class="wrap">
	<h2 class="title">请选择项目及参数</h2>
	<div style="width: 350px; height: 40px; margin:30px auto 0px;">
	<%-- 	{% if error_msg %}
		<div class="alert alert-error" style="font-size: 16px;">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			{{error_msg}}
		</div>
		{% endif %}
	--%>
	</div>
	<div style="width: 400px; margin: 10px auto;">
		<form class="form-horizontal" method="post" action="." id="deployInitOptionForm">
			<input type="hidden" value="" name="projId" id="project"/>
			<div class="control-group">
				<label class="control-label"><strong>发布类型:</strong></label>
			    <div class="controls">
			    	<select name="deployType" id="deployType" style="width:160px;">
						<!-- option value="">请选择...</option -->
						<option value="patch" selected="selected">补丁</option>
						<option value="war">war包</option>
					</select>
			    </div>
			</div>
			<div class="control-group">
				<label class="control-label"><strong>版本号:</strong></label>
			    <div class="controls">
			    	<input type="text" class="input-medium" id="version" name="version" />
			    </div>
			</div>
			<h3 class="title">选择项目</h3>
			<div>
				<table id="optionWrap" class="table table-bordered table-striped table-hover">
					<tbody>
						<c:forEach var="project" items="${projectList}">
						<tr>
							<td>
								<input type="hidden" value="${project.id}"/>
								${project.name}
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<div style="text-align: center; display: none; margin-top: 30px;">
				<select id="patchGroupSel" name="patchGroupId" style="width: 320px;"></select>
			</div>
			<div style="text-align: center; margin-top: 30px;">
				<button type="button" class="btn btn-primary" id="submitBtn" style="width: 80px;">确&nbsp;&nbsp;定</button>
			</div>
		</form>
	</div>
</div>
</body>
<%@ include file="../include/includeJs.jsp" %>
<script>
$(function(){
	var $tdArr = $('#optionWrap td');
	$tdArr.click(function(){
		$tdArr.removeClass('active');
		var $this = $(this).addClass('active');
		var projectId = $this.children('input:first').val();
		$('#project').val(projectId);
		// 查看是否有补丁组
		if($('#deployType').val() == 'patch') {
			buildPatchGroupSel(projectId);
		}
		
	});
	$('#deployType').change(function(){
		var projectId = $('#project').val();
		if(!projectId || this.value != 'patch'){
			$('#patchGroupSel').parent().hide().end().val('')
			return;
		} else {
			buildPatchGroupSel($('#project').val());
		}
	});
	$('#submitBtn').click(function(){
		if(!submitCheck()){
			return;
		}
		$('#deployInitOptionForm').submit();
	});
});
function buildPatchGroupSel(projectId){
	var $patchGroupSel = $('#patchGroupSel');
	$patchGroupSel.parent().hide().end().empty();
	$.getJSON(CTX_PATH + '/patchGroup/listData', {
		projectId: projectId,
		status: 'testing'
	}, function(data){
		if(data.success !== true || !data.list || !data.list.length){
			return;
		}
		$patchGroupSel.append('<option value="">请选择补丁组...</option>');
		var patchGroups = data.list;
		$.each(patchGroups, function(i, patchGroup){
			$patchGroupSel.append('<option value="' + patchGroup.id + '">' + patchGroup.name + ' -- ' + patchGroup.checkCode + '</option>');
		});
		$patchGroupSel.parent().show();
	});
}
function submitCheck(){
	if(!$('#project').val()){
		alert('请选择项目!');
		return false;
	}
	var $deployType = $('#deployType');
	if(!$deployType.val()){
		alert('请选择发布类型!');
		return false;
	}
	if(!/^\d+(\.\d+)+$/.test($('#version').val())){
		alert('请输入正确的版本号!\n例如 "5.13"');
		return false;
	}
	var $patchGroupSel = $('#patchGroupSel');
	if($deployType.val() == 'patch' 
			&& $patchGroupSel.children().size() 
			&& !$patchGroupSel.val()){
		alert('此工程发布需要选择补丁组!');
		return false;
	}
	return true;
}
</script>
</html>