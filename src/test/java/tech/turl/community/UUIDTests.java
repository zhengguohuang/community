package tech.turl.community;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author zhengguohuang
 * @date 2021/04/22
 */
public class UUIDTests {
    @Test
    public void testUUID() {
        for (int i = 0; i < 100; i++) {
            System.out.println(UUID.randomUUID());
        }
    }
}
