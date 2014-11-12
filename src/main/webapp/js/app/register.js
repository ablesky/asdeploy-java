define(function(require, exports, module){
	
	require('app/common').init();
	var $ = require('jquery');
	
	function initVerifyImage() {
		$('#J_verifyImage').on('click', function(){
			$(this).attr('src', CTX_PATH + '/jcaptcha.jpg?_=' + $.now());
		});
	}
	
	function init() {
		$(function(){
			$('#J_verifyImage').on('click', function(){
				$(this).attr('src', CTX_PATH + '/jcaptcha.jpg?_=' + $.now());
			});
		});
	}
	
	module.exports = {init: init};
	
});