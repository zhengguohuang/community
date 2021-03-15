package tech.turl.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import tech.turl.community.dao.LoginTicketMapper;
import tech.turl.community.entity.LoginTicket;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestLoginTicketMapper {

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsert() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setStatus(0);
        loginTicket.setTicket("dddd");
        loginTicket.setExpired(new Date());
        int ret = loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(ret);
    }

    @Test
    public void testSelectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("dddd");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateStatus() {
        int ret = loginTicketMapper.updateStatus("dddd", 1);
        System.out.println(ret);
    }

}
