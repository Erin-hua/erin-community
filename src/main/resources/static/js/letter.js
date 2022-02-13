$(function(){
    // jquery通过id定位元素，返回的是js对象
	$("#sendBtn").click(send_letter);
	// jquery通过class定位元素
	$(".close").click(delete_msg);
});

function send_letter() {
    // 发送消息的时候会关闭弹出框（填写要发送消息的弹出框）
	$("#sendModal").modal("hide");

    // 通过id选择器获取js对象并且获取其内容
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
	    CONTEXT_PATH + "/letter/send",
	    {"toName":toName,"content":content}, // 键是服务端响应的方法的参数名
	    function(data) { // data是服务端传给浏览器端的数据
	        data = $.parseJSON(data);
	        if(data.code == 0) {
	            // 通过id选择器获得提示框并且设置其内容，因为不是表单元素而是普通元素，因此调用方法text
	            $("#hintBody").text("发送成功!");
	        } else {
	            $("#hintBody").text(data.msg);
	        }

            // 显示提示框
	        $("#hintModal").modal("show");
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 重载当前页面
                location.reload();
            }, 2000);
	    }
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}