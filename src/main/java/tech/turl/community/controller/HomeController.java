package tech.turl.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tech.turl.community.entity.DiscussPost;
import tech.turl.community.entity.Page;
import tech.turl.community.entity.User;
import tech.turl.community.service.DiscussPostService;
import tech.turl.community.service.LikeService;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhengguohuang
 * @date 2021/3/15
 */
@Controller
public class HomeController implements CommunityConstant {
    @Autowired private DiscussPostService discussPostService;
    @Autowired private UserService userService;

    @Autowired private LikeService likeService;

    @GetMapping("/")
    public String getIndexPage(
            Model model,
            Page page,
            @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        // 方法调用栈，SpringMVC会自动实例化Model和Page，并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/?orderMode=" + orderMode);
        List<DiscussPost> list =
                discussPostService.findDiscussPosts(
                        0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }
}
