/**
 * 基于bootstrap样式的上传插件
 * 调用方式如下
$(function(){
	$('#test01').bootstrapFileInput({
		width: '380px',				// 整个控件的宽度
		btnWidth: '80px',			// 按钮的宽度
		fileInputId: 'fileupload01',// 赋予内置的 (input type="file")一个id，以供外部使用
		fileInputName: 'deployItem'	// 赋予内置的 (input type="file")一个name，供表单提交使用
	});
});
 * 
 * @author: zyang
 */
define(function(require){
	var jQuery = require('jquery');

(function($){
	'use strict';
	$.fn.extend({
		bootstrapFileInput: function(options){
			var opts = $.extend({}, $.fn.bootstrapFileInput.defaults, options);
			var $this = this;
			$this.css({
				width: opts.width,
				position: 'relative',
				display: 'inline-block'
			});
			var pathTxtWidth = parseInt(opts.width) - parseInt(opts.btnWidth) - 30 + 'px';
			var $pathTxt = $('<input type="text" />');
			var $browseBtn = $('<button type="button">浏&nbsp;&nbsp;览</button>');
			$pathTxt.css({
					width: pathTxtWidth
			}).attr({
					disabled: 'disabled'
				});
			$browseBtn.addClass('btn btn-primary').css({
				'width': opts.btnWidth,
				'margin-bottom': '10px'
			});
			// safari内核浏览器无法触发file类型的input的click事件，所以只能用透明度为0的方式叠在按钮上
			var rwebkit = /(webkit)[ \/]([\w.]+)/,
				isWebkit = rwebkit.test(navigator.userAgent);
			if(isWebkit){
				var $pathWrap = $('<div>').css({
					display: 'inline-block', 
					width: pathTxtWidth
				});
				var $btnWrap = $('<div>').css({
					display: 'inline-block',
					width: parseInt(opts.btnWidth) + 5 + 'px',
					position: 'relative',
					overflow: 'hidden',
					'margin-left': '23px'
				});
				$pathWrap.append($pathTxt);
				$btnWrap.append($browseBtn);
				buildFileInput($btnWrap, opts);
				$this.append($pathWrap).append($btnWrap);
			} else {
				$browseBtn.css({
					'margin-left': '10px'
				});
				$this.append($pathTxt).append($browseBtn);
				buildFileInput($this, opts);
				$browseBtn.click(function(){
					buildFileInput($this, opts);
					$(this).siblings('input[type=file]').click();
				});
			}
			
			
			/**
			 * input type=file的onchange事件第二次就不管用了
			 * chrome下还是会有问题，但是不影响功能，暂时不管了
			 * 坑确实很深
			 */
			function buildFileInput($wrapper, opts) {
				$wrapper.children('[type=file]').remove();
				var $fileInput = $('<input type="file"/>');
				$fileInput.css({
					'width': opts.btnWidth,
					'position': 'absolute',
					'opacity': 0
				}).attr({
					id: opts.fileInputId,
					name: opts.fileInputName
				});
				if(isWebkit) {
					$fileInput.css({
						right: '5px'
					});
				} else {
					$fileInput.css({
						left: '0px',
						'z-index': -1
					});
				}
				$wrapper.append($fileInput);
				$fileInput.change(function(){
					$this.attr('data-path', this.value);
					$pathTxt.val(this.value);
				});
			}
		}
	});
	$.fn.bootstrapFileInput.defaults = {
		width: '400px',
		btnWidth: '80px',
		fileInputId: '',
		fileInputName: ''
	};
	
})(jQuery);

});