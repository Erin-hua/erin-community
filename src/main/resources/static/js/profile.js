$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) { // 有btn-info样式说明未关注，在profile.html中已经判断了服务器端传来的hasFollowed值并设置了样式，这次点击是为了关注
		// 关注TA，在这里关注的是用户，entityType是3
		$.post(
		    CONTEXT_PATH + "/follow",
		    {"entityType":3,"entityId":$(btn).prev().val()}, // 获得按钮上一节点的值
		    function(data) {
		        data = $.parseJSON(data);
		        if(data.code == 0) { // 成功则刷新页面
                    window.location.reload();
		        } else {
                    alert(data.msg);
		        }
		    }
		);
		// $(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else { // 有btn-secondary样式说明已经关注了，这次点击是为了取消关注
		// 取消关注
		$.post(
		    CONTEXT_PATH + "/unfollow",
		    {"entityType":3,"entityId":$(btn).prev().val()},
		    function(data) {
		        data = $.parseJSON(data);
		        if(data.code == 0) {
                    window.location.reload();
		        } else {
                    alert(data.msg);
		        }
		    }
		);
		//$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
	}
}