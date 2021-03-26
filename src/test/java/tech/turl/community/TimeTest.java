package tech.turl.community;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhengguohuang
 * @date 2021/03/25
 */
public class TimeTest {
    public static void main(String[] args) {
        Date date = new Date();//获取当前时间    
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        System.out.println(simpleDateFormat.format(calendar.getTime()));
    }
}
