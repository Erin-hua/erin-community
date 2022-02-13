package com.erin.community.dao;

import com.erin.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Interface.
 * \* Description: message表的mapper，为私信列表和私信详情业务对数据库进行操作
 * \
 */

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表,针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表（用于开发私信详情页面）
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量（可能是当前用户总的未读私信数量或者是当前用户和某个人的当前会话的未读私信数量）
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息的状态，即将未读的私信的状态设置为已读，可能有多条私信未读，因此参数是一个列表
    int updateStatus(List<Integer> ids, int status);
}

