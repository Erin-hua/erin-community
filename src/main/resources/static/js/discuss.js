function like(btn, entityType, entityId, entityUserId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                // $(btn)表示获取btn节点，i子节点是得到的赞的数量
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            } else { // 请求失败
                alert(data.msg);
            }
        }
    );
}