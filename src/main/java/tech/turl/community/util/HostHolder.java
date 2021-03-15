package tech.turl.community.util;

import org.springframework.stereotype.Component;
import tech.turl.community.entity.User;

/**
 * 持有用户信息，用于代替session对象
 *
 * @author zhengguohuang
 * @date 2021/3/15
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
