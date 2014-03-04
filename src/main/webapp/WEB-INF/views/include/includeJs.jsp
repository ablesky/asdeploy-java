<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript" src="${ctx_path}/js/jquery/jquery.js"></script>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrap.js"></script>
<%-- <script type="text/javascript" src="${ctx_path}/js/seajs/sea.js"></script> --%>
<script type="text/javascript">
var CTX_PATH = '${ctx_path}';
$(function(){
	intEnvLogoBtn();
});
function intEnvLogoBtn() {
	$('#J_envLogo').on('click', function(){
		var version = $('#J_releasedVersion').val(),
			environment = $('#J_environment').val();
		var str = '代码发布系统 V' + version + '\n'
			+ '当前发布环境 ' + environment;
		alert(str);
	});
}
</script>