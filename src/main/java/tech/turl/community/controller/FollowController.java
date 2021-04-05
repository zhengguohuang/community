package tech.turl.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.turl.community.entity.Event;
import tech.turl.community.entity.Page;
import tech.turl.community.entity.User;
import tech.turl.community.event.EventProducer;
import tech.turl.community.service.FollowService;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;
import tech.turl.community.util.HostHolder;

import java.util.List;
import java.util.Map;

/**
 * @author zhengguohuang
 * @date 2021/03/22
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired private HostHolder hostHolder;

    @Autowired private FollowService followService;

    @Autowired private UserService userService;

    @Autowired private EventProducer eventProducer;

    /**
     * 关注
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event =
                new Event()
                        .setTopic(TOPIC_FOLLOW)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(entityType)
                        .setEntityId(entityId)
                        .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注！");
    }

    /**
     * 取消关注
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注！");
    }

    /**
     * 当前用户是否关注id为userId的用户
     *
     * @param userId
     * @return
     */
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

    /**
     * 获取关注的人的列表
     *
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList =
                followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "site/followee";
    }

    /**
     * 获取关注的人的列表
     *
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList =
                followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "site/follower";
    }
}
