$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
    // 发送数据后隐藏发布帖子的边框
	$("#publishModal").modal("hide");

	// 获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    // 发送异步请求(POST)，参数分别是访问路径、给服务端的数据以及回调函数，其中参数data是服务端返回给浏览器的数据
    $.post(
        CONTEXT_PATH + "/discuss/add",
    	{"title":title,"content":content},
    	function(data) {
    	    data = $.parseJSON(data);
    	    // 在提示框中显示返回消息
    	    $("#hintBody").text(data.msg);
    	    // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后,自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 刷新页面
                if(data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
    	}
    );
}