package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import tech.turl.community.service.AlphaService;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {
    @Autowired
    private AlphaService alphaService;

    @Test
    public void testSave1() {
        Object object = alphaService.save1();
        System.out.println(object);
    }

    @Test
    public void testSave2() {
        Object object = alphaService.save2();
        System.out.println(object);
    }
}
