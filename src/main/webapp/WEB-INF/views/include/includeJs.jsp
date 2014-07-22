<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script type="text/javascript" src="${ctx_path}/js/jquery/jquery.js"></script>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrap.js"></script>
<%-- <script type="text/javascript" src="${ctx_path}/js/seajs/sea.js"></script> --%>
<script type="text/javascript">
var CTX_PATH = '${ctx_path}';
$.ajaxSetup({
	beforeSend: function(xhr) {
		xhr.setRequestHeader('isAjax', 'true');
	}
});

$(function(){
	intEnvLogoBtn();
});
function intEnvLogoBtn() {
	$('#J_envLogo').on('click', function(){
		var version = $('#J_releasedVersion').val(),
			environment = $('#J_environment').val();
		var str = '代码发布系统 V' + version + '\n'
			+ '当前发布环境 ' + environment;
		alertMsg(str);
	});
}

/**
 * 调用bootstrap样式的弹出框
 */
function alertMsg(msg) {
	var deferred = $.Deferred();
	var width = 350;
	if($.isPlainObject(msg)) {
		width = msg.width || width;
		msg = msg.message;
	}
	var $modal = $('#J_alertModal');
	if($modal.size() == 0) {
		alert(msg);
		return deferred.resolve().promise();
	}
	msg = ('' + msg).replace(/\n/g, '<br/>');
	$modal.find('.modal-body p').html(msg);
	$modal.modal().css({
		width: width,
		'margin-left': function() {
			return - $(this).width() / 2;
		},
		'margin-top': function() {
			return ( $(window).height() - $(this).height() ) / 3;	 // 乱诌的一句，完全没有道理，太神奇了
		}
	});
	$modal.on('hidden', function(){
		$(this).off('hidden');
		deferred.resolve();
	});
	return deferred.promise();
}

/**
 * 调用bootstrap样式的确认框
 */
function confirmMsg(msg) {
	var deferred = $.Deferred();
	var width = 350;
	if($.isPlainObject(msg)) {
		width = msg.width || width;
		msg = msg.message;
	}
	var $modal = $('#J_confirmModal');
	if($modal.size() == 0) {
		return deferred.resolve(confirm(msg)).promise();
	}
	msg = ('' + msg).replace(/\n/g, '<br/>');
	$modal.find('.modal-body p').html(msg);
	$modal.modal().css({
		width: width,
		'margin-left': function() {
			return - $(this).width() / 2;
		},
		'margin-top': function() {
			return ( $(window).height() - $(this).height() ) / 3;	 // 乱诌的一句，完全没有道理，太神奇了
		}
	});
	$modal.on('click', '.modal-footer .confirm', function(){
		$modal.off('click');
		$modal.modal('hide');
		deferred.resolve(true);
	});
	$modal.on('click', '.modal-footer .cancel, .modal-header .close', function(){
		$modal.off('click');
		$modal.modal('hide');
		deferred.resolve(false);
	});
	return deferred.promise();
}
</script>