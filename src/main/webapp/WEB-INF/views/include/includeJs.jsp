<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript" src="${ctx_path}/js/sea-modules/sea.js"></script>
<script type="text/javascript">
var CTX_PATH = '${ctx_path}';
</script>
<script>
seajs.config({
	base: '/js/',
	paths: {
		'jquery': '/js/jquery',
		'bootstrap': '/js/bootstrap',
		'app': '/js/app'
	},
	alias: {
		'jquery': 'jquery/jquery.js',
		'jquery.tmpl': 'jquery/jquery.tmpl.js',
		'bootstrap': 'bootstrap/bootstrap.js',
		'bootstrap.fileUploadBtn': 'bootstrap/bootstrapFileUploadBtn.js',
		'bootstrap.pageBar': 'bootstrap/bootstrapPageBar.js',
		'bootstrap.switch': 'bootstrap/bootstrapSwitch.js'
	}
});
</script>