package tech.turl.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    /**
     * 生成随机字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5加密
     * hello -> abc123def456
     * hello + 3e4af -> aof39fjf3ffi
     * @param key
     */
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static void main(String[] args) {
        System.out.println(generateUUID());
        System.out.println(md5("123"));
    }
}
