package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger() {
        LOGGER.debug("debug log");
        LOGGER.info("info log");
        LOGGER.warn("warn log");
        LOGGER.error("error log");
    }
}
