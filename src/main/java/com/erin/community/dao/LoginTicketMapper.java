package com.erin.community.dao;

import com.erin.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: login_ticket的mapper
 * \
 */


// @Deprecated表示该组件不推荐使用
@Mapper
@Deprecated
public interface LoginTicketMapper {

    /*
    * 通过insert注解来说明当前方法对应的insert sql语句
    * useGeneratedKeys设置为true，表明增加login_ticket时mysql会自动生成主键id
    * keyProperty属性设置为id之后，mybatis会从mysql中获取id并且回填给LoginTicket类的属性id
    *
    * */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /*
    * 主要用于退出时修改login_ticket的状态，将状态改为失效
    * 用标签script示范一下动态的sql语句
    * \" 表示转义
    * */
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\"> ",
            "and 1=1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, int status);

}
