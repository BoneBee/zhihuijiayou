package com.ncse.zhhygis.utils.baseUtils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @Author: zhangweixia
 * @Description:    自定义存储token工具类
 * @Date：Created in 13:34 2019/11/19
 * @Modified：
 */
public class TokenUtil {
    //token信息放入缓存
    private static Map<String, String> map = new ConcurrentHashMap<>();

    public static void setMap(Map<String, String> map) {
        TokenUtil.map = map;
    }

    public static Map<String, String> getMap() {
        synchronized (map){
            return map;
        }

    }
}
