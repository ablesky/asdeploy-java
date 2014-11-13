<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript" src="${ctx_path}/js/sea-modules/sea.js"></script>
<script>
var CTX_PATH = '${ctx_path}';
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
		'bootstrap.fileInput': 'bootstrap/bootstrapFileInput.js',
		'bootstrap.fileUploadBtn': 'bootstrap/bootstrapFileUploadBtn.js',
		'bootstrap.pageBar': 'bootstrap/bootstrapPageBar.js',
		'bootstrap.switch': 'bootstrap/bootstrapSwitch.js'
	}
});
</script>