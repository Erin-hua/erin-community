package com.erin.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: erin
 * \* To change this template use File | Settings | Editor | File and Code Templates | File | Class.
 * \* Description: 工具类
 * \
 */

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        // 生成的随机字符串一般由字母和'-'构成，在这里去掉'-'
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /*
    * MD5加密，只能加密不能解密
    * hello -> abc123def456
    * hello + 3e4a8 -> abc123def456abc
    *
    * */
    public static String md5(String key) {
        // 如果key是null或""或" "，都会被判定会空
        if (StringUtils.isBlank(key)) {
            return null;
        }

        // Spring自带的工具，把key加密成16进制的字符串并返回
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 返回JSON格式的字符串
     * @param code 编号
     * @param msg 提示
     * @param map 封装了业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "erin");
        map.put("age", 23);
        System.out.println(getJSONString(0, "ok", map));
    }

}

