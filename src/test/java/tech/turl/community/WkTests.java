package tech.turl.community;

import java.io.IOException;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
public class WkTests {
    public static void main(String[] args) throws IOException {
        String cmd =
                "e:/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://baidu.com d:/work/data/wk-images/1.png";
        Runtime.getRuntime().exec(cmd);
        System.out.println("ok");
    }
}
