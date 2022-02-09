package com.erin.community.entity;

/**
 * Created with IntelliJ IDEA.
 * User: erin
 * To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * Description: 封装分页相关的信息
 */

public class Page {

    /*
    * current和limit是浏览器传给服务端的内容
    * rows和path是服务端返回给浏览器的内容
    * */

    // 当前页面的页码
    private int current = 1;
    // 每页显示帖子的数量上限
    private int limit = 10;
    // 帖子的总数(用于计算总页数)
    private int rows;
    // 查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        // 避免当前页码被错误地设置为0或者负数
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        // 限制帖子的数量
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 根据当前页的页码来得到当前页面帖子的起始行（在总行数中）
     *
     * @return
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取页面最下方分页部分的起始页码
     *
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取页面最下方分页部分的结束页码
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }

}
