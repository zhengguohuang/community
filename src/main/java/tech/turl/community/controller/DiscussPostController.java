package tech.turl.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import tech.turl.community.entity.*;
import tech.turl.community.event.EventProducer;
import tech.turl.community.service.CommentService;
import tech.turl.community.service.DiscussPostService;
import tech.turl.community.service.LikeService;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;
import tech.turl.community.util.HostHolder;
import tech.turl.community.util.RedisKeyUtil;

import java.util.*;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired private DiscussPostService discussPostService;
    @Autowired private HostHolder hostHolder;
    @Autowired private UserService userService;
    @Autowired private CommentService commentService;
    @Autowired private LikeService likeService;
    @Autowired private EventProducer eventProducer;
    @Autowired private RedisTemplate redisTemplate;

    /**
     * 添加帖子
     *
     * @param title
     * @param content
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "您还没有登录哦！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 触发发帖事件
        Event event =
                new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(user.getId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        // 计算帖子的分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        // 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(
            @PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);

        model.addAttribute("post", post);
        int userId = post.getUserId();
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);

        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        // 点赞状态
        int likeStatus =
                hostHolder.getUser() == null
                        ? 0
                        : likeService.findEntityLikeStatus(
                                hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList =
                commentService.findCommentsByEntity(
                        ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>(16);
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 点赞状态
                likeStatus =
                        hostHolder.getUser() == null
                                ? 0
                                : likeService.findEntityLikeStatus(
                                        hostHolder.getUser().getId(),
                                        ENTITY_TYPE_COMMENT,
                                        comment.getId());
                commentVo.put("likeStatus", likeStatus);

                // 回复列表
                List<Comment> replyList =
                        commentService.findCommentsByEntity(
                                ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                // 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>(16);
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target =
                                reply.getTargetId() == 0
                                        ? null
                                        : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 点赞数量
                        likeCount =
                                likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus =
                                hostHolder.getUser() == null
                                        ? 0
                                        : likeService.findEntityLikeStatus(
                                                hostHolder.getUser().getId(),
                                                ENTITY_TYPE_COMMENT,
                                                reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount =
                        commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";
    }

    /**
     * 置顶
     *
     * @param id
     * @return
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);
        // 触发发帖事件
        Event event =
                new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 加精
     *
     * @param id
     * @return
     */
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);
        // 触发发帖事件
        Event event =
                new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(id);
        eventProducer.fireEvent(event);

        // 计算帖子的分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, id);

        return CommunityUtil.getJSONString(0);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);
        // 触发发帖事件
        Event event =
                new Event()
                        .setTopic(TOPIC_DELETE)
                        .setUserId(hostHolder.getUser().getId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    /** @author zhengguohuang */
    @GetMapping("/my")
    public String getMyDiscussPost(Model model, Page page) {
        int userId = hostHolder.getUser().getId();
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/discuss/my");
        List<DiscussPost> list =
                discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>(16);
                String title = HtmlUtils.htmlUnescape(post.getTitle());
                String content = HtmlUtils.htmlUnescape(post.getContent());

                post.setTitle(title);
                post.setContent(content);
                map.put("post", post);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("postCount", discussPostService.findDiscussPostRows(userId));
        model.addAttribute("discussPosts", discussPosts);
        return "site/my-post";
    }
}
